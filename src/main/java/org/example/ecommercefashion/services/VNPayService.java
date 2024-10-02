package org.example.ecommercefashion.services;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface VNPayService {

  String createPayment(HttpServletRequest request, long amountRequest)
      throws UnsupportedEncodingException;

  ResponseEntity<?> paymentSuccess(String status);
}
