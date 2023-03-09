package net.webpdf.wsclient.session.soap.documents.datasource;

import org.jetbrains.annotations.NotNull;

import jakarta.activation.DataSource;

import java.io.*;

/**
 * An instance of {@link BinaryDataSource} prepares the data of a given resource for further processing and usage
 * by a webPDF webservice call.
 */
public class BinaryDataSource implements DataSource {

    private final byte @NotNull [] data;

    /**
     * Prepares the given {@link InputStream} for further processing.
     *
     * @param inputStream an {@link InputStream} containing the resource.
     */
    public BinaryDataSource(@NotNull InputStream inputStream) throws IOException {
        this.data = inputStream.readAllBytes();
    }

    /**
     * Returns the {@link InputStream} containing the resource data represented by this data source.
     *
     * @return The {@link InputStream} containing the resource data.
     */
    @Override
    public @NotNull InputStream getInputStream() {
        return new ByteArrayInputStream(this.data);
    }

    /**
     * Writing to the managed resource is unsupported.
     *
     * @return an {@link OutputStream}
     */
    @Override
    public @NotNull OutputStream getOutputStream() {
        throw new UnsupportedOperationException("A binary data source does not represent a valid output target.");
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