package net.webpdf.wsclient.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {
    @Test
    public void testBuildResult() {
        Result result = Result.build(Error.NONE);
        assertEquals(Error.NONE.getCode(), result.getCode(),
                "Errorcode should have been NONE.");
        assertNull(result.getException(),
                "Exception should have been NULL.");
        assertEquals(Error.NONE.getMessage(), result.getMessage(),
                "Errormessage should been according to NONE.");
        assertEquals(Error.NONE, result.getError(),
                "Error should have been Error.NONE.");
        assertTrue(result.equalsError(Error.NONE),
                "Equality check should have returned true.");
        assertFalse(result.isError(),
                "Check for error occurance should return false.");
        assertTrue(result.isSuccess(),
                "Check for success should return true.");

        //TODO: DIFFERENTIATE
        result = Result.build(Error.UNKNOWN_EXCEPTION, -20);
        assertEquals(Error.UNKNOWN_EXCEPTION.getCode(), result.getCode(),
                "Errorcode should have been UNKNOWN.");
        assertEquals(Error.UNKNOWN_EXCEPTION, result.getError(),
                "Error should have been Error.UNKNOWN.");
        assertNull(result.getException(),
                "Exception should have been NULL.");
        assertEquals(Error.UNKNOWN_EXCEPTION.getMessage() + " [-20]", result.getMessage(),
                "Errormessage should been according to NONE with appended exitcode.");
        assertTrue(result.equalsError(Error.UNKNOWN_EXCEPTION),
                "Equality check should have returned true.");
        assertTrue(result.isError(),
                "Check for error occurance should return true.");
        assertFalse(result.isSuccess(),
                "Check for success should return false.");

        Exception ex = new IOException("inner message");
        result = Result.build(Error.UNKNOWN_EXCEPTION, ex);
        assertEquals(Error.UNKNOWN_EXCEPTION.getCode(), result.getCode(),
                "Errorcode should have been UNKNOWN.");
        assertEquals(Error.UNKNOWN_EXCEPTION, result.getError(),
                "Error should have been Error.UNKNOWN.");
        assertEquals(result.getException(), ex,
                "Exception should have been set.");
        assertEquals(Error.UNKNOWN_EXCEPTION.getMessage(), result.getMessage(),
                "Errormessage should been according to UNKNOWN.");
        assertTrue(result.equalsError(Error.UNKNOWN_EXCEPTION),
                "Equality check should have returned true.");
        assertTrue(result.isError(),
                "Check for error occurance should return true.");
        assertFalse(result.isSuccess(),
                "Check for success should return false.");

        result.appendMessage("further message");
        assertEquals(Error.UNKNOWN_EXCEPTION.getMessage() + "\nFurther message", result.getMessage(),
                "Errormessage should been according to UNKNOWN with appended further message.");
    }
}
