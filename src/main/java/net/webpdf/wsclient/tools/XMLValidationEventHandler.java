package net.webpdf.wsclient.tools;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import java.util.ArrayList;
import java.util.List;

class XMLValidationEventHandler implements ValidationEventHandler {

    private List<String> messages = new ArrayList<>();
    private XMLStatus xmlStatus = XMLStatus.OK;

    /**
     * Returns true, as long as the {@link XMLStatus} equals OK.
     *
     * @return True, if the {@link XMLStatus} equals OK.
     */
    public boolean isValid() {
        return this.xmlStatus.equals(XMLStatus.OK);
    }

    /**
     * Updates the current {@link XMLStatus} according to the given {@link ValidationEvent}.
     *
     * @param event the {@link ValidationEvent} the {@link XMLStatus} shall be updated according to.
     * @return True, if the {@link XMLStatus} could be updated accordingly.
     */
    @Override
    public boolean handleEvent(ValidationEvent event) {

        if (event == null) {
            return false;
        }

        XMLStatus status = XMLStatus.OK;
        switch (event.getSeverity()) {
            case ValidationEvent.WARNING:
                status = XMLStatus.WARNING;
                break;
            case ValidationEvent.ERROR:
                status = XMLStatus.ERROR;
                break;
            case ValidationEvent.FATAL_ERROR:
                status = XMLStatus.FATAL;
                break;

        }
        if (status.ordinal() > this.xmlStatus.ordinal()) {
            this.xmlStatus = status;
        }

        String message = event.getMessage() != null ? event.getMessage() : "";

        ValidationEventLocator validationEventLocator = event.getLocator();
        if (validationEventLocator != null) {
            message = message + String.format(" (line: %d; column: %d)", validationEventLocator.getLineNumber(),
                validationEventLocator.getColumnNumber());
        }
        messages.add(message);

        return false;
    }

    /**
     * Returns the collected messages from all {@link ValidationEvent}s, that have been passed to this handler.
     *
     * @return A String containing the collected messages from all {@link ValidationEvent}s.
     */
    public String getMessages() {
        return StringUtils.join(messages, "\n");
    }

    /**
     * Returns the current {@link XMLStatus}.
     *
     * @return the current {@link XMLStatus}.
     */
    public XMLStatus getXMLStatus() {
        return this.xmlStatus;
    }
}
