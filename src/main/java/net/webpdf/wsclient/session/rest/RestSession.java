package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.AuthUserCertificates;
import net.webpdf.wsclient.openapi.AuthUserCredentials;
import net.webpdf.wsclient.openapi.KeyStorePassword;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.rest.administration.AdministrationManager;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.RestWebService;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

/**
 * <p>
 * A class implementing {@link RestSession} establishes and manages a {@link WebServiceProtocol#REST} connection with
 * a webPDF server.
 * </p>
 * <p>
 * <b>Information:</b> A {@link RestSession} provides simplified access to the uploaded {@link RestDocument}s via
 * a {@link DocumentManager}.
 * </p>
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type used by this {@link RestSession}
 */
public interface RestSession<T_REST_DOCUMENT extends RestDocument> extends Session {

    /**
     * Returns the {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     *
     * @return The {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     */
    @NotNull CloseableHttpClient getHttpClient();

    /**
     * Returns the active {@link DocumentManager} of this {@link RestSession}.
     *
     * @return The active {@link DocumentManager} of this {@link RestSession}.
     */
    @NotNull DocumentManager<T_REST_DOCUMENT> getDocumentManager();

    /**
     * This is a shortcut for {@link DocumentManager#uploadDocument(InputStream, String)} and uploads the given source
     * to the webPDF server.
     *
     * @param source The {@link InputStream} to upload to the webPDF server.
     * @return The uploaded {@link T_REST_DOCUMENT}.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @NotNull T_REST_DOCUMENT uploadDocument(@NotNull InputStream source, @NotNull String documentName)
            throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#uploadDocument(File)} and uploads the given source to the
     * webPDF server.
     *
     * @param source The {@link InputStream} to upload to the webPDF server.
     * @return The uploaded {@link T_REST_DOCUMENT}.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @NotNull T_REST_DOCUMENT uploadDocument(@NotNull File source) throws ResultException;

    /**
     * Returns the active {@link AdministrationManager} of this {@link RestSession}.
     *
     * @return The active {@link AdministrationManager} of this {@link RestSession}.
     */
    @SuppressWarnings("unused")
    @NotNull AdministrationManager<T_REST_DOCUMENT> getAdministrationManager();

    /**
     * Returns the {@link AuthUserCredentials} logged in via this {@link RestSession}.
     *
     * @return The {@link AuthUserCredentials} logged in via this {@link RestSession}.
     */
    @Nullable AuthUserCredentials getUser();

    /**
     * Returns the {@link AuthUserCertificates} of the currently logged-in user of this {@link RestSession}.
     *
     * @return The {@link AuthUserCertificates} of the currently logged-in user of this {@link RestSession}.
     */
    @Nullable AuthUserCertificates getCertificates();

    /**
     * Updates the {@link KeyStorePassword}s for specific keystore and returns the
     * {@link AuthUserCertificates} for the currently logged-in user afterward.
     *
     * @param keystoreName     The name of the keystore to be updated.
     * @param keyStorePassword The {@link KeyStorePassword} to unlock the certificates with.
     * @return The {@link AuthUserCertificates} of the logged-in user in this {@link RestSession}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Nullable AuthUserCertificates updateCertificates(
            String keystoreName, KeyStorePassword keyStorePassword
    ) throws ResultException;

    /**
     * Creates a matching {@link RestWebService} instance to execute a webPDF operation for the current session.
     *
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link RestWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link RestWebService} creation failed.
     */
    @NotNull <T_WEBSERVICE extends RestWebService<?, ?, ?>> T_WEBSERVICE createWebServiceInstance(
            @NotNull WebServiceType webServiceType) throws ResultException;

}