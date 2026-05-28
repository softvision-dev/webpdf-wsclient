package net.webpdf.wsclient.session.rest.user;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.AuthUserCertificates;
import net.webpdf.wsclient.openapi.AuthUserCredentials;
import net.webpdf.wsclient.openapi.KeyStorePassword;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class implementing {@link UserManager} provides access to authentication user operations
 * for the currently logged-in user of a {@link RestSession}.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} used by the currently active {@link RestSession}.
 */
@SuppressWarnings("unused")
public interface UserManager<T_REST_DOCUMENT extends RestDocument> {

    /**
     * Returns the {@link RestSession} used by this {@link UserManager}.
     *
     * @return The {@link RestSession} used by this {@link UserManager}.
     */
    @NotNull RestSession<T_REST_DOCUMENT> getSession();

    /**
     * Fetches the current user information live from the server.
     * <p>
     * Unlike {@link RestSession#getUser()}, which returns a snapshot from session creation time,
     * this method always performs a server request and reflects the current server state —
     * for example after permission changes that occurred during the active session.
     * </p>
     *
     * @return The current {@link AuthUserCredentials} of the logged-in user.
     * @throws ResultException Shall be thrown, if the request failed.
     * @see RestSession#getUser()
     */
    @NotNull AuthUserCredentials fetchUserInfo() throws ResultException;

    /**
     * Reads the current certificate state of the logged-in user live from the server.
     * <p>
     * Unlike {@link RestSession#getCertificates()}, which returns a snapshot from session
     * creation time, this method always performs a server request and reflects the current
     * certificate state without modifying any passwords.
     * </p>
     *
     * @return The current {@link AuthUserCertificates} of the logged-in user.
     * @throws ResultException Shall be thrown, if the request failed.
     * @see RestSession#getCertificates()
     */
    @NotNull AuthUserCertificates readCertificates() throws ResultException;

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
    @NotNull AuthUserCertificates updateCertificatePasswords(
            @NotNull String keystoreName, @NotNull KeyStorePassword keyStorePassword
    ) throws ResultException;

}
