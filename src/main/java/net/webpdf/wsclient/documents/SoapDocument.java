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

    public SoapDocument(InputStream inputStream, OutputStream outputStream) {
        super(null);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

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

    public DataHandler getSourceDataHandler() {
        return this.inputStream != null
                ? new DataHandler(new InputStreamDataSource(this.inputStream))
                : null;
    }

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

    @Override
    public void close() {
        if (this.closeInput) IOUtils.closeQuietly(this.inputStream);
        if (this.closeOutput) IOUtils.closeQuietly(this.outputStream);
    }
}
