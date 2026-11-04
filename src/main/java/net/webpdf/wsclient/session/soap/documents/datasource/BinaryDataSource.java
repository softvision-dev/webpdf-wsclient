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
     * @throws IOException Shall be thrown, should the temp {@link File} not be creatable or writable.
     */
    public BinaryDataSource(@NotNull InputStream inputStream) throws IOException {
        // Files.createTempFile restricts the file to its owner where the file system supports POSIX permissions,
        // the document content must not be readable by other local users.
        File createdFile = Files.createTempFile("SOAPClient", null).toFile();
        createdFile.deleteOnExit();
        try {
            FileUtils.copyInputStreamToFile(inputStream, createdFile);
        } catch (IOException | RuntimeException ex) {
            // No instance exists that could be closed later, the partially written file must be removed here.
            if (!createdFile.delete()) {
                ex.addSuppressed(new IOException("Failed to delete temp file " + createdFile));
            }
            throw ex;
        }
        this.tempFile = createdFile;
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
     * Deletes the underlying temp {@link File}. Closing an already closed {@link BinaryDataSource} has no effect.
     *
     * @throws ResultException Shall be thrown, when deleting the temp file failed.
     */
    @Override
    public void close() throws ResultException {
        // deleteIfExists keeps a repeated close (e.g. an explicit close inside try-with-resources) free of errors.
        try {
            Files.deleteIfExists(this.tempFile.toPath());
        } catch (IOException ex) {
            throw new ClientResultException(Error.FAILED_TO_CLOSE_DATA_SOURCE, ex);
        }
    }

}
