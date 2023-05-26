package com.example.wall.http;

import com.hjq.http.exception.HttpException;

public class TokenException extends HttpException {
    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
