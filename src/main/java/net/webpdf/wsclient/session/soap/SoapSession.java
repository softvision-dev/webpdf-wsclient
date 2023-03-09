package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.datasource.BinaryDataSource;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 * <p>
 * A class implementing {@link SoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection with
 * a webPDF server.
 * </p>
 *
 * @param <T_SOAP_DOCUMENT> The {@link RestDocument} type used by this {@link RestSession}
 */
public interface SoapSession<T_SOAP_DOCUMENT extends SoapDocument> extends Session {

    /**
     * When returning {@code true}, a wsdl stored on the local file system shall be used instead of the WSDL published
     * by the webPDF server.
     *
     * @return {@code true}, if a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the webPDF server.
     */
    boolean isUseLocalWsdl();

    /**
     * When set to {@code true}, a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the webPDF server.
     *
     * @param useLocalWsdl {@code true}, to use a wsdl stored on the local file system, instead of the WSDL published
     *                     by the webPDF server.
     */
    @SuppressWarnings({"SameParameterValue"})
    void setUseLocalWsdl(boolean useLocalWsdl);

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
    @NotNull T_SOAP_DOCUMENT createDocument();

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
    @SuppressWarnings("unused")
    @NotNull T_SOAP_DOCUMENT createDocument(@NotNull InputStream source) throws ResultException;

    /**
     * Creates a {@link SoapDocument} originating from the given {@link URI}.
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @NotNull T_SOAP_DOCUMENT createDocument(@NotNull URI source) throws ResultException;

    /**
     * Creates a {@link SoapDocument} originating from the given {@link File}.
     *
     * @param source The {@link File} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @NotNull T_SOAP_DOCUMENT createDocument(@NotNull File source) throws ResultException;

    /**
     * Creates a matching {@link SoapWebService} instance to execute a webPDF operation for the current session.
     *
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link SoapWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link SoapWebService} creation failed.
     */
    @NotNull <T_WEBSERVICE extends SoapWebService<?, ?, ?>> T_WEBSERVICE createWSInstance(
            @NotNull WebServiceType webServiceType) throws ResultException;

}
