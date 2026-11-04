package net.webpdf.wsclient.session.connection.http;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * An instance of {@link HttpAuthorizationHandler} may be used for a {@link ClassicHttpRequest} as a
 * {@link ExecChainHandler} to provide authorization in the context of the {@link ExecChain}.
 * </p>
 */
public class HttpAuthorizationHandler implements HttpExecChainHandler {

    private final @NotNull Session session;

    /**
     * Instantiates a new {@link HttpAuthorizationHandler} for a given {@link Session}.
     *
     * @param session The {@link Session} to provide authorization for.
     */
    public HttpAuthorizationHandler(@NotNull Session session) {
        this.session = session;
    }

    /**
     * <p>
     * When this method is called the {@link HttpAuthorizationHandler} acts as a {@link ExecChainHandler} in the
     * context of a {@link ExecChain}. It will check whether the given request contains an
     * {@link HttpHeaders#AUTHORIZATION} {@link Header} and in case it is missing it shall provide such a {@link Header}
     * according to the provided and active {@link Session}.
     * </p>
     * <p>
     * This handler also runs for every redirect hop. The webPDF server answers operations and uploads with a
     * {@code 303}, for which the HTTP client issues a new request without the original headers, and builds its
     * target from the {@code X-Forwarded-*} headers of a proxy or from its configured server URL. That target may be
     * another host or port than the one the {@link Session} connects to, so the {@link Header} is provided for any
     * host. A redirect that copies the original request including its {@link Header} (for example a {@code 307})
     * is not followed to another scheme, host or port by HttpClient 5.5 and later.
     * </p>
     * <p>
     * One exception applies to every request: a {@link Session} using {@code https} never sends the
     * {@link Header} to a target that does not use {@code https}, as the credentials would be transmitted
     * unencrypted. Such a request loses an {@link HttpHeaders#AUTHORIZATION} {@link Header} it already carries, so
     * the rule also holds with HTTP client versions that copy the {@link Header} into such a redirect.
     * </p>
     * <p>
     * The retry step of the HTTP client, which follows this handler, rebuilds a repeated request (for example after
     * a {@code 503}) from the original request of the execution scope. That original request therefore receives
     * the same decision: it loses the {@link Header} on such a target and gains the {@link Header} this handler
     * provides on any other.
     * </p>
     *
     * @param classicHttpRequest the actual request.
     * @param scope              the execution scope.
     * @param execChain          the next element in the request execution chain.
     */
    @Override
    public ClassicHttpResponse execute(@NotNull ClassicHttpRequest classicHttpRequest, @NotNull ExecChain.Scope scope,
            @NotNull ExecChain execChain) throws IOException, HttpException {
        ExecChain.Scope nextScope = scope;
        try {
            if (isEncryptionDowngrade(scope.route.getTargetHost())) {
                classicHttpRequest.removeHeaders(HttpHeaders.AUTHORIZATION);
                if (scope.originalRequest.containsHeader(HttpHeaders.AUTHORIZATION)) {
                    ClassicHttpRequest originalRequest = ClassicRequestBuilder.copy(scope.originalRequest).build();
                    originalRequest.removeHeaders(HttpHeaders.AUTHORIZATION);
                    nextScope = withOriginalRequest(scope, originalRequest);
                }
            } else {
                Header authorizationHeader;
                if (!classicHttpRequest.containsHeader(HttpHeaders.AUTHORIZATION) &&
                        (authorizationHeader = session.getAuthProvider().provide(session).getAuthHeader()) != null) {
                    classicHttpRequest.addHeader(authorizationHeader);
                    if (!scope.originalRequest.containsHeader(HttpHeaders.AUTHORIZATION)) {
                        ClassicHttpRequest originalRequest = ClassicRequestBuilder.copy(scope.originalRequest).build();
                        originalRequest.addHeader(authorizationHeader);
                        nextScope = withOriginalRequest(scope, originalRequest);
                    }
                }
            }
        } catch (ResultException ex) {
            throw new IOException(ex);
        }
        return execChain.proceed(classicHttpRequest, nextScope);
    }

    /**
     * Returns a copy of the given execution scope with another original request.
     *
     * @param scope           The execution scope to copy.
     * @param originalRequest The original request of the new execution scope.
     * @return The copied execution scope.
     */
    private static @NotNull ExecChain.Scope withOriginalRequest(@NotNull ExecChain.Scope scope,
            @NotNull ClassicHttpRequest originalRequest) {
        return new ExecChain.Scope(scope.exchangeId, scope.route, originalRequest, scope.execRuntime,
                scope.clientContext);
    }

    /**
     * Returns {@code true}, if the {@link Session} uses {@code https} and the given request target does not, or if
     * the target is unknown. The {@link Session}´s authorization must not be sent to such a target.
     *
     * @param target The target host of the request.
     * @return {@code true}, if sending the {@link Session}´s authorization to the target would lose the encryption
     * the {@link Session} has been established with.
     */
    private boolean isEncryptionDowngrade(@Nullable HttpHost target) {
        if (!"https".equalsIgnoreCase(this.session.getSessionContext().getUrl().getProtocol())) {
            return false;
        }
        return target == null || !"https".equalsIgnoreCase(target.getSchemeName());
    }

    /**
     * Provides a name for the {@link HttpAuthorizationHandler} in the context of a {@link ExecChain}.
     *
     * @return A name for the {@link HttpAuthorizationHandler} in the context of a {@link ExecChain}.
     */
    @Override
    public @NotNull String getExecChainHandlerName() {
        return "REDIRECT_INTERCEPTOR";
    }

}
