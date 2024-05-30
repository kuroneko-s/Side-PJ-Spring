package com.choidh.service.common.exception;

public class FileCantDeleteException extends RuntimeException {
    public FileCantDeleteException() {
        super();
    }

    public FileCantDeleteException(String message) {
        super(message);
    }

    public FileCantDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileCantDeleteException(Throwable cause) {
        super(cause);
    }

    protected FileCantDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
