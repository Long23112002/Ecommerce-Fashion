package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
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

        countQuantity(request.getQuantity(), productDetail.getQuantity());

        OrderDetail existedOrderDetail = repository
                .findOrderDetailByOrderAndProductDetail(order, productDetail)
                .orElse(null);

        if (existedOrderDetail != null) {

            // Cập nhật số lượng và tổng tiền nếu sản phẩm đã tồn tại
            int newQuantity = existedOrderDetail.getQuantity() + request.getQuantity();
            existedOrderDetail.setQuantity(newQuantity);
            existedOrderDetail.setTotalMoney(newQuantity * productDetail.getPrice());
            existedOrderDetail.setUpdatedBy(user.getId());
            return repository.save(existedOrderDetail);
        } else {

            // Tạo mới hóa đơn chi tiết nếu sản phẩm chưa tồn tại
            OrderDetail newDetail = new OrderDetail();
            newDetail.setCode("HDCT" + repository.getLastValue());
            newDetail.setOrder(order);
            newDetail.setProductDetail(productDetail);
            newDetail.setQuantity(request.getQuantity());
            newDetail.setPrice(productDetail.getPrice());
            newDetail.setTotalMoney(request.getQuantity() * productDetail.getPrice());
            newDetail.setCreatedBy(user.getId());
            return repository.save(newDetail);
        }
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
        detail.setDeleted(true);
        repository.save(detail);
        return MessageResponse.builder().message("Order detail deleted successfully").build();

    }

    private void countQuantity(Integer requestQuantity, Integer productQuantity) {
        if (requestQuantity > productQuantity) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm chỉ còn " + productQuantity);
        }
    }
}
