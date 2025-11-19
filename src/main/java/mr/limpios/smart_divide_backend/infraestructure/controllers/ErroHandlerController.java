package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.exceptions.UnauthorizedAccessException;

@ControllerAdvice
public class ErroHandlerController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidDataException.class)
  public ResponseEntity<WrapperResponse<Void>> handleInvalidDataException(Exception exception) {
    return new ResponseEntity<>(new WrapperResponse<>(false, exception.getMessage(), null),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceExistException.class)
  public ResponseEntity<WrapperResponse<Void>> handleResourceExistException(Exception exception) {
    return new ResponseEntity<>(new WrapperResponse<>(false, exception.getMessage(), null),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<WrapperResponse<Void>> handleResourceNotFoundException(
      Exception exception) {
    return new ResponseEntity<>(new WrapperResponse<>(false, exception.getMessage(), null),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<WrapperResponse<Void>> handleUnauthorizedException(Exception exception) {
    return new ResponseEntity<>(new WrapperResponse<>(false, exception.getMessage(), null),
        HttpStatus.UNAUTHORIZED);
  }
}
