package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.OptCodeRequest;
import org.example.ecommercefashion.entities.User;

import java.util.Optional;

public interface OTPService {
    String generateOTP();
    void storeTemporaryUser(User user);
    User getTemporaryUser(String emailUser);
    void saveOtp(String emailUser, String otp);
    Optional getOtp(String emailUser);
    void deleteOtp(String emailUser);
}
