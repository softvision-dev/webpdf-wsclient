package net.webpdf.wsclient.schema;

import net.webpdf.wsclient.schema.operation.FileDataSourceType;
import org.jetbrains.annotations.Nullable;

public interface FileDataType {

    /**
     * Return the file data source. (might be null, if the data source shall be defined using an URI or direct byte value.)
     *
     * @return The file data source.
     */
    @Nullable
    FileDataSourceType getSource();

    /**
     * Set the file data source. (might be null, if the data source shall be defined using an URI or direct byte value.)
     *
     * @param value The file data source.
     */
    void setSource(@Nullable FileDataSourceType value);

    /**
     * Returns true, if a file data source has been set.
     *
     * @return True, if a file data source has been set.
     */
    boolean isSetSource();

    /**
     * Returns the source URI. (might be null, if the data source shall be defined using a file data handler or direct byte value.)
     *
     * @return The source URI.
     */
    @Nullable
    String getUri();

    /**
     * Set the source URI. (might be null, if the data source shall be defined using a file data handler or direct byte value.)
     *
     * @param value The source URI.
     */
    void setUri(@Nullable String value);

    /**
     * Returns true, if a file source URI has been set.
     *
     * @return True, if a file source URI has been set.
     */
    boolean isSetUri();

    /**
     * Returns the direct byte value. (might be null, if the data source shall be defined using a file data handler or an URI.)
     *
     * @return The source data as a byte array.
     */
    @Nullable
    byte[] getValue();

    /**
     * Set the direct byte value. (might be null, if the data source shall be defined using a file data handler or an URI.)
     *
     * @param value The source data as a byte array.
     */
    void setValue(@Nullable byte[] value);

    /**
     * Returns true, if a direct byte value has been set.
     *
     * @return True, if a direct byte value has been set.
     */
    boolean isSetValue();

}
