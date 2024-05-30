package com.choidh.service.common.exception;

public class DirectoryNotCreateException extends RuntimeException {
    public DirectoryNotCreateException() {
        super();
    }

    public DirectoryNotCreateException(String message) {
        super(message);
    }

    public DirectoryNotCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryNotCreateException(Throwable cause) {
        super(cause);
    }

    protected DirectoryNotCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
