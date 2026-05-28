package net.webpdf.wsclient.session.rest.user;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.AuthUserCertificates;
import net.webpdf.wsclient.openapi.AuthUserCredentials;
import net.webpdf.wsclient.openapi.KeyStorePassword;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Implements {@link UserManager} by delegating all operations to the webPDF server
 * via {@link HttpRestRequest}.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} used by the currently active {@link RestSession}.
 */
public class AbstractUserManager<T_REST_DOCUMENT extends RestDocument>
        implements UserManager<T_REST_DOCUMENT> {

    private static final @NotNull String INFO_PATH = "authentication/user/info/";
    private static final @NotNull String CERTIFICATES_PATH = "authentication/user/certificates/";
    private static final @NotNull String CERTIFICATE_PASSWORDS_PATH = "authentication/user/certificates/passwords/";

    private final @NotNull RestSession<T_REST_DOCUMENT> session;

    /**
     * Initializes a {@link UserManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link UserManager} shall be created for.
     */
    AbstractUserManager(@NotNull RestSession<T_REST_DOCUMENT> session) {
        this.session = session;
    }

    /**
     * Returns the {@link RestSession} used by this {@link UserManager}.
     *
     * @return The {@link RestSession} used by this {@link UserManager}.
     */
    @Override
    public @NotNull RestSession<T_REST_DOCUMENT> getSession() {
        return session;
    }

    /**
     * Fetches the current user information live from the server.
     * <p>
     * Unlike {@link RestSession#getUser()}, which returns a snapshot from session creation time,
     * this method always performs a server request and reflects the current server state.
     * </p>
     *
     * @return The current {@link AuthUserCredentials} of the logged-in user.
     * @throws ResultException Shall be thrown, if the request failed.
     * @see RestSession#getUser()
     */
    @Override
    public @NotNull AuthUserCredentials fetchUserInfo() throws ResultException {
        AuthUserCredentials user = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, INFO_PATH)
                .executeRequest(AuthUserCredentials.class);
        if (user == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }
        return user;
    }

    /**
     * Reads the current certificate state of the logged-in user live from the server.
     * <p>
     * Unlike {@link RestSession#getCertificates()}, which returns a snapshot from session
     * creation time, this method always performs a server request.
     * </p>
     *
     * @return The current {@link AuthUserCertificates} of the logged-in user.
     * @throws ResultException Shall be thrown, if the request failed.
     * @see RestSession#getCertificates()
     */
    @Override
    public @NotNull AuthUserCertificates readCertificates() throws ResultException {
        AuthUserCertificates certificates = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, CERTIFICATES_PATH)
                .executeRequest(AuthUserCertificates.class);
        if (certificates == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }
        return certificates;
    }

    /**
     * Updates the passwords for a specific keystore of the currently logged-in user and returns
     * the updated {@link AuthUserCertificates}.
     * <p>
     * This method supersedes the deprecated {@link RestSession#updateCertificates(String, KeyStorePassword)}.
     * Note that unlike the deprecated method, this method does not update the certificate snapshot
     * returned by {@link RestSession#getCertificates()}.
     * </p>
     *
     * @param keystoreName     The name of the keystore whose passwords shall be updated.
     * @param keyStorePassword The {@link KeyStorePassword} containing the new passwords.
     * @return The updated {@link AuthUserCertificates} of the currently logged-in user.
     * @throws ResultException Shall be thrown, if the request failed.
     * @see RestSession#updateCertificates(String, KeyStorePassword)
     */
    @Override
    public @NotNull AuthUserCertificates updateCertificatePasswords(
            @NotNull String keystoreName, @NotNull KeyStorePassword keyStorePassword
    ) throws ResultException {
        AuthUserCertificates certificates = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.PUT, CERTIFICATE_PASSWORDS_PATH + keystoreName,
                        prepareHttpEntity(keyStorePassword))
                .executeRequest(AuthUserCertificates.class);
        if (certificates == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }
        return certificates;
    }

    private <T> @NotNull HttpEntity prepareHttpEntity(@NotNull T parameter) throws ResultException {
        try {
            return new StringEntity(SerializeHelper.toJSON(parameter),
                    ContentType.create(DataFormat.JSON.getMimeType(), StandardCharsets.UTF_8));
        } catch (UnsupportedCharsetException ex) {
            throw new ClientResultException(Error.XML_OR_JSON_CONVERSION_FAILURE, ex);
        }
    }

}
