package net.webpdf.wsclient.session;

import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import org.apache.hc.core5.http.NameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Returns the currently set {@link TLSContext}. ({@code null} in case this is representing an HTTP {@link Session})
     *
     * @return The currently set {@link TLSContext}.
     */
    @Nullable TLSContext getTlsContext();

    /**
     * Returns the currently set {@link ProxyConfiguration}.
     *
     * @return The currently set {@link ProxyConfiguration}.
     */
    @Nullable ProxyConfiguration getProxy();

    /**
     * Set a {@link ProxyConfiguration}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set.
     */
    void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException;

    /**
     * Returns the {@link WebServiceProtocol} this {@link Session} is using.
     *
     * @return The {@link WebServiceProtocol} this {@link Session} is using.
     */
    @NotNull WebServiceProtocol getWebServiceProtocol();

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
     * Provides {@link AuthMaterial} for the authorization of the {@link Session}´s requests, using the
     * {@link Session}´s {@link AuthProvider}.
     *
     * @return {@link AuthMaterial} for the authorization of the {@link Session}´s requests.
     * @throws ResultException Shall be thrown, should the determination of {@link AuthMaterial} fail.
     */
    @NotNull AuthMaterial getAuthMaterial() throws ResultException;

    /**
     * <p>
     * Returns the {@link Session}´s skew time.<br>
     * The skew time helps to avoid using expired tokens. The returned value (in seconds) is subtracted from the
     * expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @return The {@link Session}´s token refresh skew time in seconds.
     */
    int getSkewTime();

    /**
     * <p>
     * Sets the {@link Session}´s skew time.<br>
     * The skew time helps to avoid using expired tokens. The returned value (in seconds) is subtracted from the
     * expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @param skewTime The {@link Session}´s token refresh skew time in seconds.
     */
    @SuppressWarnings("unused")
    void setSkewTime(int skewTime);

    /**
     * Close the {@link Session}.
     *
     * @throws ResultException Shall be thrown, if closing the {@link Session} failed.
     */
    @Override
    void close() throws ResultException;

}
