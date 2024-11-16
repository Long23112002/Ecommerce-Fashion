package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailCartResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.CartValueInfo;
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
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Người dùng đã có giỏ hàng");
        } else {
            Cart cart = new Cart();
            FnCommon.coppyNonNullProperties(cart, cartRequest);
            return cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public Cart update(CartRequest cartRequest, String token) {
        JwtResponse jwtResponse = jwtService.decodeToken(token);
        Cart existingCart = getCartByUserId(jwtResponse.getUserId());

        Set<Long> requestedProductIds = extractRequestedProductIds(cartRequest);
        Set<CartValue> updatedCartValues =
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
        cart.setCartValues(new HashSet<>());
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository
                .getCartByUserId(userId)
                .orElseGet(()->{
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .cartValues(new HashSet<>())
                            .cartValueInfos(new HashSet<>())
                            .build();
                    return cartRepository.save(newCart);
                });
        setCartValueInfos(cart);
        return cart;
    }

    private void setCartValueInfos(Cart cart) {
        cart.setCartValueInfos(
                cart.getCartValues().stream()
                        .map(
                                value -> {
                                    ProductDetailCartResponse productDetail =
                                            productDetailRepository
                                                    .findById(value.getProductDetailId())
                                                    .map(
                                                            productDetailEntity ->
                                                                    new ProductDetailCartResponse(
                                                                            productDetailEntity.getId(),
                                                                            productDetailEntity.getPrice(),
                                                                            productDetailEntity.getImages(),
                                                                            productDetailEntity.getProduct(),
                                                                            productDetailEntity.getSize(),
                                                                            productDetailEntity.getColor(),
                                                                            productDetailEntity.getOriginPrice(),
                                                                            productDetailEntity.getQuantity()
                                                                    ))
                                                    .orElse(null);
                                    return new CartValueInfo(value.getQuantity(), productDetail);
                                })
                        .collect(Collectors.toSet()));
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

    private Set<CartValue> updateExistingCartValues(
            Cart existingCart, CartRequest cartRequest, Set<Long> requestedProductIds) {
        Set<CartValue> updatedCartValues = new HashSet<>();

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
                    existingValue.setQuantity(requestedValue.getQuantity());
                }
                updatedCartValues.add(existingValue);
            }
        }
        return updatedCartValues;
    }

    private void addNewCartValues(
            Cart existingCart, CartRequest cartRequest, Set<CartValue> updatedCartValues) {
        for (CartValue newValue : cartRequest.getCartValues()) {
            boolean exists =
                    existingCart.getCartValues().stream()
                            .anyMatch(val -> val.getProductDetailId().equals(newValue.getProductDetailId()));
            if (!exists) {
                updatedCartValues.add(newValue);
            }
        }
    }
}