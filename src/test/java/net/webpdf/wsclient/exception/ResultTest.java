package net.webpdf.wsclient.exception;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResultTest {
    @Test
    public void testBuildResult() {
        Result result = Result.build(Error.NONE);
        assertEquals("Errorcode should have been NONE.", Error.NONE.getCode(), result.getCode());
        assertNull("Exception should have been NULL.", result.getException());
        assertEquals("Errormessage should been according to NONE.", Error.NONE.getMessage(), result.getMessage());
        assertEquals("Error should have been Error.NONE.", Error.NONE, result.getError());
        assertTrue("Equality check should have returned true.", result.equalsError(Error.NONE));
        assertFalse("Check for error occurance should return false.", result.isError());
        assertTrue("Check for success should return true.", result.isSuccess());

        //TODO: DIFFERENTIATE
        result = Result.build(Error.UNKNOWN_EXCEPTION, -20);
        assertEquals("Errorcode should have been UNKNOWN.", Error.UNKNOWN_EXCEPTION.getCode(), result.getCode());
        assertEquals("Error should have been Error.UNKNOWN.", Error.UNKNOWN_EXCEPTION, result.getError());
        assertNull("Exception should have been NULL.", result.getException());
        assertEquals("Errormessage should been according to NONE with appended exitcode.", Error.UNKNOWN_EXCEPTION.getMessage() + " [-20]", result.getMessage());
        assertTrue("Equality check should have returned true.", result.equalsError(Error.UNKNOWN_EXCEPTION));
        assertTrue("Check for error occurance should return true.", result.isError());
        assertFalse("Check for success should return false.", result.isSuccess());

        Exception ex = new IOException("inner message");
        result = Result.build(Error.UNKNOWN_EXCEPTION, ex);
        assertEquals("Errorcode should have been UNKNOWN.", Error.UNKNOWN_EXCEPTION.getCode(), result.getCode());
        assertEquals("Error should have been Error.UNKNOWN.", Error.UNKNOWN_EXCEPTION, result.getError());
        assertEquals("Exception should have been set.", result.getException(), ex);
        assertEquals("Errormessage should been according to UNKNOWN.", Error.UNKNOWN_EXCEPTION.getMessage(), result.getMessage());
        assertTrue("Equality check should have returned true.", result.equalsError(Error.UNKNOWN_EXCEPTION));
        assertTrue("Check for error occurance should return true.", result.isError());
        assertFalse("Check for success should return false.", result.isSuccess());

        result.addMessage("further message");
        assertEquals("Errormessage should been according to UNKNOWN with appended further message.", Error.UNKNOWN_EXCEPTION.getMessage() + "\nFurther message", result.getMessage());
    }
}
