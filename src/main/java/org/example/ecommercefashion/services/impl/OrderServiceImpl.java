package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.request.GhtkOrderRequest;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.OrderLog;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.OrderDetailValue;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CartService;
import org.example.ecommercefashion.services.DiscountService;
import org.example.ecommercefashion.services.GhtkService;
import org.example.ecommercefashion.services.OrderLogService;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.services.ProductDetailService;
import org.example.ecommercefashion.strategies.TransactionDTO;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.example.ecommercefashion.strategies.TransactionStrategy;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ApplicationContext applicationContext;
    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductDetailService productDetailService;
    private final UserRepository userRepository;
    private final GhtkService ghtkService;
    private final OrderLogService orderLogService;
    private final DiscountService discountService;
    private final EmailJob emailJob;
    private final CartService cartService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest dto, String token) {
        Order order = new Order();
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        order.setStatus(OrderStatus.DRAFT);
        order.setTotalMoney(calculateTotalOrderMoney(dto.getOrderDetails()));
        order.setUser(user);
        order.setAddress(new Address());
        order.setPaymentMethod(PaymentMethodEnum.CASH);
        order = orderRepository.save(order);
        order.setOrderDetails(createOrderDetailsWithStockDeduction(dto.getOrderDetails(), order));
        order = orderRepository.save(order);
        return toDto(order);
    }

    @Override
    public OrderResponse updateAddress(Long id, OrderAddressUpdate dto) {
        Order order = getById(id);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_NOT_IN_DRAFT);
        }
        int quantity = order.getOrderDetails().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        GhtkOrderRequest ghtkReq = GhtkOrderRequest.builder()
                .totalMoney(order.getTotalMoney())
                .quantity(quantity)
                .toDistrictId(dto.getDistrictID())
                .toWardCode(dto.getWardCode())
                .build();

        GhtkFeeResponse ghtkRes = ghtkService.getShippingFee(ghtkReq);
        double moneyShip = ghtkRes.getData().getService_fee();
        order.setMoneyShip(moneyShip);
        order.setAddress(Address.builder()
                .districtID(dto.getDistrictID())
                .districtName(dto.getDistrictName())
                .provinceID(dto.getProvinceID())
                .provinceName(dto.getProvinceName())
                .wardCode(dto.getWardCode())
                .wardName(dto.getWardName())
                .specificAddress((order.getAddress().getSpecificAddress()))
                .build());

        order = orderRepository.save(order);
        return toDto(order);
    }

    @Override
    public String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws JobExecutionException {
        TransactionStrategy strategy = (TransactionStrategy) applicationContext.getBean(dto.getPaymentMethod().getVal());
        Order order = getById(id);
        order.setFullName(dto.getFullName());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setAddress(updateAddress(dto, order));
        order.setNote(dto.getNote());
        if (!validateAddress(order)) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_ADDRESS);
        }
        if(order.getTotalMoney()-order.getMoneyShip()<0) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_PAY_AMOUNT);
        }
        String redirect = strategy.processPayment(toDto(order));
        orderRepository.save(order);
        return redirect;
    }

    @Override
    public OrderResponse updateDiscount(Long id, Long discountId) {
        Order order = getById(id);
        DiscountResponse discount = discountService.getByDiscountId(discountId);
        order.setDiscountId(discountId);
        Double total = order.getTotalMoney();
        if (discount.getType() == TypeDiscount.PERCENTAGE) {
            Double value = discount.getValue();
            Double discountAmount = Math.min(total * (value / 100), discount.getMaxValue());
            order.setDiscountAmount(discountAmount);
        } else {
            Double discountAmount = discount.getValue();
            order.setDiscountAmount(discountAmount);
        }
        return toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse confirmOrder(TransactionRequest request) throws JobExecutionException {
        Order order = getById(request.getOrderId());
        TransactionStrategy strategy = (TransactionStrategy) applicationContext.getBean(request.getPaymentMethod().getVal());
        TransactionDTO dto = TransactionDTO.builder()
                .order(order)
                .confirmationCode(request.getConfirmationCode())
                .status(request.getStatus())
                .build();
        order = strategy.confirmPayment(dto);
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetail.getProductDetail().getId());
            productDetailService.handleMinusQuantity(orderDetail.getQuantity(), productDetail);
        }
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        updateCartAfterPayment(order);
        OrderResponse response = toDto(orderRepository.save(order));
        emailJob.orderSuccessfulEmail(response);
        return response;
    }

    @Override
    public OrderResponse updateStateOrder(Long id, OrderChangeState dto) {
        Order order = getById(id);
        Order prev = order.clone();


        order.setStatus(dto.getStatus());
        if (dto.getAddress() != null) {
            order.setAddress(dto.getAddress());
            order.setTotalMoney(dto.getTotalMoney());
        }
        if (dto.getPhoneNumber() != null) {
            order.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getNote() != null) {
            order.setNote(dto.getNote());
        }
        if (dto.getPaymentMethod() != null) {
            order.setPaymentMethod(dto.getPaymentMethod());
        }

        orderLogService.create(OrderLog.builder()
                .newValue(order.getStatus())
                .oldStatus(prev.getStatus())
                .order(order)
                .user(order.getUser())
                .build());

        return toDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        Order order =
                orderRepository
                        .findById(id)
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderDetail.setDeleted(true);
            orderDetailRepository.save(orderDetail);
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return toDto(getById(id));
    }

    @Override
    public Page<OrderResponse> filter(OrderParam param, Pageable pageable) {
        return orderRepository.filter(param, pageable).map(this::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<OrderDetail> createOrderDetailsWithStockDeduction(
            List<OrderDetailValue> orderDetailValues, Order order) {
        validateStockAvailability(orderDetailValues);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailValue orderDetailValue : orderDetailValues) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(orderDetailValue.getQuantity());
            orderDetail.setPrice(productDetail.getPrice());
            orderDetail.setTotalMoney(productDetail.getPrice() * orderDetailValue.getQuantity());
            orderDetail.setOrder(order);

            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return orderDetails;
    }

    public Double calculateTotalOrderMoney(List<OrderDetailValue> orderDetails) {
        double totalMoney = 0;
        for (OrderDetailValue orderDetailValue : orderDetails) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());
            totalMoney += productDetail.getPrice() * orderDetailValue.getQuantity();
        }

        return totalMoney;
    }

    private void validateStockAvailability(List<OrderDetailValue> orderDetailValues) {
        for (OrderDetailValue orderDetailValue : orderDetailValues) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());

            int availableQuantity = productDetail.getQuantity();
            int requestedQuantity = orderDetailValue.getQuantity();

            if (requestedQuantity > availableQuantity) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
            }
        }
    }

    private User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
    }

    private Address updateAddress(OrderUpdateRequest dto, Order order) {
        Address address = order.getAddress();
        address.setSpecificAddress(dto.getSpecificAddress());
        return address;
    }

    private boolean validateAddress(Order order) {
        Address address = order.getAddress();
        return address.getProvinceID() != null
                && address.getDistrictID() != null
                && address.getWardCode() != null
                && address.getSpecificAddress() != null;
    }

    private void updateCartAfterPayment(Order order) {
        Long userId = order.getUser().getId();
        Cart cart = cartService.getCartByUserId(userId);

        Map<Long, CartValue> mapCartValue = order.getOrderDetails()
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getProductDetail().getId(),
                        o -> CartValue.builder()
                                .productDetailId(o.getProductDetail().getId())
                                .quantity(o.getQuantity())
                                .build()
                ));

        List<CartValue> cartValues = cart.getCartValues();

        Set<CartValue> newCartValues =
                cartValues.stream()
                        .map(c -> {
                            CartValue cv = mapCartValue.get(c.getProductDetailId());
                            if (cv == null) {
                                return c;
                            }
                            Integer quantity = c.getQuantity() - cv.getQuantity();
                            if (quantity <= 0) {
                                return null;
                            }
                            c.setQuantity(quantity);
                            return c;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        CartRequest request = CartRequest.builder()
                .userId(userId)
                .cartValues(newCartValues)
                .build();
        cartService.update(request, userId);
    }

    @Override
    public Order createOrderAtStore(String token) {
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        if (orderRepository.countOrderPendingStore(user.getId()) >= 4) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Đã đạt giới hạn lượng hóa đơn chờ");
        }
        Order order = new Order();
        order.setCode("HD" + orderRepository.getLastValue());
        order.setStatus(OrderStatus.PENDING_AT_STORE);
        order.setStaffId(user.getId());
        order.setFullName("Khách lẻ");

        order.setPaymentMethod(PaymentMethodEnum.CASH);
        order = orderRepository.save(order);

        return order;
    }

    @Override
    public List<OrderResponse> getOrderPendingAtStore(String token) {
        List<Order> responses = orderRepository.findPendingOrders(OrderStatus.PENDING_AT_STORE);
        return toDtos(responses);
    }

    private Order getById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
    }

    private List<OrderResponse> toDtos(Collection<Order> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    private OrderResponse toDto(Order entity) {
        return OrderResponse.builder()
                .id(entity.getId())
                .discountId(entity.getDiscountId())
                .user(entity.getUser())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .shipdate(entity.getShipdate())
                .note(entity.getNote())
                .moneyShip(entity.getMoneyShip())
                .discountAmount(entity.getDiscountAmount())
                .totalMoney(entity.getTotalMoney())
                .revenueAmount(entity.getTotalMoney() - entity.getDiscountAmount())
                .payAmount((entity.getTotalMoney() - entity.getDiscountAmount()) + entity.getMoneyShip())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deleted(entity.getDeleted())
                .staffId(entity.getStaffId())
                .orderDetails(entity.getOrderDetails())
                .orderLogs(entity.getOrderLogs())
                .code(entity.getCode())
                .build();
    }
}
