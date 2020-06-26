package net.webpdf.wsclient.schema.beans.limits;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserLimits.class, name = "user"),
    @JsonSubTypes.Type(value = AnonymousLimits.class, name = "anonymous")
})
@SuppressWarnings("unused")
public interface Limits extends Serializable {

    /**
     * Returns the upload limit of the affected user type.
     *
     * @return The upload limit of the affected user type.
     */
    @XmlElement
    int getUploadLimit();

    /**
     * Sets the upload limit of the affected user type.
     *
     * @param uploadLimit The upload limit of the affected user type.
     */
    void setUploadLimit(int uploadLimit);

    /**
     * Returns the maximum number of files for the affected user type.
     *
     * @return The maximum number of files for the affected user type.
     */
    @XmlElement
    int getMaxFiles();

    /**
     * Sets the maximum number of files for the affected user type.
     *
     * @param maxFiles The maximum number of files for the affected user type.
     */
    void setMaxFiles(int maxFiles);

    /**
     * Returns the disk space limit of the affected user type.
     *
     * @return The disk space limit of the affected user type.
     */
    @XmlElement
    int getDiskSpaceLimit();

    /**
     * Sets the disk space limit of the affected user type.
     *
     * @param diskSpaceLimit The disk space limit of the affected user type.
     */
    void setDiskSpaceLimit(int diskSpaceLimit);

    /**
     * Whether or not usage limits exist for the user type.
     *
     * @return True, if limits have been set for the affected user type.
     */
    boolean hasLimits();
}
