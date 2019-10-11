package net.webpdf.wsclient.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorTest {
    @Test
    public void testError() {
        Error error = Error.getName(Error.NONE.getCode());
        assertEquals("Errorcode should have been NONE", Error.NONE.getCode(), error.getCode());
        assertEquals("Errormessage should be according to NONE", Error.NONE.getMessage(), error.getMessage());
        //TODO: DIFFERENTIATE
        error = Error.getName(Error.UNKNOWN_EXCEPTION.getCode());
        assertEquals("Errorcode should have been UNKNOWN", Error.UNKNOWN_EXCEPTION.getCode(), error.getCode());
        assertEquals("Errormessage should be according to UNKNOWN", Error.UNKNOWN_EXCEPTION.getMessage(), error.getMessage());
        error = Error.getName(Integer.MAX_VALUE);
        assertEquals("Errorcode should have been UNKNOWN", Error.UNKNOWN_EXCEPTION.getCode(), error.getCode());
        assertEquals("Errormessage should be according to UNKNOWN", Error.UNKNOWN_EXCEPTION.getMessage(), error.getMessage());
    }
}
