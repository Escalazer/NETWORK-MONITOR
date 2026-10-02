package com.networkmonitor.exception;

public class NetworkDiagnosticException extends RuntimeException {

    public NetworkDiagnosticException(String message) {
        super(message);
    }

    public NetworkDiagnosticException(String message, Throwable cause) {
        super(message, cause);
    }
}
