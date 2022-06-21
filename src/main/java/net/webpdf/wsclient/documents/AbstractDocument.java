package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.webservice.WebService;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * An instance of {@link AbstractDocument} represents a document as it is processed/created by a {@link WebService} or
 * uploaded to the webPDF server.
 */
public abstract class AbstractDocument implements Document {

    private final @Nullable URI source;
    private final boolean fileSource;

    /**
     * Prepares the given {@link URI} as a new {@link Document} for the processing by webPDF webservices.
     *
     * @param source The {@link URI} source of the document.
     */
    public AbstractDocument(@Nullable URI source) {
        this.source = source;
        this.fileSource = source != null && source.getScheme().startsWith("file");
    }

    /**
     * Returns the {@link URI} of the document.
     *
     * @return The {@link URI} of the document.
     */
    @Override
    public @Nullable URI getSource() {
        return this.source;
    }

    /**
     * Returns {@code true}, if the {@link URI} of the document points to a file.
     *
     * @return {@code true}, if the {@link URI} of the document points to a file.
     */
    @Override
    public boolean isFileSource() {
        return fileSource;
    }

}
