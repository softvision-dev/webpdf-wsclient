package net.webpdf.wsclient.documents;

import java.net.URI;

public interface Document {

    /**
     * Returns the {@link URI} of the document source.
     *
     * @return The {@link URI} of the document source.
     */
    URI getSource();
}
