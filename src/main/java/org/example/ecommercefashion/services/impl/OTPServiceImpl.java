package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.response.OtpResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {
  private final Map<String, User> temporaryUserStorage = new HashMap<>();
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final int LENGTH_OTP = 6;
  private final String NUMBERS = "0123456789";

  @Value("${spring.cache.redis.time-to-live}")
  private int expireTime;

  @Autowired private RedisTemplate<String, String> redisTemplate;
  private ValueOperations<String, String> valueOps;

  @Override
  public void saveOtp(String emailUser, String otp) {
    try {
      valueOps = redisTemplate.opsForValue();
      valueOps.set(emailUser, otp, expireTime, TimeUnit.SECONDS);
      log.info("đã save trong redis ");
      log.info("valueOps {} ", valueOps);
    } catch (Exception e) {
      throw new ExceptionHandle(HttpStatus.REQUEST_TIMEOUT, "Error while saving to cache " + e);
    }
  }

  @Override
  public Optional<OtpResponse> getOtp(String emailUser) {
    valueOps = redisTemplate.opsForValue();
    log.info("valueOps {}", valueOps.get(emailUser));
    try {
      Boolean b = redisTemplate.hasKey(emailUser);
      if (Boolean.TRUE.equals(b)) {
        String otp = (String) valueOps.get(emailUser);
        return Optional.of(new OtpResponse(emailUser, otp));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      throw new ExceptionHandle(
          HttpStatus.REQUEST_TIMEOUT, "Error while retrieving from the cache: " + e);
    }
  }

  @Override
  public void deleteOtp(String emailUser) {
    try {
      redisTemplate.delete(emailUser);
    } catch (Exception e) {
      throw new ExceptionHandle(
          HttpStatus.REQUEST_TIMEOUT, "Error while removing from the cache " + e);
    }
  }

  @Override
  public String generateOTP() {
    StringBuilder otp = new StringBuilder();
    Random random = new SecureRandom();
    for (int i = 0; i < LENGTH_OTP; ++i) {
      otp.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
    }
    return otp.toString();
  }

  @Override
  public void storeTemporaryUser(User user) {
    temporaryUserStorage.put(user.getEmail(), user);
  }

  @Override
  public User getTemporaryUser(String emailUser) {
    return temporaryUserStorage.get(emailUser);
  }
}
