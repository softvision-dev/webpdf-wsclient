package net.webpdf.wsclient.session.soap.documents;

import jakarta.activation.DataHandler;
import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * A class implementing {@link SoapDocument} represents a document, as it is processed/created by a
 * {@link SoapWebService}.
 * </p>
 * <p>
 * Such a {@link SoapDocument} defines a source for the document, that shall be processed by a called
 * {@link SoapWebService} and a target resource for the document produced by that webservice.
 * </p>
 */
public interface SoapDocument extends Document, AutoCloseable {

    /**
     * Returns a {@link DataHandler} for the document's source.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    @Nullable DataHandler getSourceDataHandler();

    /**
     * Attempts to write the {@link WebServiceProtocol#SOAP} response document to the given {@link DataHandler}.
     *
     * @param resultDataHandler The {@link DataHandler} the {@link WebServiceProtocol#SOAP} response document shall be
     *                          written to.
     * @throws IOException Shall be thrown, should writing the result document fail.
     */
    void save(@Nullable DataHandler resultDataHandler) throws IOException;

}
