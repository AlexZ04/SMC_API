package ru.smc.smc.api.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.smc.smc.api.domain.exceptions.BadRequestException;
import ru.smc.smc.api.domain.exceptions.NotFoundException;
import ru.smc.smc.api.domain.exceptions.UnauthorizedException;
import ru.smc.smc.api.domain.model.response.ErrorResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUsedCredentialsException(BadRequestException exception) {
        logError(exception);

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchNotFoundException(NotFoundException exception) {
        logError(exception);

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnauthorizedException(UnauthorizedException exception) {
        logError(exception);

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnknownException(Exception exception) {
        logError(exception);

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception exception) {
        log.error("Получена новая ошибка: {}", exception.getMessage(), exception);
    }
}
