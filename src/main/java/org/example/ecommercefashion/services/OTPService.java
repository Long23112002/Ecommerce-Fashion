package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.User;

import java.util.Optional;

public interface OTPService {
    String generateOTP();

    void storeTemporaryUser(User user);

    User getTemporaryUser(String emailUser);

    // Lưu OTP vào Redis với thời gian hết hạn
    void saveOtp(String emailUser, String otp);

    // Lấy OTP từ Redis
    Optional getOtp(String emailUser);

    // Xóa OTP khỏi redis sau khi xác nhận thành công
    void deleteOtp(String emailUser);
}
