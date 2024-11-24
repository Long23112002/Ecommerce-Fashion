package org.example.ecommercefashion.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.longnh.exceptions.ExceptionHandle;
import java.io.ByteArrayOutputStream;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.services.QRCodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class QRCodeGeneratorServiceImpl implements QRCodeGeneratorService {

  @Autowired private ProductDetailRepository productDetailRepository;

  @Override
  public byte[] generateQRCode(Long productDetailId, Long qty) {
    ProductDetail productDetail =
        productDetailRepository
            .findById(productDetailId)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, "Product detail not found"));

    try (ByteArrayOutputStream pdfStream = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, pdfStream);
      document.open();

      Image logo = loadLogoImage("src/main/resources/msttcorefonts/logo.png");

      generateQRCodes(document, productDetail, qty, logo);

      document.close();
      return pdfStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate QR code", e);
    }
  }

  private Image loadLogoImage(String path) throws Exception {
    Image logo = Image.getInstance(path);
    logo.scaleToFit(30, 30);
    return logo;
  }

  private void generateQRCodes(Document document, ProductDetail productDetail, Long qty, Image logo)
      throws Exception {
    int qrCodesPerPage = 2;
    float initialYPosition = document.getPageSize().getHeight() - 250;
    float yPosition = initialYPosition;
    int qrCodeCount = 0;

    Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    for (long i = 0; i < qty; i++) {
      String productInfo = generateProductInfo(productDetail);
      byte[] qrCodeBytes = generateQRCodeImage(productInfo);
      Image qrCodeImage = Image.getInstance(qrCodeBytes);
      qrCodeImage.scaleToFit(200, 200);

      if (qrCodeCount >= qrCodesPerPage) {
        document.newPage();
        yPosition = initialYPosition;
        qrCodeCount = 0;
      }

      float xPosition = (document.getPageSize().getWidth() - qrCodeImage.getScaledWidth()) / 2;

      positionQRCode(
          document,
          qrCodeImage,
          logo,
          productDetail.getProduct().getCode(),
          boldFont,
          xPosition,
          yPosition);
      yPosition -= qrCodeImage.getScaledHeight() + 40;
      qrCodeCount++;
    }
  }

  private void positionQRCode(
      Document document,
      Image qrCodeImage,
      Image logo,
      String productCode,
      Font boldFont,
      float xPosition,
      float yPosition)
      throws DocumentException {
    qrCodeImage.setAbsolutePosition(xPosition, yPosition);
    document.add(qrCodeImage);
    positionLogoOnQRCode(logo, qrCodeImage);
    document.add(logo);
    document.add(new Chunk("\n"));
  }

  private String generateProductInfo(ProductDetail productDetail) {
    return String.format(
        "ID: %d\nName: %s\nPrice: %.2f\nProduct Code: %s\nColor: %s\nSize: %s",
        productDetail.getId(),
        productDetail.getProduct().getName(),
        productDetail.getPrice(),
        productDetail.getProduct().getCode(),
        productDetail.getColor().getName(),
        productDetail.getSize().getName());
  }

  private byte[] generateQRCodeImage(String productInfo) throws Exception {
    MultiFormatWriter writer = new MultiFormatWriter();
    BitMatrix bitMatrix = writer.encode(productInfo, BarcodeFormat.QR_CODE, 300, 300);

    ByteArrayOutputStream qrCodeStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrCodeStream);

    return qrCodeStream.toByteArray();
  }

  private void positionLogoOnQRCode(Image logo, Image qrCodeImage) {
    float x =
        qrCodeImage.getAbsoluteX() + (qrCodeImage.getScaledWidth() - logo.getScaledWidth()) / 2;
    float y =
        qrCodeImage.getAbsoluteY() + (qrCodeImage.getScaledHeight() - logo.getScaledHeight()) / 2;
    logo.setAbsolutePosition(x, y);
  }
}
