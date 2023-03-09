package net.webpdf.wsclient.session.soap.documents;

import jakarta.activation.DataSource;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.documents.AbstractDocument;
import net.webpdf.wsclient.session.soap.documents.datasource.BinaryDataSource;
import net.webpdf.wsclient.session.soap.documents.datasource.PathDataSource;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import java.io.*;
import java.net.URI;

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

    private final @Nullable DataSource source;
    private @Nullable DataHandler result;

    /**
     * <p>
     * Creates a handle for a {@link SoapWebService} output without providing a source document.<br>
     * This shall always return {@code null}, in case {@link #getSourceDataHandler()} is called and is recommended for
     * webservices, that don't require a source document. (Such as the URL-Converter.)
     * </p>
     * <p>
     * <b>Be aware:</b> Most webservices require a source document, with few exceptions. Before using this,
     * make sure that this is valid for the {@link WebService} call you intend to execute.
     * </p>
     */
    public SoapWebServiceDocument() {
        super(null);
        this.source = null;
    }

    /**
     * <p>
     * Manages a {@link SoapDocument} originating from the given {@link InputStream}.<br>
     * <b>Be aware:</b> This copies all remaining bytes from the given {@link InputStream} to an array, to create a
     * reusable {@link BinaryDataSource}.<br>
     * Especially for large files using this constructor is ill advised, using the alternative constructors
     * {@link #SoapWebServiceDocument(URI)} or {@link #SoapWebServiceDocument(File)} is always recommended.
     * </p>
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    public SoapWebServiceDocument(@NotNull InputStream source) throws ResultException {
        super(null);
        try {
            this.source = new BinaryDataSource(source);
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT, ex);
        }
    }

    /**
     * Manages a {@link SoapDocument} originating from the given {@link URI}.
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    public SoapWebServiceDocument(@NotNull URI source) throws ResultException {
        super(source);
        try {
            if (isFileSource()) {
                this.source = new PathDataSource(new File(source).toPath());
            } else {
                throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT);
            }
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT, ex);
        }
    }

    /**
     * Manages a {@link SoapDocument} originating from the given {@link File}.
     *
     * @param source The {@link File} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    public SoapWebServiceDocument(@NotNull File source) throws ResultException {
        super(source.toURI());
        try {
            this.source = new PathDataSource(source.toPath());
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT, ex);
        }
    }

    /**
     * Returns a {@link DataHandler} for the document's source - may return {@code null} in case no source document
     * has been selected.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    @Override
    public @Nullable DataHandler getSourceDataHandler() {
        return this.source != null
                ? new DataHandler(this.source)
                : null;
    }

    /**
     * Sets the result {@link DataHandler}, that represents the current result of a {@link SoapWebService} call for
     * this {@link SoapDocument}.
     *
     * @param result The result {@link DataHandler}, that represents the current result of a {@link SoapWebService}
     *               call.
     */
    @Override
    public void setResult(@Nullable DataHandler result) {
        this.result = result;
    }

    /**
     * Returns the result {@link DataHandler}, that represents the current result of a {@link SoapWebService} call for
     * this {@link SoapDocument}. (May return {@code null}, should such a result not exist.)
     *
     * @return The result {@link DataHandler}, that represents the current result of a {@link SoapWebService}
     * call.
     */
    @Override
    public @Nullable DataHandler getResult() {
        return this.result;
    }

    /**
     * Attempts to write the response {@link SoapDocument} to the given {@link DataHandler}.
     *
     * @param target The {@link OutputStream} the response {@link SoapDocument} shall be written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    @Override
    public void writeResult(@NotNull OutputStream target) throws ResultException {
        if (this.result == null) {
            throw new ClientResultException(Error.SOAP_EXECUTION, new IOException("No document content available"));
        }

        try {
            this.result.writeTo(target);
        } catch (IOException ex) {
            throw new ClientResultException(Error.SOAP_EXECUTION, ex);
        }
    }

    /**
     * Attempts to write the {@link WebServiceProtocol#SOAP} response document to the given {@link File}.
     *
     * @param target The target {@link File} the {@link WebServiceProtocol#SOAP} response document shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    @Override
    public void writeResult(@NotNull File target) throws ResultException {
        try (OutputStream outputStream = new FileOutputStream(target)) {
            writeResult(outputStream);
        } catch (IOException ex) {
            throw new ClientResultException(Error.SOAP_EXECUTION, ex);
        }
    }

    /**
     * Closes and abandons a possibly existing result {@link DataHandler}.
     */
    @Override
    public void close() {
        try {
            if (source instanceof Closeable) {
                ((Closeable) source).close();
            }
            if (result instanceof Closeable) {
                ((Closeable) result).close();
                this.result = null;
            }
        } catch (IOException e) {
            //IGNORE
        }
    }

}
