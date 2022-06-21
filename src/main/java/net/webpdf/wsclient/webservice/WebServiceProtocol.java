package net.webpdf.wsclient.webservice;

/**
 * Selects the webservice protocol/paradigm used to connect the webPDF server and structure webservice requests.
 *
 * @see #SOAP
 * @see #REST
 */
public enum WebServiceProtocol {

    /**
     * The Simple Object Access Protocol.
     */
    SOAP,
    /**
     * The Representational State Transfer paradigm.
     */
    REST

}
