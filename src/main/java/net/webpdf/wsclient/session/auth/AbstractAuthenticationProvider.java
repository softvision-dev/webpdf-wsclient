package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.AuthLoginOptions;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.auth.material.token.SessionToken;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * A class extending {@link AbstractAuthenticationProvider} shall provide the means to use the webPDF server´s login
 * endpoint to authenticate a user. It shall also organize the automatic refresh of the {@link SessionToken} provided
 * by the login endpoint and shall update it´s used {@link AuthMaterial} accordingly.
 * </p>
 * <p>
 * <b>Be aware:</b> Currently an {@link AbstractAuthenticationProvider} shall only serve one {@link Session} at a time.
 * An {@link AbstractAuthenticationProvider} being called by another {@link Session} than it´s current master, shall
 * assume it´s current master to have expired and shall, try to reauthorize that new {@link Session} (new master).<br>
 * For that reason an {@link AbstractAuthenticationProvider}s shall be reusable by subsequent {@link Session}s.
 * </p>
 * <p>
 * <b>Be aware:</b> However - An implementation of {@link AuthProvider} is not required to serve multiple
 * {@link Session}s at a time. It is expected to create a new {@link AuthProvider} for each existing
 * {@link Session}.
 * </p>
 */
public abstract class AbstractAuthenticationProvider implements AuthProvider {

    private static final @NotNull String LOGIN_PATH = "authentication/user/login/";
    private static final @NotNull String REFRESH_PATH = "authentication/user/refresh/";
    private final @NotNull AuthMaterial initialAuthMaterial;
    private final @NotNull AtomicReference<AuthMaterial> authMaterial = new AtomicReference<>();
    private final @NotNull AtomicBoolean updating = new AtomicBoolean(false);
    private final @NotNull AtomicReference<Session> session = new AtomicReference<>();

    /**
     * <p>
     * Creates a fresh authentication provider, that shall initialize a {@link Session} using the given
     * {@link AuthMaterial}.<br>
     * <b>Be aware:</b> possibly the given {@link AuthMaterial} will only be used during login and will be replaced
     * with a {@link SessionToken}.
     * </p>
     * <p>
     * <b>Be aware:</b> Currently an {@link AbstractAuthenticationProvider} shall only serve one {@link Session} at a
     * time. An {@link AbstractAuthenticationProvider} being called by another {@link Session} than it´s current master,
     * shall assume it´s current master to have expired and shall, try to reauthorize that new {@link Session}
     * (new master).<br>
     * For that reason an {@link AbstractAuthenticationProvider}s shall be reusable by subsequent {@link Session}s.
     * </p>
     *
     * @param authMaterial The {@link AuthMaterial} to initialize the {@link Session} with.
     */
    public AbstractAuthenticationProvider(@NotNull AuthMaterial authMaterial) {
        this.initialAuthMaterial = authMaterial;
        setAuthMaterial(authMaterial);
    }

    /**
     * <p>
     * Resumes an existing authentication provider, that shall resume a {@link Session} if a {@link SessionToken} is
     * provided.
     * </p>
     * <p>
     * <b>Be aware:</b> Currently an {@link AbstractAuthenticationProvider} shall only serve one {@link Session} at a
     * time. An {@link AbstractAuthenticationProvider} being called by another {@link Session} than it´s current master,
     * shall assume it´s current master to have expired and shall, try to reauthorize that new {@link Session}
     * (new master).<br>
     * For that reason an {@link AbstractAuthenticationProvider}s shall be reusable by subsequent {@link Session}s.
     * </p>
     *
     * @param authMaterial The {@link AuthMaterial} to initialize the {@link Session} with.
     * @param resumeAuthMaterial The {@link AuthMaterial} to resume the {@link Session} with.
     */
    public AbstractAuthenticationProvider(@NotNull AuthMaterial authMaterial, AuthMaterial resumeAuthMaterial) {
        this.initialAuthMaterial = authMaterial;
        setAuthMaterial(resumeAuthMaterial);
    }

    /**
     * Returns the current {@link Session} this {@link AuthProvider} provides authorization for.
     *
     * @return The current {@link Session} this {@link AuthProvider} provides authorization for.
     */
    public @Nullable Session getSession() {
        return this.session.get();
    }

    /**
     * Returns the initial {@link AuthMaterial} given to this {@link AuthProvider}.
     *
     * @return The initial {@link AuthMaterial} given to this {@link AuthProvider}.
     */
    public @NotNull AuthMaterial getInitialAuthMaterial() {
        return this.initialAuthMaterial;
    }

    /**
     * Returns the currently used {@link AuthMaterial}.
     *
     * @return The currently used {@link AuthMaterial}.
     */
    protected @NotNull AuthMaterial getAuthMaterial() {
        return this.authMaterial.get();
    }

    /**
     * Sets the used {@link AuthMaterial}.
     *
     * @param authMaterial The {@link AuthMaterial} to set.
     */
    protected void setAuthMaterial(@NotNull AuthMaterial authMaterial) {
        this.authMaterial.set(authMaterial);
    }

