package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.Order;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface VNPayService {

  String createPayment(HttpServletRequest request, long amountRequest, long orderId)
      throws UnsupportedEncodingException;

  ResponseEntity<?> paymentSuccess(String status);

  boolean match(Order order, String secure);
}
