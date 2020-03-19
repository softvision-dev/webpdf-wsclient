package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "historyEntry")
public class HistoryEntryBean implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int id;
    private boolean active;
    @Nullable
    private String fileName;
    @Nullable
    private String operation;
    @Nullable
    private String dateTime;

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
     * When set to true, the history entry is currently active.
     *
     * @param active True, if the history entry is currently active.
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
    @Nullable
    public String getFileName() {
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
    @Nullable
    public String getOperation() {
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
    @Nullable
    public String getDateTime() {
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
