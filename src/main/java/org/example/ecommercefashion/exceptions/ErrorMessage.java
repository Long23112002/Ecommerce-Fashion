package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum ErrorMessage implements BaseErrorMessage {
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

    IMAGE_NOT_FOUND("Image not found"),
    TEMPLATE_NOT_FOUND("Template not found"),

    ACCESS_DENIED("Access denied"),
    ROLE_EXISTED("Role does not existed"),
    USER_EXISTED("User existed"),


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
