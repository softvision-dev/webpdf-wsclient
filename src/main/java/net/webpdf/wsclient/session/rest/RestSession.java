package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.RestWebServiceDocumentManager;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.schema.beans.Token;
import net.webpdf.wsclient.schema.beans.UserCredentialsBean;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.Session;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface RestSession<T_REST_DOCUMENT extends RestDocument> extends Session<T_REST_DOCUMENT> {
    /**
     * Returns the session {@link Token} of this session.
     *
     * @return The session {@link Token} of this session.
     */
    @Nullable
    Token getToken();

    /**
     * Returns the {@link CloseableHttpClient} connected to the server via this session.
     *
     * @return The {@link CloseableHttpClient} connected to the server via this session.
     */
    @NotNull
    CloseableHttpClient getHttpClient();

    /**
     * Returns the {@link RestWebServiceDocumentManager} managing source and target document for this session.
     *
     * @return The {@link RestWebServiceDocumentManager} managing source and target document for this session.
     */
    @NotNull
    DocumentManager<T_REST_DOCUMENT> getDocumentManager();

    /**
     * Sets the {@link DataFormat} accepted by this session.
     *
     * @param dataFormat The {@link DataFormat} accepted by this session.
     */
    @SuppressWarnings("unused")
    void setDataFormat(@Nullable DataFormat dataFormat);

    /**
     * Login into the server with an existing session token
     *
     * @param token the token to refresh the session with
     * @throws IOException HTTP access error
     */
    void login(@Nullable Token token) throws IOException;

    /**
     * Login into the server and get a token
     *
     * @throws IOException HTTP access error
     */
    void login() throws IOException;

    /**
     * Returns the {@link UserCredentialsBean} for the currently logged in user
     *
     * @return The {@link UserCredentialsBean} for the currently logged in user
     */
    @Nullable
    UserCredentialsBean getUserCredentials();
}
