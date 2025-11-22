package com.financemanager.financemanager.DTOs.responses;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor
public class ErrorResponse {
    private final Instant timestamp = Instant.now();
    private final int     status;      // HTTP-код
    private final String  error;       // короткий код/phrase
    private final String  message;     // человеко-читаемое описание
    private final String  path;        // URI, к которому обратились
}