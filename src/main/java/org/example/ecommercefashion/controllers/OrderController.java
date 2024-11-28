package org.example.ecommercefashion.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.validation.Valid;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderAtStoreUpdateRequest;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.services.PaymentService;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest orderRequest,
                                @RequestHeader(value = "Authorization", required = false) String token) {
        return orderService.createOrder(orderRequest, token);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/update-address/{id}")
    public OrderResponse updateAdress(@PathVariable Long id,
                                      @Valid @RequestBody OrderAddressUpdate orderAddressUpdate) {
        return orderService.updateAddress(id, orderAddressUpdate);
    }

    @PutMapping("/update-discount/{id}")
    public OrderResponse updateDiscount(@PathVariable Long id,
                                        Long discountId) {
        return orderService.updateDiscount(id, discountId);
    }

    @PutMapping("/payment/{id}")
    public String orderUpdateAndPay(@PathVariable Long id,
                                    @Valid @RequestBody OrderUpdateRequest orderUpdateRequest)
            throws UnsupportedEncodingException, JobExecutionException {
        return orderService.orderUpdateAndPay(id, orderUpdateRequest);
    }

    @PutMapping("/confirm")
    public OrderResponse orderUpdateAndPay(@RequestBody TransactionRequest request)
            throws JobExecutionException {
        return orderService.confirmOrder(request);
    }

    @PutMapping("/{id}")
    public OrderResponse updateStateOrder(
            @PathVariable Long id, @Valid @RequestBody OrderChangeState orderChangeState) {
        return orderService.updateStateOrder(id, orderChangeState);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping
    public ResponsePageV2<OrderResponse> filter(OrderParam param, PageableRequest pageableRequest) {
        return new ResponsePageV2<>(orderService.filter(param, pageableRequest.toPageable()));
    }

    @GetMapping("/store")
    public Order createOrderAtStore(@RequestHeader("Authorization") String token) {
        return orderService.createOrderAtStore(token);
    }

    @GetMapping("/list-pending")
    public List<Order> getOrderPendingAtStore(@RequestHeader("Authorization") String token) {
        return orderService.getOrderPendingAtStore(token);
    }

    @GetMapping("/checksum")
    public boolean checkTransaction(Double amount, String description) {
        return paymentService.handelPaymentApi(amount, description);
    }

    @GetMapping("/store/{id}")
    public void updateStatusAtStore(@PathVariable Long id) {
        orderService.updateStateOrderAtStore(id);
    }

    @GetMapping("/export-pdf/{orderId}")
    public ResponseEntity<byte[]> generateOrderPdf(@PathVariable Long orderId) {
        byte[] pdfBytes = orderService.generateOrderPdf(orderId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

  @PutMapping("/store/{id}")
  public Order addGuestDiscountOrder(
          @PathVariable Long id, @Valid @RequestBody OrderAtStoreUpdateRequest request) {
    return orderService.updateOrderAtStore(id, request);
  }
}
