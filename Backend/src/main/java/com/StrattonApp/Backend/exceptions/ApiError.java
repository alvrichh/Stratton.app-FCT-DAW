package com.StrattonApp.Backend.exceptions;

import java.time.Instant;
import java.util.Map;

/**
 * Cuerpo JSON uniforme de las respuestas de error.
 */
public class ApiError {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> errors;

    public ApiError(int status, String error, String message, String path, Map<String, String> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, String> getErrors() { return errors; }
}
