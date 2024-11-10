package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import org.example.ecommercefashion.dtos.request.PaymentRequest;
import org.example.ecommercefashion.entities.Payment;
import org.example.ecommercefashion.repositories.PaymentRepository;
import org.example.ecommercefashion.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Autowired private PaymentRepository paymentRepository;

  @Override
  public Payment createPayment(PaymentRequest request) {
    Payment payment = new Payment();
    FnCommon.coppyNonNullProperties(payment, request);
    return paymentRepository.save(payment);
  }

  @Override
  public Payment getPaymentById(Long id) {
    return paymentRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ExceptionHandle(
                    HttpStatus.BAD_REQUEST, "Không tìm thấy phương thức thanh toán"));
  }

  @Override
  public Payment updatePayment(Long id, PaymentRequest request) {
    Payment payment = getPaymentById(id);
    FnCommon.coppyNonNullProperties(payment, request);
    return paymentRepository.save(payment);
  }
}
