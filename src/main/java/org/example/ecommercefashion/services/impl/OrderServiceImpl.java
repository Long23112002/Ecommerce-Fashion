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
    public Order createOrder(OrderCreateRequest dto, String token) {
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
        order.setFinalPrice(order.getTotalMoney());
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order updateAddress(Long id, OrderAddressUpdate dto) {
        Order order = getOrderById(id);
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

        double finalPrice = (order.getTotalMoney() - order.getDiscountAmount()) + moneyShip;
        if (finalPrice < 0) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
        order.setFinalPrice(finalPrice);

        orderRepository.save(order);
        return order;
    }

    @Override
    public String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws JobExecutionException {
        TransactionStrategy strategy = (TransactionStrategy) applicationContext.getBean(dto.getPaymentMethod().getVal());
        Order order = getOrderById(id);
        order.setFullName(dto.getFullName());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setAddress(updateAddress(dto, order));
        if (!validateAddress(order)) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_ADDRESS);
        }
        order.setNote(dto.getNote());
        String redirect = strategy.processPayment(order);
        orderRepository.save(order);
        return redirect;
    }

    @Override
    public Order updateDiscount(Long id, Long discountId) {
        Order order = getOrderById(id);
        DiscountResponse discount = discountService.getByDiscountId(discountId);
        order.setDiscountId(discountId);
        Double total = order.getTotalMoney();
        if (discount.getType() == TypeDiscount.PERCENTAGE) {
            Double value = discount.getValue();
            Double discountAmount = Math.min(total * (value / 100), discount.getMaxValue());
            Double finalPrice = total - discountAmount;
            order.setDiscountAmount(discountAmount);
            order.setFinalPrice(finalPrice + order.getMoneyShip());
        } else {
            Double discountAmount = discount.getValue();
            total -= discountAmount;
            order.setDiscountAmount(discountAmount);
            order.setFinalPrice(total + order.getMoneyShip());
        }
        return orderRepository.save(order);
    }

    @Override
    public Order confirmOrder(TransactionRequest request) throws JobExecutionException {
        Order order = getOrderById(request.getOrderId());
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
        emailJob.orderSuccessfulEmail(order);
        return orderRepository.save(order);
    }

    @Override
    public Order updateStateOrder(Long id, OrderChangeState dto) {
        Order order = getOrderById(id);
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

        return orderRepository.save(order);
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
    public Order getOrderById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
    }

    @Override
    public Page<Order> filter(OrderParam param, Pageable pageable) {
        return orderRepository.filter(param, pageable);
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
        if (orderRepository.countOrderPendingStore() >= 4) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Đã đạt giới hạn lượng hóa đơn chờ");
        }
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_AT_STORE);
        order.setStaffId(userJWT.getUserId());
        order.setFullName("Khách lẻ");

        order.setPaymentMethod(PaymentMethodEnum.CASH);
        order = orderRepository.save(order);

        return order;
    }

    @Override
    public List<Order> getOrderPendingAtStore(String token) {
        return orderRepository.findPendingOrders(OrderStatus.PENDING_AT_STORE);
    }
}
