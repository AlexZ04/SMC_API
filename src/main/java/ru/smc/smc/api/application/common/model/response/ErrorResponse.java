package ru.smc.smc.api.application.common.model.response;

import jakarta.validation.constraints.NotNull;

public record ErrorResponse(int code, @NotNull String message) {
}
