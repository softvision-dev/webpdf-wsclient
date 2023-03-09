package net.webpdf.wsclient.session.soap.documents.datasource;

import jakarta.activation.DataSource;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * An instance of {@link PathDataSource} prepares the data of a given resource for further processing and usage
 * by a webPDF webservice call.
 */
public class PathDataSource implements DataSource {

    private final @NotNull Path path;

    /**
     * Prepares the given {@link Path} for further processing.
     *
     * @param path an {@link Path} containing the resource.
     */
    public PathDataSource(@NotNull Path path) throws IOException {
        this.path = path;
    }

    /**
     * Returns the {@link InputStream} containing the resource at the selected {@link Path}.
     *
     * @return The {@link InputStream} containing the resource data.
     * @throws IOException Shall be thrown, should the {@link Path} not be readable.
     */
    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.path);
    }

    /**
     * Returns the {@link OutputStream} to the selected {@link Path}.
     *
     * @return The {@link OutputStream} to the selected {@link Path}.
     * @throws IOException Shall be thrown, should the {@link Path} not be writable.
     */
    @Override
    public @NotNull OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.path);
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
        return this.path.getFileName().toString();
    }

}
