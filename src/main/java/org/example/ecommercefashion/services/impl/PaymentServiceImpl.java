package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import org.example.ecommercefashion.dtos.request.PaymentRequest;
import org.example.ecommercefashion.dtos.response.BankTransactionResponse;
import org.example.ecommercefashion.entities.Payment;
import org.example.ecommercefashion.entities.Transaction;
import org.example.ecommercefashion.repositories.PaymentRepository;
import org.example.ecommercefashion.services.PaymentService;
import org.example.ecommercefashion.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Autowired private PaymentRepository paymentRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private TransactionService transactionService;


  private static final String API_URL = "https://my.sepay.vn/userapi/transactions/list?account_number=2222013333567&limit=20";
  private static final String AUTHORIZATION_TOKEN = "Bearer UFPIKSBWYYYZKUVPNTAGHHTJGNZS4L8JEHQMNGCC3TO4CXD6OQBFXE1EOIUDLX07";


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

  public boolean handelPaymentApi(Double amount, String description) {
    BankTransactionResponse transactions = getTransactions();
    if (transactions != null) {
      return transactions.getTransactions().stream()
              .anyMatch(transaction -> {
                boolean isAmountMatch = transaction.getAmountIn() != null
                        && transaction.getAmountIn().equals(amount);

                boolean isDescriptionMatch = transaction.getCode() != null
                        && transaction.getCode().equals(description);

                if (isAmountMatch && isDescriptionMatch) {
                  transactionService.create(Transaction.builder()
                                  .accountNumber(transaction.getAccountNumber())
                                  .code(transaction.getCode())
                                  .body(transaction.getTransactionContent())
                                  .amountIn(BigDecimal.valueOf(transaction.getAmountIn()))
                                  .referenceNumber(transaction.getReferenceNumber())
                                  .transactionDate(ZonedDateTime.parse(transaction.getTransactionDate()))
                          .build());
                  return true;
                }
                return false;
              });
    }
    return false;
  }

  public BankTransactionResponse getTransactions() {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", AUTHORIZATION_TOKEN);

    HttpEntity<String> entity = new HttpEntity<>(headers);


    ResponseEntity<BankTransactionResponse> response = restTemplate.exchange(
            API_URL, HttpMethod.GET, entity, BankTransactionResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
        System.out.print(response.getBody());
        return response.getBody();
    }
      return null;
  }
}
