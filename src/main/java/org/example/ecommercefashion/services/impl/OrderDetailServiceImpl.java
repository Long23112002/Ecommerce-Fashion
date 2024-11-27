package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.example.ecommercefashion.dtos.request.OrderDetailCreateRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.OrderDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository repository;
    private final OrderRepository orderRepository;
    private final ProductDetailRepository productDetailRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
    }

    @Override
    public Page<OrderDetail> filter(Long orderId, Pageable pageable) {
        return repository.filter(orderId, pageable);
    }

    @Override
    public OrderDetail addProductDetailToOrderDetail(OrderDetailCreateRequest request, String token) {
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        Order order = orderRepository
                .findById(request.getIdOrder())
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
        ProductDetail productDetail = productDetailRepository
                .findById(request.getIdProductDetail())
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));

        OrderDetail existedOrderDetail = repository
                .findOrderDetailByOrderAndProductDetail(order, productDetail)
                .orElse(null);

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng nhập vào không hợp lệ");
        }

        if (existedOrderDetail != null) {
            updateOrderDetail(existedOrderDetail, request.getQuantity(), productDetail, user);
            repository.save(existedOrderDetail);
        } else {
            countQuantity(request.getQuantity(), productDetail.getQuantity());
            OrderDetail orderDetail = addNewOrderDetail(order, user, productDetail, request.getQuantity());
            repository.save(orderDetail);
        }

        updateOrderTotalMoney(order);
        return existedOrderDetail != null ? existedOrderDetail :
                repository.findOrderDetailByOrderAndProductDetail(order, productDetail).orElseThrow();

    }
    private void countQuantity(Integer requestQuantity, Integer productQuantity) {
        if (requestQuantity > productQuantity) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm chỉ còn " + productQuantity);
        }
    }
    private OrderDetail addNewOrderDetail(Order order, User user, ProductDetail productDetail, Integer quantity){
        OrderDetail newDetail = new OrderDetail();
        newDetail.setCode("HDCT" + repository.getLastValue());
        newDetail.setOrder(order);
        newDetail.setProductDetail(productDetail);
        newDetail.setQuantity(quantity);
        newDetail.setPrice(productDetail.getPrice());
        newDetail.setTotalMoney(quantity * productDetail.getPrice());
        newDetail.setCreatedBy(user.getId());
        return newDetail;
    }

    private OrderDetail updateOrderDetail(OrderDetail orderDetail, Integer quantity, ProductDetail productDetail, User user){
        int newTotalQuantity = orderDetail.getQuantity() + quantity;
        if (newTotalQuantity > productDetail.getQuantity()) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng tổng vượt quá số lượng sản phẩm có sẵn");
        }
        orderDetail.setQuantity(newTotalQuantity);
        orderDetail.setTotalMoney(newTotalQuantity * productDetail.getPrice());
        orderDetail.setUpdatedBy(user.getId());
        return orderDetail;
    }

    private void updateOrderTotalMoney(Order order) {
        List<OrderDetail> orderDetails = repository.findAllByOrder(order);
        double totalMoney = orderDetails.stream()
                .mapToDouble(OrderDetail::getTotalMoney) // Lấy tổng tiền từ mỗi OrderDetail
                .sum();
        order.setTotalMoney(totalMoney); // Cập nhật lại tổng tiền
        orderRepository.save(order);
    }

    private List<OrderDetail> getListOrderDetail(Long id){
        return repository.getAllByOrderId(id);
    }
    @Override
    public MessageResponse deleteOrderDetail(Long id) {
        OrderDetail detail =
                repository
                        .findById(id)
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order detail"));
        Order order = detail.getOrder();
        if (order == null) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Không tìm thấy hóa đơn liên quan");
        }

        Double updatedTotalMoney = order.getTotalMoney() - detail.getTotalMoney();
        order.setTotalMoney(Math.max(0, updatedTotalMoney));

        detail.setDeleted(true);
        repository.save(detail);
        orderRepository.save(order);
        return MessageResponse.builder().message("Order detail deleted successfully").build();

    }

}
