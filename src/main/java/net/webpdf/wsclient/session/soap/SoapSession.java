package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.credentials.token.Token;
import net.webpdf.wsclient.session.credentials.token.TokenProvider;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.client5.http.auth.Credentials;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * A class implementing {@link SoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection with
 * a webPDF server.
 * </p>
 *
 * @param <T_SOAP_DOCUMENT> The {@link SoapDocument} type used by this {@link SoapSession}.
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

    /**
     * Uses the given {@link Token} as the {@link Credentials} authorizing this session.
     *
     * @param token The {@link Token} authorizing this session.
     */
    void setCredentials(@Nullable Token token);

    /**
     * Uses the given {@link TokenProvider}, to produce a {@link Token}, that shall be used as the {@link Credentials}
     * authorizing this session.
     *
     * @param tokenProvider The {@link TokenProvider} creating the {@link Token} authorizing this session.
     */
    void setCredentials(@Nullable TokenProvider<?> tokenProvider) throws IOException;

}
