package com.choidh.service.common.exception;

public class FileNotSavedException extends RuntimeException {
    public FileNotSavedException() {
        super();
    }

    public FileNotSavedException(String message) {
        super(message);
    }

    public FileNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotSavedException(Throwable cause) {
        super(cause);
    }

    protected FileNotSavedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
