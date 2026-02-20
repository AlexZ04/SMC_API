package ru.smc.smc.api.common.model.response;

import jakarta.validation.constraints.NotNull;

public record ErrorResponse(int code, @NotNull String message) {
}
