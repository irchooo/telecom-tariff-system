package ru.itmo.telecom.shared.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorDto {
    private final String error;
}
