package mr.limpios.smart_divide_backend.infraestructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.exceptions.UnauthorizedAccessException;

class ErroHandlerControllerTest {

    private final ErroHandlerController errorHandler = new ErroHandlerController();

    @Test
    void handleInvalidDataException_returnsBadRequest() {
        String errorMessage = "Invalid data provided";
        InvalidDataException exception = new InvalidDataException(errorMessage);

        ResponseEntity<WrapperResponse<Void>> response = errorHandler.handleInvalidDataException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleResourceExistException_returnsConflict() {
        String errorMessage = "Resource already exists";
        ResourceExistException exception = new ResourceExistException(errorMessage);

        ResponseEntity<WrapperResponse<Void>> response = errorHandler.handleResourceExistException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleResourceNotFoundException_returnsNotFound() {
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ResponseEntity<WrapperResponse<Void>> response = errorHandler.handleResourceNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorized() {
        String errorMessage = "Access denied";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        ResponseEntity<WrapperResponse<Void>> response = errorHandler.handleUnauthorizedException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}