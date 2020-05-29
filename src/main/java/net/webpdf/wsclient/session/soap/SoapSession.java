package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.Session;

public interface SoapSession<T_SOAP_DOCUMENT extends SoapDocument> extends Session<T_SOAP_DOCUMENT> {

    /**
     * When returning true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @return returns true, if a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the server.
     */
    boolean isUseLocalWsdl();

    /**
     * When set to true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @param useLocalWsdl True, to use a wsdl stored on the local file system, instead of the WSDL published by the server.
     */
    @SuppressWarnings({"SameParameterValue"})
    void setUseLocalWsdl(boolean useLocalWsdl);
}
