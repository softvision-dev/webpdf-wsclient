package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import org.apache.http.auth.Credentials;

import java.io.IOException;
import java.net.URI;

public interface Session extends AutoCloseable {

    /**
     * Returns the currently set TLS context.
     * @return The currently set TLS context.
     */
    TLSContext getTlsContext();

    /**
     * Terminates the current session.
     *
     * @throws IOException an {@link IOException}
     */
    @Override
    void close() throws IOException;

    /**
     * Returns the {@link WebServiceProtocol} of this session.
     *
     * @return The {@link WebServiceProtocol} of this session.
     */
    WebServiceProtocol getWebServiceProtocol();

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath The location of the webservice interface on the webPDF server.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    URI getURI(String subPath) throws ResultException;

    /**
     * Returns the {@link DataFormat} accepted by this session.
     *
     * @return The {@link DataFormat} accepted by this session.
     */
    DataFormat getDataFormat();

    /**
     * Returns the {@link Credentials} authorizing this session.
     *
     * @return The {@link Credentials} authorizing this session.
     */
    Credentials getCredentials();

    /**
     * Sets the {@link Credentials} authorizing this session.
     *
     * @param userCredentials The {@link Credentials} authorizing this session.
     */
    void setCredentials(Credentials userCredentials);

}
