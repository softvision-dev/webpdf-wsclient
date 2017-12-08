package net.webpdf.wsclient.tools;

import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;

import java.net.URL;

import static org.junit.Assert.*;

public class XMLValidationEventHandlerTest {
    @Test
    public void testEvents() {
        XMLValidationEventHandler eventHandler = new XMLValidationEventHandler();
        assertEquals("The initial state of an eventHandler should always be OK.", XMLStatus.OK, eventHandler.getXMLStatus());
        assertTrue("Initially an eventHandler should not indicate a failure.", eventHandler.isValid());
        eventHandler.handleEvent(null);
        assertEquals("A null event should not alter the current status.", XMLStatus.OK, eventHandler.getXMLStatus());
        assertTrue("A null event should not influence the current validation.", eventHandler.isValid());
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.WARNING));
        assertEquals("A warning event should lead to an according status.", XMLStatus.WARNING, eventHandler.getXMLStatus());
        assertFalse("The handler shall indicate a failure after an occurring warning.", eventHandler.isValid());
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.ERROR));
        assertEquals("An error event should lead to an according status", XMLStatus.ERROR, eventHandler.getXMLStatus());
        assertFalse("The handler shall indicate a failure after an occurring error.", eventHandler.isValid());
        eventHandler.handleEvent(new TestValidationEvent(ValidationEvent.FATAL_ERROR));
        assertEquals("A fatal error event should lead to an according status", XMLStatus.FATAL, eventHandler.getXMLStatus());
        assertFalse("The handler shall indicate a failure after an occurring fatal error.", eventHandler.isValid());

        assertEquals("The summary of events is not as expected.",
            "LEVEL:0 (line: 0; column: 0)\n" +
                "LEVEL:1 (line: 0; column: 0)\n" +
                "LEVEL:2 (line: 0; column: 0)",
            eventHandler.getMessages()
        );
    }

    private class TestValidationEvent implements ValidationEvent {

        private final int errorLevel;

        public TestValidationEvent(int errorLevel) {
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
