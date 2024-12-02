package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.response.CartValueResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailCartResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.CartValueInfo;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.CartRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    @Transactional
    public Cart create(CartRequest cartRequest) {
        if (cartRepository.existsByUserId(cartRequest.getUserId())) {
            return getCartByUserId(cartRequest.getUserId());
        } else {
            Cart cart = new Cart();
            FnCommon.coppyNonNullProperties(cart, cartRequest);
            cart.setCartValues(cartRequest.getCartValues());
            return cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public Cart update(CartRequest cartRequest, String token) {
        JwtResponse jwtResponse = jwtService.decodeToken(token);
        Long userId = jwtResponse.getUserId();
        return updateCart(cartRequest, userId);
    }

    @Override
    @Transactional
    public Cart update(CartRequest cartRequest, Long userId) {
        return updateCart(cartRequest, userId);
    }

    private Cart updateCart(CartRequest cartRequest, Long userId) {
        Cart existingCart = getCartByUserId(userId);

        Set<Long> requestedProductIds = extractRequestedProductIds(cartRequest);
        List<CartValue> updatedCartValues =
                updateExistingCartValues(existingCart, cartRequest, requestedProductIds);

        addNewCartValues(existingCart, cartRequest, updatedCartValues);

        existingCart.setCartValues(updatedCartValues);
        Cart savedCart = cartRepository.save(existingCart);
        setCartValueInfos(savedCart);

        return savedCart;
    }

    @Override
    @Transactional
    public void delete(String token) {
        JwtResponse jwtResponse = jwtService.decodeToken(token);
        Cart cart = getCartById(jwtResponse.getUserId());
        cart.setCartValues(new ArrayList<>());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart getCartByUserId(Long userId) {
        synchronized (this) {
            return cartRepository
                    .getFirstByUserId(userId)
                    .orElseGet(() -> {
                        CartRequest request = CartRequest.builder()
                                .userId(userId)
                                .cartValues(new ArrayList<>())
                                .build();
                        return create(request);
                    });
        }
    }

    @Override
    public CartValueResponse validCart(List<CartValue> request) {
        CartValueResponse response = new CartValueResponse();
        response.setValid(true);
        Map<Long, Integer> cartValueMap = request.stream()
                .collect(Collectors.toMap(
                        r -> r.getProductDetailId(),
                        r -> r.getQuantity()
                ));
        Set<Long> productDetailIds = request.stream()
                .map(req -> req.getProductDetailId())
                .collect(Collectors.toSet());
        List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailIds);
        List<CartValue> cartValues = productDetails.stream()
                .map(pd -> {
                    long id = pd.getId();
                    int quantity = cartValueMap.get(id);
                    if (pd.getQuantity() < quantity) {
                        response.setValid(false);
                        return new CartValue(id, pd.getQuantity());
                    }
                    return new CartValue(id, quantity);
                })
                .toList();
        response.setCartValues(cartValues);
        return response;
    }

    @Override
    public List<CartValueInfo> getCartValueInfos(List<CartValue> values) {
        return values.stream()
                .map(value -> {
                    int valueQuantity = value.getQuantity();
                    ProductDetailCartResponse productDetail =
                            productDetailRepository
                                    .findById(value.getProductDetailId())
                                    .map(entity ->{
                                        return ProductDetailCartResponse.builder()
                                                .id(entity.getId())
                                                .price(entity.getPrice())
                                                .images(entity.getImages())
                                                .product(entity.getProduct())
                                                .size(entity.getSize())
                                                .color(entity.getColor())
                                                .quantity(entity.getQuantity())
                                                .build();
                                    })
                                    .orElse(null);
                    int productQuantity = productDetail.getQuantity();
                    int quantity = Math.min(productQuantity,valueQuantity);
                    return new CartValueInfo(quantity, productDetail);
                })
                .toList();
    }

    private void setCartValueInfos(Cart cart) {
        cart.setCartValueInfos(getCartValueInfos(cart.getCartValues()));
    }

    private Cart getCartById(Long id) {
        return cartRepository
                .findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, "Không tìm thấy giỏ hàng"));
    }

    private Set<Long> extractRequestedProductIds(CartRequest cartRequest) {
        return cartRequest.getCartValues().stream()
                .map(CartValue::getProductDetailId)
                .collect(Collectors.toSet());
    }

    private List<CartValue> updateExistingCartValues(
            Cart existingCart, CartRequest cartRequest, Set<Long> requestedProductIds) {
        List<CartValue> updatedCartValues = new ArrayList<>();

        for (CartValue existingValue : existingCart.getCartValues()) {
            if (requestedProductIds.contains(existingValue.getProductDetailId())) {
                CartValue requestedValue =
                        cartRequest.getCartValues().stream()
                                .filter(
                                        reqVal ->
                                                reqVal.getProductDetailId().equals(existingValue.getProductDetailId()))
                                .findFirst()
                                .orElse(null);
                if (requestedValue != null) {
                    ProductDetail pd = productDetailRepository.getById(requestedValue.getProductDetailId());
                    if (requestedValue.getQuantity() > pd.getQuantity()) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_ENOUGH);
                    }
                    existingValue.setQuantity(requestedValue.getQuantity());
                }
                updatedCartValues.add(existingValue);
            }
        }
        return updatedCartValues;
    }

    private void addNewCartValues(Cart existingCart,
                                  CartRequest cartRequest,
                                  List<CartValue> updatedCartValues) {
        for (CartValue newValue : cartRequest.getCartValues()) {
            boolean exists = existingCart.getCartValues().stream()
                    .anyMatch(val -> val.getProductDetailId().equals(newValue.getProductDetailId()));
            if (!exists) {
                updatedCartValues.add(newValue);
            }
        }
    }
}