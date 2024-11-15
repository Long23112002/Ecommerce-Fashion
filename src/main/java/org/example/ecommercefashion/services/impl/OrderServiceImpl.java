package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.GhtkOrderRequest;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.entities.value.OrderDetailValue;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.GhtkService;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.services.PaymentService;
import org.example.ecommercefashion.services.ProductDetailService;
import org.example.ecommercefashion.services.VNPayService;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GhtkService ghtkService;

    @Autowired
    private EmailJob emailJob;

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
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order updateAddress(Long id, OrderAddressUpdate dto) {
        Order order = getOrderById(id);

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
                .build());

        double finalPrice = order.getTotalMoney()+moneyShip;
        if(finalPrice<0){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
        order.setFinalPrice(finalPrice);

        orderRepository.save(order);
        return order;
    }

    @Override
    public String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws UnsupportedEncodingException {
        Order order = orderRepository.getById(id);
        order.setFullName(dto.getFullName());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setAddress(updateAddress(dto, order));
        order.setNote(dto.getNote());
        orderRepository.save(order);
//        long finalPrice = order.getFinalPrice().longValue();
        long finalPrice = 2000;
        return vnPayService.createPayment(httpServletRequest, finalPrice, order.getId());
    }

    @Override
    public Order updateStateOrder(Long id, OrderChangeState dto) {
        Order order = getOrderById(id);
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
        return orderRepository.save(order);
    }

    @Override
    public Order confirm(Long orderId, String encode, String status) throws JobExecutionException {
        Order order = getOrderById(orderId);
        boolean match = vnPayService.match(order, encode);
        if(!status.equals("00")){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PAYMENT_FAILED);
        }
        if(!match){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.SECURE_NOT_MATCH);
        }
        order.setPaymentMethod(PaymentMethodEnum.VNPAY);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        emailJob.OrdersuccessfulEmail(order);
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
            productDetailService.handleMinusQuantity(orderDetailValue.getQuantity(), productDetail);

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
}