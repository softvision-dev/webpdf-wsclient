package net.webpdf.wsclient.session;

import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * <p>
 * An instance of {@link Session} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 *
 * @param <T_DOCUMENT> The {@link Document} type used by this {@link Session}.
 */
@SuppressWarnings("unused")
public interface Session<T_DOCUMENT extends Document> extends AutoCloseable {

    /**
     * Returns the currently set {@link TLSContext}. ({@code null} in case this is representing a HTTP {@link Session})
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
     * Returns the {@link DataFormat} accepted by this {@link Session}.
     *
     * @return The {@link DataFormat} accepted by this {@link Session}.
     */
    @Nullable DataFormat getDataFormat();

    /**
     * Returns the {@link Credentials} authorizing this session.
     *
     * @return The {@link Credentials} authorizing this session.
     */
    @Nullable Credentials getCredentials();

    /**
     * Sets the {@link Credentials} authorizing this session.
     *
     * @param userCredentials The {@link Credentials} authorizing this session.
     */
    void setCredentials(@Nullable Credentials userCredentials);

    /**
     * Close the {@link Session}.
     *
     * @throws java.io.IOException Shall be thrown, if closing the {@link Session} failed.
     */
    @Override
    void close() throws IOException;

}
