package net.webpdf.wsclient.session.soap.documents.datasource;

import jakarta.activation.DataSource;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;

/**
 * An instance of {@link BinaryDataSource} prepares the data of a given resource for further processing and usage
 * by a webPDF webservice call.
 */
public class BinaryDataSource implements DataSource, AutoCloseable {

    private final @NotNull File tempFile;

    /**
     * Prepares the given {@link InputStream} for further processing.
     *
     * @param inputStream an {@link InputStream} containing the resource.
     */
    public BinaryDataSource(@NotNull InputStream inputStream) throws IOException {
        this.tempFile = File.createTempFile("SOAPClient", null);
        this.tempFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(inputStream, tempFile);
    }

    /**
     * Returns the {@link InputStream} containing the resource data represented by this data source.
     *
     * @return The {@link InputStream} containing the resource data.
     * @throws IOException Shall be thrown, should the temp {@link File} not be readable.
     */
    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.tempFile.toPath());
    }

    /**
     * Writing to the managed resource is unsupported.
     *
     * @return an {@link OutputStream}
     * @throws IOException Shall be thrown, should the temp {@link File} not be writable.
     */
    @Override
    public @NotNull OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.tempFile.toPath());
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

    /**
     * Deletes the underlying temp {@link File}.
     *
     * @throws ResultException Shall be thrown, when deleting the temp file failed.
     */
    @Override
    public void close() throws ResultException {
        if (!this.tempFile.delete()) {
            throw new ClientResultException(Error.FAILED_TO_CLOSE_DATA_SOURCE);
        }
    }

}
