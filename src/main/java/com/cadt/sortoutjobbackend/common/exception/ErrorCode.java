package com.cadt.sortoutjobbackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Authentication Errors (AUTH_xxx)
    AUTH_INVALID_CREDENTIALS("AUTH_001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_002", "Token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_003", "Invalid token", HttpStatus.UNAUTHORIZED),
    AUTH_EMAIL_NOT_VERIFIED("AUTH_004", "Email not verified", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_LOCKED("AUTH_005", "Account is locked", HttpStatus.FORBIDDEN),
    AUTH_USE_OAUTH("AUTH_006", "This account uses Google or Phone login. Please use the original method.", HttpStatus.BAD_REQUEST),
    AUTH_ALREADY_VERIFIED("AUTH_007", "Email already verified", HttpStatus.BAD_REQUEST),
    AUTH_ACCOUNT_DISABLED("AUTH_008", "Your account has been disabled. Please contact support.", HttpStatus.FORBIDDEN),

    // User Errors (USER_xxx)
    USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXISTS("USER_002", "Email already exists", HttpStatus.CONFLICT),
    USER_PHONE_EXISTS("USER_003", "Phone number already linked to another account", HttpStatus.CONFLICT),
    USER_PASSWORD_MISMATCH("USER_004", "Current password is incorrect", HttpStatus.BAD_REQUEST),
    USER_CANNOT_CHANGE_PASSWORD("USER_005", "Cannot change password for OAuth/Phone accounts", HttpStatus.BAD_REQUEST),

    // OTP Errors (OTP_xxx)
    OTP_EXPIRED("OTP_001", "OTP has expired", HttpStatus.BAD_REQUEST),
    OTP_INVALID("OTP_002", "Invalid OTP", HttpStatus.BAD_REQUEST),
    OTP_NOT_FOUND("OTP_003", "OTP not found. Please request a new one", HttpStatus.NOT_FOUND),

    // Token Errors (TOKEN_xxx)
    TOKEN_NOT_FOUND("TOKEN_001", "Refresh token not found", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED("TOKEN_002", "Refresh token expired", HttpStatus.UNAUTHORIZED),
    VERIFICATION_TOKEN_INVALID("TOKEN_003", "Invalid verification token", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_USED("TOKEN_004", "Verification token already used", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_EXPIRED("TOKEN_005", "Verification token expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_INVALID("TOKEN_006", "Invalid password reset token", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_USED("TOKEN_007", "Password reset token already used", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED("TOKEN_008", "Password reset token expired", HttpStatus.BAD_REQUEST),

    // Validation Errors (VAL_xxx)
    VALIDATION_ERROR("VAL_001", "Validation failed", HttpStatus.BAD_REQUEST),

    // General Errors
    INTERNAL_ERROR("ERR_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("ERR_002", "Bad request", HttpStatus.BAD_REQUEST),
    FORBIDDEN("ERR_003", "Access denied", HttpStatus.FORBIDDEN),

    // Resource Errors (RESOURCE_xxx)
    RESOURCE_NOT_FOUND("RESOURCE_001", "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("RESOURCE_002", "You are not authorized to access this resource", HttpStatus.FORBIDDEN),
    RESOURCE_CONFLICT("RESOURCE_003", "Resource already exists", HttpStatus.CONFLICT),

    // RATE LIMIT ERRORS
    RATE_LIMIT_EXCEEDED("RATE_001", "Too many requests. Please try again later.",
            HttpStatus.TOO_MANY_REQUESTS);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
