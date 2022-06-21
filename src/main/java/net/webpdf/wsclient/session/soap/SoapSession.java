package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.webservice.WebServiceProtocol;

/**
 * <p>
 * A class implementing {@link SoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection with
 * a webPDF server.
 * </p>
 *
 * @param <T_SOAP_DOCUMENT>> The {@link SoapDocument} type used by this {@link SoapSession}.
 */
public interface SoapSession<T_SOAP_DOCUMENT extends SoapDocument> extends Session<T_SOAP_DOCUMENT> {

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

}
