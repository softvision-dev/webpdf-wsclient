package net.webpdf.wsclient.session.connection.http;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.*;
import org.jetbrains.annotations.NotNull;

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
     *
     * @param classicHttpRequest the actual request.
     * @param scope              the execution scope.
     * @param execChain          the next element in the request execution chain.
     */
    @Override
    public ClassicHttpResponse execute(@NotNull ClassicHttpRequest classicHttpRequest, @NotNull ExecChain.Scope scope,
            @NotNull ExecChain execChain) throws IOException, HttpException {
        Header authorizationHeader;
        try {
            if (!classicHttpRequest.containsHeader(HttpHeaders.AUTHORIZATION) &&
                    (authorizationHeader = this.session.getAuthMaterial().getAuthHeader()) != null) {
                classicHttpRequest.addHeader(authorizationHeader);
            }
        } catch (ResultException ex) {
            throw new IOException(ex);
        }
        return execChain.proceed(classicHttpRequest, scope);
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
