package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.PaymentRequest;
import org.example.ecommercefashion.entities.Payment;

public interface PaymentService {

  Payment createPayment(PaymentRequest request);

  Payment getPaymentById(Long id);

  Payment updatePayment(Long id, PaymentRequest request);
}
