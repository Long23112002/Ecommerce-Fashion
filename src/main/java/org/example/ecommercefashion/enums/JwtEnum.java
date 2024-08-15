package org.example.ecommercefashion.enums;

public enum JwtEnum {
    USER_ID("userId"),
    NAME_PERMISSION("namePermission"),
    AUTHORITIES_SYSTEM("authoritiesSystem"),
    IS_EMAIL_VERIFIED("isEmailVerified"),
    EMAIL("email");

    private final String val;

    JwtEnum(String value) {
        this.val = value;
    }

    public String val() {
        return val;
    }
}
