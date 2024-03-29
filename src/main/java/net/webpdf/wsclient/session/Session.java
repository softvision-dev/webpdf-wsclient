package net.webpdf.wsclient.session;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.SessionContextSettings;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.core5.http.NameValuePair;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.List;

/**
 * <p>
 * An instance of {@link Session} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 */
public interface Session extends AutoCloseable {

    /**
     * Returns the {@link WebServiceProtocol} this {@link Session} is using.
     *
     * @return The {@link WebServiceProtocol} this {@link Session} is using.
     */
    @NotNull WebServiceProtocol getWebServiceProtocol();

    /**
     * Returns the {@link SessionContextSettings} used for this session.
     *
     * @return The {@link SessionContextSettings} used for this session.
     */
    @NotNull SessionContextSettings getSessionContext();

    /**
     * Provides the {@link AuthProvider} for the authorization of the {@link Session}´s requests.
     *
     * @return {@link AuthProvider} for the authorization of the {@link Session}´s requests.
     */
    @NotNull AuthProvider getAuthProvider();

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath The location of the webservice interface on the webPDF server.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull URI getURI(String subPath) throws ResultException;

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath    The location of the webservice interface on the webPDF server.
     * @param parameters Additional Get parameters.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull URI getURI(@NotNull String subPath, List<NameValuePair> parameters) throws ResultException;

    /**
     * Close the {@link Session}.
     *
     * @throws ResultException Shall be thrown, if closing the {@link Session} failed.
     */
    @Override
    void close() throws ResultException;

}
