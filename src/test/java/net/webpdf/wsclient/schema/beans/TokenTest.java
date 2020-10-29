package net.webpdf.wsclient.schema.beans;

import net.webpdf.wsclient.exception.ResultException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenTest {

    @Test
    public void testToken(){
        Token token = new Token();
        assertNotNull(token.getToken());
        token.setToken("");
        assertNotNull(token.getToken());
        assertTrue(token.getToken().isEmpty());
        token.setToken("B407C53DBF8E4E20B08BE32B2A552CC8");
        assertNotNull(token.getToken());
        assertFalse(token.getToken().isEmpty());
        token.setToken("");
        assertNotNull(token.getToken());
        assertTrue(token.getToken().isEmpty());

        token = Token.create("");
        assertNotNull(token.getToken());
        assertTrue(token.getToken().isEmpty());
        token.setToken("B407C53DBF8E4E20B08BE32B2A552CC8");
        assertNotNull(token.getToken());
        assertFalse(token.getToken().isEmpty());
    }
}