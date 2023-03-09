package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.ServerContext;
import net.webpdf.wsclient.session.connection.ServerContextSettings;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.datasource.BinaryDataSource;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 * <p>
 * An instance of {@link SoapWebServiceSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public class SoapWebServiceSession extends AbstractSoapSession<SoapWebServiceDocument> {

    /**
     * Creates a new {@link SoapWebServiceSession} instance providing connection information, authorization objects for
     * a webPDF server-client {@link SoapSession}.
     *
     * @param serverContext The {@link ServerContext} initializing the {@link ServerContextSettings} of this
     *                      {@link SoapSession}.
     * @param authProvider  The {@link AuthProvider} for authentication/authorization of this {@link SoapSession}.
     * @throws ResultException Shall be thrown, in case establishing the session failed.
     */
    public SoapWebServiceSession(@NotNull ServerContext serverContext, @NotNull AuthProvider authProvider)
            throws ResultException {
        super(serverContext, authProvider);
    }

    /**
     * Creates a matching {@link SoapWebService} instance to execute a webPDF operation for the current session.
     *
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link SoapWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link SoapWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T_WEBSERVICE extends SoapWebService<?, ?, ?>> T_WEBSERVICE createWSInstance(
            @NotNull WebServiceType webServiceType) throws ResultException {
        return (T_WEBSERVICE) WebServiceFactory.createInstance(this, webServiceType);
    }

    /**
     * <p>
     * Creates a handle for a {@link SoapWebService} output without providing a source document.<br>
     * This is recommended for webservices, that don't require a source document. (Such as the URL-Converter.)
     * </p>
     * <p>
     * <b>Be aware:</b> Most webservices require a source document, with few exceptions. Before using this,
     * make sure that this is valid for the {@link WebService} call you intend to execute.
     * </p>
     */
    @Override
    public @NotNull SoapWebServiceDocument createDocument() {
        return new SoapWebServiceDocument();
    }

    /**
     * <p>
     * Creates a {@link SoapDocument} originating from the given {@link InputStream}.<br>
     * <b>Be aware:</b> This copies all remaining bytes from the given {@link InputStream} to an array, to create a
     * reusable {@link BinaryDataSource}.<br>
     * Especially for large files using this is ill advised, using the alternative constructors
     * {@link #createDocument(URI)} or {@link #createDocument(File)} is always recommended.
     * </p>
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createDocument(@NotNull InputStream source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

    /**
     * Creates a {@link SoapDocument} originating from the given {@link URI}.
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createDocument(@NotNull URI source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

    /**
     * Creates a {@link SoapDocument} originating from the given {@link File}.
     *
     * @param source The {@link File} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createDocument(@NotNull File source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

}
