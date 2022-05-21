package com.hanghae99.onit_be.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    //ExceptionHandler 를 통해 Global 예외 처리 . IllegalArgumentException,NullPointerException 발생시
    // 오류에 해당하는 msg 와 400 코드가 가도록 설정 .

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        Exception exception = new Exception();
        exception.setHttpStatus(HttpStatus.BAD_REQUEST);
        exception.setMsg(ex.getMessage());

        return new ResponseEntity(
                exception,
                HttpStatus.OK
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Object> handleApiRequestException(NullPointerException ex) {
        Exception exception = new Exception();
        exception.setHttpStatus(HttpStatus.BAD_REQUEST);
        exception.setMsg(ex.getMessage());

        return new ResponseEntity(
                exception,
                HttpStatus.OK
        );
    }
}
