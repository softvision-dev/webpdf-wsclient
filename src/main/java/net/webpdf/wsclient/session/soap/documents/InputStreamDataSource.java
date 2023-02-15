package net.webpdf.wsclient.session.soap.documents;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.jetbrains.annotations.NotNull;

import jakarta.activation.DataSource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An instance of {@link InputStreamDataSource} prepares the data of a given resource for further processing and usage
 * by a webPDF webservice call.
 */
public class InputStreamDataSource implements DataSource {

    private final @NotNull InputStream inputStream;

    /**
     * Prepares the given resource for further processing.
     *
     * @param inputStream an {@link InputStream} containing the resource.
     */
    public InputStreamDataSource(@NotNull InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Returns the {@link InputStream} containing the resource data represented by this data source.
     *
     * @return The {@link InputStream} containing the resource data.
     */
    @Override
    public @NotNull InputStream getInputStream() {
        return CloseShieldInputStream.wrap(this.inputStream);
    }

    /**
     * Writing to the managed resource is unsupported.
     *
     * @return an {@link OutputStream}
     */
    @Override
    public @NotNull OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the MIME-type of the contained resource.
     *
     * @return The MIME-type of the contained resource.
     */
    @Override
    public @NotNull String getContentType() {
        return "application/octet-stream";
    }

    /**
     * Returns the name of the contained resource.
     *
     * @return The name of the contained resource.
     */
    @Override
    public @NotNull String getName() {
        return "";
    }

}