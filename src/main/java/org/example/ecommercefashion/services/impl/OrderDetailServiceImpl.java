package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.OrderDetailCreateRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
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

        countQuantity(request.getQuantity(), productDetail.getQuantity());

        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);
        detail.setProductDetail(productDetail);
        detail.setQuantity(request.getQuantity());
        detail.setPrice(productDetail.getPrice());
        detail.setTotalMoney(request.getQuantity() * productDetail.getPrice());
        detail.setCreatedBy(user.getId());

        return repository.save(detail);
    }

    private void countQuantity(Integer requestQuantity, Integer productQuantity){
        if (requestQuantity > productQuantity) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm chỉ còn " + productQuantity);
        }
    }
}
