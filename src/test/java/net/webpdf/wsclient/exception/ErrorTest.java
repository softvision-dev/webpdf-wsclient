package net.webpdf.wsclient.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorTest {
    @Test
    public void testError() {
        Error error = Error.getName(Error.UNKNOWN_EXCEPTION.getCode());
        assertEquals(Error.UNKNOWN_EXCEPTION.getCode(), error.getCode(),
                "Error-code should have been UNKNOWN");
        assertEquals(Error.UNKNOWN_EXCEPTION.getMessage(), error.getMessage(),
                "Error-message should be according to UNKNOWN");
        error = Error.getName(Integer.MAX_VALUE);
        assertEquals(Error.UNKNOWN_EXCEPTION.getCode(), error.getCode(),
                "Error-code should have been UNKNOWN");
        assertEquals(Error.UNKNOWN_EXCEPTION.getMessage(), error.getMessage(),
                "Error-message should be according to UNKNOWN");
    }

}
