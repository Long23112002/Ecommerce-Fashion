package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum ErrorMessage implements BaseErrorMessage {
    CATEGORY_NAME_EXISTED("Tên Danh Mục Đã Tồn Tại"),
    ORIGIN_NAME_EXISTED("Tên Xuất Xứ Đã Tồn Tại"),
    BRAND_NAME_EXISTED("Tên Thương Hiệu Đã Tồn Tại"),
    ORIGIN_NOT_FOUND("Origin not found"),
    BRAND_NOT_FOUND("Brand not found"),
    CATEGORY_NOT_FOUND("Category not found"),
    SUCCESS("Success"),
    FALSE("False"),
    ROLE_NOT_FOUND("Role not found"),
    PERMISSION_NOT_FOUND("Permission not found"),
    USER_NOT_FOUND("User not found"),
    ROLE_ALREADY_ASSIGNED("Role already assigned"),
    JWT_EXPIRED("JWT expired"),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found"),
    BAD_CREDENTIAL("Bad credential"),
    INVALID_REFRESH_TOKEN("Invalid refresh token"),
    CURRENT_PASSWORD_SAME_NEW_PASSWORD("Current password same new password"),
    OTP_IS_EXPIRED("OTP is expired"),
    ERROR_UPLOADING_FILE("Error uploading file"),
    SOMETHING_WENT_WRONG("Something went wrong"),
    NOT_FOUND_BUCKET("Not found bucket"),
    ACCESS_DENIED("Access denied"),
    IMAGE_NOT_FOUND("Image not found"),
    ROLE_EXISTED("Role does not existed"),

    WEBSOCKET_NOT_FOUND("Websocket not found"),
    USER_PERMISSION_DENIED("user does not have the required permissions to access this action"),
    EMAIL_EXISTED("Email của bạn đã tồn tại trong hệ thống"),
    PHONE_EXISTED("Số điện thoại của bạn đã tồn tại trong hệ thống"),
    CHAT_ROOM_NOT_FOUND("Chat room not found"),
    TEMPLATE_NOT_FOUND("Template not found"),
    OTP_NOT_MATCH("OTP not match"),
    OTP_EXPIRED("OTP expired"),
    ;


    public String val;

    private ErrorMessage(String label) {
        val = label;
    }

    @Override
    public String val() {
        return val;
    }
}
