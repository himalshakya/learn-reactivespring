package net.shakya.learnreactivespring.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException exp){
    log.error("Exception caught by handleRuntimeException : {} ", exp.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exp.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllException(Exception exp){
    log.error("Exception caught by handleAllException : {} ", exp.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exp.getMessage());
  }
}
