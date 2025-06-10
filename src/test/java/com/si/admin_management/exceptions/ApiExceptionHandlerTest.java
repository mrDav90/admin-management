package com.si.admin_management.exceptions;

import com.si.admin_management.exception.ApiException;
import com.si.admin_management.exception.ApiExceptionHandler;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApiExceptionHandlerTest {
    @Mock
    private final ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();


    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        ResponseEntity<ApiException> response = exceptionHandler.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entity not found", response.getBody().getMessage());
    }

    @Test
    void testHandleEntityExistsException() {
        EntityExistsException ex = new EntityExistsException("Entity already exists");
        ResponseEntity<ApiException> response = exceptionHandler.handleEntityExistException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entity already exists", response.getBody().getMessage());
    }

}
