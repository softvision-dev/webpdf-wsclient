package net.webpdf.wsclient.session.soap.documents;

import jakarta.activation.DataHandler;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.OutputStream;

/**
 * <p>
 * A class implementing {@link SoapDocument} represents a document, as it is processed/created by a
 * {@link SoapWebService}.
 * </p>
 * <p>
 * Such a {@link SoapDocument} defines a source for the document, that shall be processed by a called
 * {@link SoapWebService} and a target resource for the document produced by that webservice.
 * </p>
 * <p>
 * <b>Be aware:</b> A {@link SoapDocument} is using {@link DataHandler}s, that might require closing to prevent resource
 * leaks. You should always {@link #close()} {@link SoapDocument}s.
 * </p>
 */
public interface SoapDocument extends Document, AutoCloseable {

    /**
     * Returns a {@link DataHandler} for the document's source - may return {@code null} in case no source document
     * has been selected.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    @Nullable DataHandler getSourceDataHandler();

    /**
     * Sets the result {@link DataHandler}, that represents the current result of a {@link SoapWebService} call for
     * this {@link SoapDocument}.
     *
     * @param result The result {@link DataHandler}, that represents the current result of a {@link SoapWebService}
     *               call.
     */
    void setResult(@Nullable DataHandler result);

    /**
     * Returns the result {@link DataHandler}, that represents the current result of a {@link SoapWebService} call for
     * this {@link SoapDocument}. (May return {@code null}, should such a result not exist.)
     *
     * @return The result {@link DataHandler}, that represents the current result of a {@link SoapWebService}
     * call.
     */
    @Nullable DataHandler getResult();

    /**
     * Attempts to write the {@link WebServiceProtocol#SOAP} response document to the given {@link OutputStream}.
     *
     * @param target The target {@link OutputStream} the {@link WebServiceProtocol#SOAP} response document shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    void writeResult(@NotNull OutputStream target) throws ResultException;

    /**
     * Attempts to write the {@link WebServiceProtocol#SOAP} response document to the given {@link File}.
     *
     * @param target The target {@link File} the {@link WebServiceProtocol#SOAP} response document shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    void writeResult(@NotNull File target) throws ResultException;

    /**
     * Closes the documents {@link DataHandler}s.
     *
     * @throws ResultException Shall be thrown, when closing the underlying {@link DataHandler}s failed.
     */
    @Override
    void close() throws ResultException;

}
