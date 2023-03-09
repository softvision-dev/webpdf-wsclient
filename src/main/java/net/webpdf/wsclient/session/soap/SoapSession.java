package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.factory.SoapDocumentFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

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
     * Provides a {@link SoapDocumentFactory} for the creation of session appropriate {@link SoapDocument}s.
     *
     * @return A {@link SoapDocumentFactory} for the creation of session appropriate {@link SoapDocument}s.
     */
    @NotNull SoapDocumentFactory<T_SOAP_DOCUMENT> createSoapDocumentFactory();

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
