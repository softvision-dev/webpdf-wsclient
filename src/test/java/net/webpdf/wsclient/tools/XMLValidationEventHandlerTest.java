package net.webpdf.wsclient.tools;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventLocator;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class XMLValidationEventHandlerTest {
    @Test
    public void testEvents() {
        XMLValidationEventHandler eventHandler = new XMLValidationEventHandler();
        assertEquals(XMLStatus.OK, eventHandler.getXMLStatus(),
                "The initial state of an eventHandler should always be OK.");
        assertTrue(eventHandler.isValid(),
                "Initially an eventHandler should not indicate a failure.");
        eventHandler.handleEvent(null);
        assertEquals(XMLStatus.OK, eventHandler.getXMLStatus(),
                "A null event should not alter the current status.");
        assertTrue(eventHandler.isValid(),
                "A null event should not influence the current validation.");
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.WARNING));
        assertEquals(XMLStatus.WARNING, eventHandler.getXMLStatus(),
                "A warning event should lead to an according status.");
        assertFalse(eventHandler.isValid(),
                "The handler shall indicate a failure after an occurring warning.");
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.ERROR));
        assertEquals(XMLStatus.ERROR, eventHandler.getXMLStatus(),
                "An error event should lead to an according status");
        assertFalse(eventHandler.isValid(),
                "The handler shall indicate a failure after an occurring error.");
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.FATAL_ERROR));
        assertEquals(XMLStatus.FATAL, eventHandler.getXMLStatus(),
                "A fatal error event should lead to an according status");
        assertFalse(eventHandler.isValid(),
                "The handler shall indicate a failure after an occurring fatal error.");

        assertEquals("LEVEL:0 (line: 0; column: 0)\n" +
                        "LEVEL:1 (line: 0; column: 0)\n" +
                        "LEVEL:2 (line: 0; column: 0)",
                eventHandler.getMessages(),
                "The summary of events is not as expected."
        );
    }

    private static class TestValidationEvent implements ValidationEvent {

        private final int errorLevel;

        TestValidationEvent(int errorLevel) {
            this.errorLevel = errorLevel;
        }

        @Override
        public int getSeverity() {
            return errorLevel;
        }

        @Override
        public String getMessage() {
            return "LEVEL:" + errorLevel;
        }

        @Override
        public Throwable getLinkedException() {
            return null;
        }

        @Override
        public ValidationEventLocator getLocator() {
            return new ValidationEventLocator() {
                @Override
                public URL getURL() {
                    return null;
                }

                @Override
                public int getOffset() {
                    return 0;
                }

                @Override
                public int getLineNumber() {
                    return 0;
                }

                @Override
                public int getColumnNumber() {
                    return 0;
                }

                @Override
                public Object getObject() {
                    return null;
                }

                @Override
                public Node getNode() {
                    return null;
                }
            };
        }

    }

}
