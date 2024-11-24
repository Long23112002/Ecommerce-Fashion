package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.QRCodeRequest;
import org.example.ecommercefashion.services.QRCodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qr_code")
@Api(tags = "QR_CODE", value = "Endpoints for QR_CODE")
public class QRCodeGeneratorController {

  @Autowired private QRCodeGeneratorService qrCodeGenerator;

  @GetMapping("")
  public ResponseEntity<byte[]> generateOrderPdf(QRCodeRequest request) {
    byte[] pdfBytes =
        qrCodeGenerator.generateQRCode(request.getProductDetailId(), request.getQty());

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=qr_" + System.currentTimeMillis() + ".pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }
}
