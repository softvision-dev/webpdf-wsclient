package net.webpdf.wsclient.schema;

import net.webpdf.wsclient.schema.operation.FileDataSourceType;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * A class implementing {@link FileDataType} shall provide a common interface to access the binary data of some resource.
 * </p>
 * <p>
 * It shall be inherited by different JAXB stubs to provide a unified access to data.
 * </p>
 *
 * @param <T> The data format of the source data.
 */
public interface FileDataType<T> {

    /**
     * Return the file data source. (might be {@code null}, if the data source shall be defined using an URI or direct
     * byte value.)
     *
     * @return The file data source.
     */
    @Nullable FileDataSourceType getSource();

    /**
     * Set the file data source. (might be {@code null}, if the data source shall be defined using an URI or direct byte
     * value.)
     *
     * @param value The file data source.
     */
    void setSource(@Nullable FileDataSourceType value);

    /**
     * Returns {@code true}, if a file data source has been set.
     *
     * @return {@code true}, if a file data source has been set.
     */
    boolean isSetSource();

    /**
     * Returns the source URI. (might be {@code null}, if the data source shall be defined using a file data handler or
     * direct byte value.)
     *
     * @return The source URI.
     */
    @Nullable String getUri();

    /**
     * Set the source URI. (might be {@code null}, if the data source shall be defined using a file data handler or
     * direct byte value.)
     *
     * @param value The source URI.
     */
    void setUri(@Nullable String value);

    /**
     * Returns {@code true}, if a file source URI has been set.
     *
     * @return {@code true}, if a file source URI has been set.
     */
    boolean isSetUri();

    /**
     * Returns the value in the selected data format. (might be {@code null}, if the data source shall be defined using
     * a file data handler or an URI.)
     *
     * @return The source data.
     */
    @Nullable T getValue();

    /**
     * Set the value in the selected data format. (might be {@code null}, if the data source shall be defined using a
     * file data handler or an URI.)
     *
     * @param value The source data.
     */
    void setValue(@Nullable T value);

    /**
     * Returns {@code true}, if a direct byte value has been set.
     *
     * @return {@code true}, if a direct byte value has been set.
     */
    boolean isSetValue();

}
