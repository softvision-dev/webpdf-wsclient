package net.webpdf.wsclient.documents;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

abstract class AbstractDocument implements Document {

    @Nullable
    private URI source;
    private boolean fileSource;

    /**
     * Prepares a document for webservice processing.
     *
     * @param source The source the document shall be loaded from.
     */
    AbstractDocument(@Nullable URI source) {
        this.source = source;
        this.fileSource = source != null && source.getScheme().startsWith("file");
    }

    /**
     * Returns the {@link URI} of the document source.
     *
     * @return The {@link URI} of the document source.
     */
    @Override
    @Nullable
    public URI getSource() {
        return this.source;
    }

    /**
     * Returns true if the {@link URI} of the document source points to a file.
     *
     * @return True if the {@link URI} of the document source points to a file.
     */
    public boolean isFileSource() {
        return fileSource;
    }

}
