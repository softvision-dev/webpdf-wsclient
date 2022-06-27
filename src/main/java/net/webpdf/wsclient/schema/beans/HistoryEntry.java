package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.session.documents.rest.manager.DocumentManager;
import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * An instance of {@link HistoryEntry} is intended to wrap and simplify information about a previous state of a given
 * {@link Document}, it shall provide an ID for that history entry, allowing to revert to that state of the linked
 * document.<br>
 * Such {@link HistoryEntry} objects can be received via and are managed by a {@link RestSession}´s
 * {@link DocumentManager}.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "historyEntry")
public class HistoryEntry implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int id;
    private boolean active;
    private @Nullable String fileName;
    private @Nullable String operation;
    private @Nullable String dateTime;

    /**
     * Returns the ID of the history entry.
     *
     * @return The ID of the history entry.
     */
    @XmlElement
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the history entry.
     *
     * @param id The ID, that shall be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns true, if the history entry is currently active.
     *
     * @return True, if the history entry is currently active.
     */
    @XmlElement
    public boolean isActive() {
        return active;
    }

    /**
     * When set to {@code true}, the history entry is currently active.
     *
     * @param active {@code true}, if the history entry is currently active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the name of the file, that has been changed.
     *
     * @return The name of the file, that has been changed.
     */
    @XmlElement
    public @Nullable String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file, that has been changed.
     *
     * @param fileName The name of the file, that has been changed.
     */
    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the name of the operation, that has been executed.
     *
     * @return The name of the operation, that has been executed.
     */
    @XmlElement
    public @Nullable String getOperation() {
        return operation;
    }

    /**
     * Sets the name of the operation, that has been executed.
     *
     * @param operation The name of the operation, that has been executed.
     */
    public void setOperation(@Nullable String operation) {
        this.operation = operation;
    }

    /**
     * Returns the timestamp of the history entry.
     *
     * @return The timestamp of the history entry.
     */
    @XmlElement
    public @Nullable String getDateTime() {
        return dateTime;
    }

    /**
     * Sets the timestamp of the history entry.
     *
     * @param dateTime The timestamp of the history entry.
     */
    public void setDateTime(@Nullable String dateTime) {
        this.dateTime = dateTime;
    }

}
