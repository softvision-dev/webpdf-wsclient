package net.webpdf.wsclient.session;

import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.ServerContextSettings;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.hc.core5.http.NameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
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
     * Returns the {@link ServerContextSettings} used for this session.
     *
     * @return The {@link ServerContextSettings} used for this session.
     */
    @NotNull ServerContextSettings getServerContext();

    /**
     * Provides {@link AuthMaterial} for the authorization of the {@link Session}´s requests, using the
     * {@link Session}´s {@link AuthProvider}.
     *
     * @return {@link AuthMaterial} for the authorization of the {@link Session}´s requests.
     * @throws ResultException Shall be thrown, should the determination of {@link AuthMaterial} fail.
     */
    @NotNull AuthMaterial getAuthMaterial() throws ResultException;

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
     * <p>
     * Returns (and initializes) the {@link Session}´s {@link SSLContext}.
     * </p>
     * <p>
     * <b>Information:</b> Actually this is not exactly a "SSL" context, but a "TLS" context.
     * TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is no longer
     * supported by the webPDF wsclient, as it is obsolete and insecure.
     * </p>
     *
     * @return The resulting {@link SSLContext}.
     */
    @Nullable SSLContext getTLSContext() throws ResultException;

    /**
     * Close the {@link Session}.
     *
     * @throws ResultException Shall be thrown, if closing the {@link Session} failed.
     */
    @Override
    void close() throws ResultException;

}
