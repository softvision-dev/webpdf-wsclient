package net.webpdf.wsclient.documents;

import java.net.URI;

abstract class AbstractDocument implements Document {

    private URI source;
    private boolean fileSource;

    AbstractDocument(URI source) {
        this.source = source;
        this.fileSource = source != null && source.getScheme().startsWith("file");
    }

    @Override
    public URI getSource() {
        return this.source;
    }

    public boolean isFileSource() {
        return fileSource;
    }

}
