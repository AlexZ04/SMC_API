package ru.smc.smc.api.infrastructure;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler
//    public ResponseEntity<ResponseDto> catchUsedCredentialsException(UsedCredentialsException exception) {
//        return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST.value(), exception.getMessage()),
//                HttpStatus.BAD_REQUEST);
//    }
}