    /**
     * Refresh authorization {@link SessionToken} for an active {@link Session}.
     *
     * @param session The session to refresh the authorization for.
     * @return The {@link AuthMaterial} refreshed by this {@link AuthProvider}.
     * @throws AuthResultException Shall be thrown, should the authentication/authorization fail for some reason.
     */
    @Override
    public @NotNull AuthMaterial refresh(Session session) throws AuthResultException {
        if (updating.get() || !(session instanceof RestSession)) {
            return this.getAuthMaterial();
        }

        if (!(getAuthMaterial() instanceof SessionToken)) {
            return this.provide(session);
        }

        try {
            updating.set(true);
            this.session.set(session);
            RestSession<?> restSession = (RestSession<?>) session;

            ((SessionToken) this.getAuthMaterial()).refresh();

            AuthLoginOptions loginOptions = new AuthLoginOptions();
            loginOptions.setCreateRefreshToken(true);

            AuthMaterial authMaterial = HttpRestRequest.createRequest(restSession)
                    .buildRequest(HttpMethod.POST, REFRESH_PATH, prepareHttpEntity(loginOptions))
                    .executeRequest(SessionToken.class);

            if (authMaterial == null) {
                throw new ClientResultException(Error.AUTHENTICATION_FAILURE);
            }

            setAuthMaterial(authMaterial);
            this.updating.set(false);
        } catch (ResultException ex) {
            throw new AuthResultException(ex);
        }

        return this.getAuthMaterial();
    }

    /**
     * Login and provide authorization {@link SessionToken} for a {@link Session}.
     *
     * @param session The session to provide the authorization for.
     * @return The {@link AuthMaterial} provided by this {@link AuthProvider}.
     * @throws AuthResultException Shall be thrown, should the authentication/authorization fail for some reason.
     */
    protected @NotNull AuthMaterial login(@NotNull Session session) throws AuthResultException {
        if (updating.get() || !(session instanceof RestSession) || getAuthMaterial() instanceof SessionToken) {
            return this.getAuthMaterial();
        }

        try {
            updating.set(true);
            this.session.set(session);
            RestSession<?> restSession = (RestSession<?>) session;

            AuthLoginOptions loginOptions = new AuthLoginOptions();
            loginOptions.setCreateRefreshToken(true);

            AuthMaterial authMaterial = HttpRestRequest.createRequest(restSession)
                    .buildRequest(HttpMethod.POST, LOGIN_PATH, prepareHttpEntity(loginOptions), getInitialAuthMaterial())
                    .executeRequest(SessionToken.class);

            if (authMaterial == null) {
                throw new ClientResultException(Error.AUTHENTICATION_FAILURE);
            }

            setAuthMaterial(authMaterial);
            this.updating.set(false);
        } catch (ResultException ex) {
            throw new AuthResultException(ex);
        }

        return this.getAuthMaterial();
    }


    /**
     * <p>
     * Provides {@link AuthMaterial} for the authorization of a {@link Session}.<br>
     * Will attempt to produce a {@link SessionToken} for {@link RestSession}s.<br>
     * Will also refresh expired {@link SessionToken}s.
     * </p>
     * <p>
     * <b>Be aware:</b> Currently an {@link AbstractAuthenticationProvider} shall only serve one {@link Session} at a
     * time. An {@link AbstractAuthenticationProvider} being called by another {@link Session} than it´s current master,
     * shall assume it´s current master to have expired and shall, try to reauthorize that new {@link Session}
     * (new master).<br>
     * For that reason an {@link AbstractAuthenticationProvider}s shall be reusable by subsequent {@link Session}s.
     * </p>
     *
     * @param session The session to provide authorization for.
     * @return The {@link AuthMaterial} provided by this {@link AuthProvider}.
     * @throws AuthResultException Shall be thrown, should the authentication/authorization fail for some reason.
     */
    @Override
    public @NotNull AuthMaterial provide(@NotNull Session session) throws AuthResultException {
        if (updating.get() || !(session instanceof RestSession)) {
            return getAuthMaterial();
        }

        if (!(getAuthMaterial() instanceof SessionToken)) {
            this.login(session);
        }

        if (getAuthMaterial() instanceof SessionToken && (
                ((SessionToken) getAuthMaterial()).isExpired(session.getSessionContext().getSkewTime())
        )) {
            this.refresh(session);
        }

        return this.getAuthMaterial();
    }

    /**
     * Prepares a {@link HttpEntity} for internal requests to the webPDF server.
     *
     * @param parameter The parameters, that shall be used for the request.
     * @param <T>       The parameter type (data transfer object/bean) that shall be used.
     * @return The resulting state of the data transfer object.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private <T> @NotNull HttpEntity prepareHttpEntity(@NotNull T parameter) throws ResultException {
        try {
            return new StringEntity(SerializeHelper.toJSON(parameter),
                    ContentType.create(DataFormat.JSON.getMimeType(), StandardCharsets.UTF_8));
        } catch (UnsupportedCharsetException ex) {
            throw new ClientResultException(Error.XML_OR_JSON_CONVERSION_FAILURE, ex);
        }
    }
}
