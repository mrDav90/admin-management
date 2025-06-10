package com.si.admin_management.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoggingAspectTest {
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Test
    void testLogAround() throws Throwable {
        // Given
        LoggingAspect loggingAspect = new LoggingAspect();
        String typeName = "TestTypeName";
        String methodName = "testMethodName";
        String[] args = {"arg1", "arg2"};

        // Mock
        Signature signature = mock(Signature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(typeName);
        when(signature.getName()).thenReturn(methodName);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        String expectedResult = "Test Result";
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        // When
        Object result = loggingAspect.logAround(proceedingJoinPoint);

        // Then
        verify(proceedingJoinPoint, times(1)).proceed();
        assertEquals(expectedResult, result);
    }
}
