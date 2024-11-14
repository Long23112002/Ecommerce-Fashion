package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.services.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    public String createPayment(HttpServletRequest request,
                                @RequestParam("amount") long amountRequest,
                                @RequestParam("orderId") long orderId)
            throws UnsupportedEncodingException {
        return vnPayService.createPayment(request, amountRequest, orderId);
    }

    @GetMapping("/payment-info")
    public ResponseEntity<?> paymentSuccess(@RequestParam("status") String status) {
        return vnPayService.paymentSuccess(status);
    }
}
