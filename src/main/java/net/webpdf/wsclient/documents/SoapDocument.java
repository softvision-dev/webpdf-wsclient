package net.webpdf.wsclient.documents;

import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import java.io.*;
import java.net.URI;

public class SoapDocument extends AbstractDocument implements AutoCloseable {

    private InputStream inputStream;
    private boolean closeInput = false;
    private OutputStream outputStream;
    private boolean closeOutput = false;

    /**
     * Manages a SOAP document originating from the given {@link InputStream} and writing it's request answer to the
     * given {@link OutputStream}.
     *
     * @param inputStream  The {@link InputStream} the SOAP document is originating from.
     * @param outputStream The target {@link OutputStream} the request answer shall be written to.
     */
    public SoapDocument(InputStream inputStream, OutputStream outputStream) {
        super(null);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Manages a SOAP document originating from the given {@link URI} and writing it's request answer to the
     * given {@link File}.
     *
     * @param source     The {@link URI} the SOAP document is originating from.
     * @param targetFile The target {@link File} the request answer shall be written to.
     */
    public SoapDocument(URI source, File targetFile) {
        super(source);
        try {
            if (isFileSource()) {
                this.inputStream = new FileInputStream(new File(source));
                this.closeInput = true;
            }
            this.outputStream = targetFile != null ? new FileOutputStream(targetFile) : null;
            this.closeOutput = true;
        } catch (IOException ignored) {
        }
    }

    /**
     * Returns a {@link DataHandler} for the document's source.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    public DataHandler getSourceDataHandler() {
        return this.inputStream != null
                   ? new DataHandler(new InputStreamDataSource(this.inputStream))
                   : null;
    }

    /**
     * Attempts to write the SOAP response document to the given {@link DataHandler}.
     *
     * @param resultDataHandler The {@link DataHandler} the SOAP response document shall be written to.
     * @throws IOException a {@link IOException}
     */
    public void save(DataHandler resultDataHandler) throws IOException {

        if (this.outputStream == null) {
            throw new IOException("No output stream available");
        }

        if (resultDataHandler == null) {
            throw new IOException("No document content available");
        }

        resultDataHandler.writeTo(this.outputStream);

        if (resultDataHandler instanceof Closeable) {
            ((Closeable) resultDataHandler).close();
        }
    }

    /**
     * Closes source and target stream.
     */
    @Override
    public void close() {
        if (this.closeInput) IOUtils.closeQuietly(this.inputStream);
        if (this.closeOutput) IOUtils.closeQuietly(this.outputStream);
    }
}
