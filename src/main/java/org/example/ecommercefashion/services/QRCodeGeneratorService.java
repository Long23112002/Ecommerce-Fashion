package org.example.ecommercefashion.services;

public interface QRCodeGeneratorService {

  byte[] generateQRCode(Long productDetailId, Long qty);
}
