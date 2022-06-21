package net.webpdf.wsclient.documents.soap;

import net.webpdf.wsclient.documents.AbstractDocument;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;

/**
 * <p>
 * An instance of {@link SoapWebServiceDocument} represents a document, as it is processed/created by a
 * {@link SoapWebService}.
 * </p>
 * <p>
 * Such a {@link SoapWebServiceDocument} defines a source for the document, that shall be processed by a called
 * {@link SoapWebService} and a target resource for the document produced by that webservice.
 * </p>
 */
public class SoapWebServiceDocument extends AbstractDocument implements SoapDocument {

    private @Nullable InputStream inputStream;
    private boolean closeInput = false;
    private @Nullable OutputStream outputStream;
    private boolean closeOutput = false;

    /**
     * Manages a {@link SoapDocument} originating from the given {@link InputStream} and writing it´s request answer to
     * the given {@link OutputStream}.
     *
     * @param inputStream  The {@link InputStream} the {@link SoapDocument} is originating from.
     * @param outputStream The target {@link OutputStream} the request answer shall be written to.
     */
    public SoapWebServiceDocument(@Nullable InputStream inputStream, @Nullable OutputStream outputStream) {
        super(null);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Manages a {@link SoapDocument} originating from the given {@link URI} and writing it´s request answer to the
     * given {@link File}.
     *
     * @param source     The {@link URI} the {@link SoapDocument} is originating from.
     * @param targetFile The target {@link File} the request answer shall be written to.
     */
    public SoapWebServiceDocument(@Nullable URI source, @Nullable File targetFile) {
        super(source);
        try {
            if (source != null && isFileSource()) {
                this.inputStream = Files.newInputStream(new File(source).toPath());
                this.closeInput = true;
            }
            this.outputStream = targetFile != null ? Files.newOutputStream(targetFile.toPath()) : null;
            this.closeOutput = true;
        } catch (IOException ignored) {
        }
    }

    /**
     * Returns a {@link DataHandler} for the document's source.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    @Override
    public @Nullable DataHandler getSourceDataHandler() {
        return this.inputStream != null
                ? new DataHandler(new InputStreamDataSource(this.inputStream))
                : null;
    }

    /**
     * Attempts to write the response {@link SoapDocument} to the given {@link DataHandler}.
     *
     * @param resultDataHandler The {@link DataHandler} the response {@link SoapDocument} shall be written to.
     * @throws IOException Shall be thrown, should writing the result document fail.
     */
    @Override
    public void save(@Nullable DataHandler resultDataHandler) throws IOException {

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
     * Closes the source and target streams.
     */
    @Override
    public void close() {
        try {
            if (this.closeInput && this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.closeOutput && this.outputStream != null) {
                this.outputStream.close();
            }
        } catch (IOException e) {
            //IGNORE
        }
    }

}
