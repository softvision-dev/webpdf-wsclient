package net.webpdf.wsclient.session.documents;

import net.webpdf.wsclient.webservice.WebService;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * A class implementing {@link Document} represents a document, as it is processed/created by a {@link WebService} or
 * uploaded to the webPDF server.
 */
public interface Document {

    /**
     * Returns the {@link URI} of the document.
     *
     * @return The {@link URI} of the document.
     */
    @Nullable URI getSource();

    /**
     * Returns {@code true}, if the {@link URI} of the document points to a file.
     *
     * @return {@code true}, if the {@link URI} of the document points to a file.
     */
    boolean isFileSource();

}
