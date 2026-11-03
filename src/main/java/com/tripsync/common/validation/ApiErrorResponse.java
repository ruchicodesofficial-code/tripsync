package com.tripsync.common.validation;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}
