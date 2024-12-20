package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.example.ecommercefashion.dtos.request.OrderDetailCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderDetailUpdateRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.OrderDetailService;
import org.example.ecommercefashion.services.ProductDetailService;
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
    private final ProductDetailService productDetailService;
    private final DiscountRepository discountRepository;
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
        ProductDetail productDetail = productDetailService.detail(request.getIdProductDetail());

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

    @Override
    public OrderDetail updateProductDetailToOrderDetail(OrderDetailUpdateRequest request, String token) {
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        OrderDetail orderDetail =
                repository.findById(request.getOrderDetailId())
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_DETAIL_NOT_FOUND));
        int quantity = request.getQuantity();
        ProductDetail productDetail = productDetailService.detail(orderDetail.getProductDetail().getId());
        if (quantity > productDetail.getQuantity()) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm chỉ còn " + productDetail.getQuantity());
        }
        orderDetail.setQuantity(quantity);
        orderDetail.setTotalMoney(quantity * productDetail.getPrice());
        orderDetail.setUpdatedBy(user.getId());
        orderDetail = repository.save(orderDetail);
        Order order = orderDetail.getOrder();
        updateOrderTotalMoney(order);
        return orderDetail;
    }

    private void countQuantity(Integer requestQuantity, Integer productQuantity) {
        if (requestQuantity > productQuantity) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm chỉ còn " + productQuantity);
        }
    }

    private OrderDetail addNewOrderDetail(Order order, User user, ProductDetail productDetail, Integer quantity) {
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

    private OrderDetail updateOrderDetail(OrderDetail orderDetail, Integer quantity, ProductDetail productDetail, User user) {
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
        if (order.getDiscountId() != null) {
            Discount discount = discountRepository.findById(order.getDiscountId()).orElse(null);
            if (discount != null) {
                if (totalMoney <= 0 || (discount.getType() == TypeDiscount.FIXED_AMOUNT && totalMoney<discount.getValue())) {
                    removeDiscount(order);
                }
            } else {
                removeDiscount(order);
            }
        }
        orderRepository.save(order);
    }

    private List<OrderDetail> getListOrderDetail(Long id) {
        return repository.getAllByOrderId(id);
    }

    @Override
    public OrderResponse deleteOrderDetail(Long id) {
        OrderDetail detail =
                repository
                        .findById(id)
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order detail"));
        Order order = detail.getOrder();
        if (order == null) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Không tìm thấy hóa đơn liên quan");
        }

        Double updatedTotalMoney = Math.max(0, (order.getTotalMoney() - detail.getTotalMoney()));
        order.setTotalMoney(updatedTotalMoney);

        if (order.getDiscountId() != null) {
            Discount discount = discountRepository.findById(order.getDiscountId()).orElse(null);
            if (discount != null) {
                if (updatedTotalMoney <= 0 || (discount.getType() == TypeDiscount.FIXED_AMOUNT && updatedTotalMoney<discount.getValue())) {
                    removeDiscount(order);
                }
            } else {
                removeDiscount(order);
            }
        }

        repository.delete(detail);
        orderRepository.save(order);
        return toDto(order);
    }

    private OrderResponse toDto(Order entity) {
        UserResponse user = FnCommon.copyNonNullProperties(UserResponse.class, entity.getUser());
        OrderResponse response = FnCommon.copyProperties(OrderResponse.class, entity);
        response.setUser(user);
        response.setPayAmount((entity.getTotalMoney() - entity.getDiscountAmount()) + entity.getMoneyShip());
        response.setRevenueAmount(entity.getTotalMoney() - entity.getDiscountAmount());
        return response;
    }

    private void removeDiscount(Order order) {
        order.setDiscountId(null);
        order.setDiscountAmount(0.0);
    }

}
