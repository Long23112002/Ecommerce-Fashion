package org.example.ecommercefashion.services.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.example.ecommercefashion.config.payment.VNPayConfig;
import org.example.ecommercefashion.services.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VNPayServiceImpl implements VNPayService {

  @Autowired private VNPayConfig vnPayConfig;

  @Value("${vnpay.url}")
  private String vnPayUrl;

  @Value("${vnpay.return.url}")
  private String returnUrl;

  @Value("${vnpay.tmn.code}")
  private String tmnCode;

  @Value("${vnpay.secret.key}")
  private String secretKey;

  @Value("${vnpay.api.url}")
  private String vnPayAPIUrl;

  @Value("${vnpay.version}")
  private String vnPayVersion;

  @Value("${vnpay.command}")
  private String command;

  @Override
  public String createPayment(HttpServletRequest request, long amountRequest) {

    String orderType = "other";
    long amount = calculateAmount(amountRequest);

    String vnpTxnRef = vnPayConfig.getRandomNumber(8);
    String vnpIpAddress = vnPayConfig.getIpAddress(request);
    String vnpTmnCode = tmnCode;

    Map<String, String> vnpParams =
        buildParamVNPay(vnpTxnRef, amount, orderType, vnpIpAddress, vnpTmnCode);

    String queryUrl = buildQueryUrl(vnpParams);
    String secureHash = generateSecureHash(vnpParams);

    queryUrl += "&vnp_SecureHash=" + secureHash;

    return vnPayUrl + "?" + queryUrl;
  }

  @Override
  public ResponseEntity<?> paymentSuccess(String status) {
    if (status.equals("00")) {
      return ResponseEntity.ok("redirect:/success");
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private Map<String, String> buildParamVNPay(
      String vnpTxnRef, long amount, String orderType, String vnpIpAddress, String vnpTmnCode) {
    Map<String, String> vnpParams = new HashMap<>();

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnpCreateDate = format.format(cld.getTime());

    vnpParams.put("vnp_CreateDate", vnpCreateDate);
    vnpParams.put("vnp_Version", vnPayVersion);
    vnpParams.put("vnp_Command", command);
    vnpParams.put("vnp_TmnCode", vnpTmnCode);
    vnpParams.put("vnp_Amount", String.valueOf(amount));
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_BankCode", "NCB");
    vnpParams.put("vnp_TxnRef", vnpTxnRef);
    vnpParams.put("vnp_OrderInfo", "Thanh toán đơn hàng" + vnpTxnRef);
    vnpParams.put("vnp_OrderType", orderType);
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", returnUrl);
    vnpParams.put("vnp_IpAddr", vnpIpAddress);
    return vnpParams;
  }

  private String buildQueryUrl(Map<String, String> vnpParams) {
    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);

    StringBuilder query = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();

    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = vnpParams.get(fieldName);

      if (fieldValue != null && !fieldValue.isEmpty()) {
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

        if (itr.hasNext()) {
          query.append('&');
        }
      }
    }

    return query.toString();
  }

  private String generateSecureHash(Map<String, String> vnpParams) {
    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();

    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = vnpParams.get(fieldName);

      if (fieldValue != null && !fieldValue.isEmpty()) {
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

        if (itr.hasNext()) {
          hashData.append('&');
        }
      }
    }

    return vnPayConfig.hmacSHA512(secretKey, hashData.toString());
  }

  private long calculateAmount(long amountRequest) {
    return amountRequest * 100;
  }
}
