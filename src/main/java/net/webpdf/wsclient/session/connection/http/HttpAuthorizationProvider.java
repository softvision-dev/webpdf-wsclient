package net.webpdf.wsclient.session.connection.http;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.session.rest.RestSession;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * An instance of {@link HttpAuthorizationProvider} may be used for a given {@link RestSession} to:
 * <ul>
 * <li>Directly provide a {@link HttpHeaders#AUTHORIZATION} {@link Header} for a {@link HttpUriRequest}.</li>
 * <li>Indirectly as a {@link ExecChainHandler} to provide authorization for a {@link ClassicHttpRequest} in
 * the context of a {@link ExecChain}.</li>
 * </ul>
 * </p>
 */
public class HttpAuthorizationProvider implements HttpExecChainHandler {

    private final @NotNull RestSession<?> session;

    /**
     * Instantiates a new {@link HttpAuthorizationProvider} for a given {@link RestSession}.
     *
     * @param session The {@link RestSession} to create a {@link HttpAuthorizationProvider} for.
     */
    public HttpAuthorizationProvider(@NotNull RestSession<?> session) {
        this.session = session;
    }

    /**
     * <p>
     * When this method is called the {@link HttpAuthorizationProvider} acts as a {@link ExecChainHandler} in the
     * context of a {@link ExecChain}. It will check whether the given request contains an
     * {@link HttpHeaders#AUTHORIZATION} {@link Header} and in case it is missing it shall provide such a {@link Header}
     * according to the provided and active {@link RestSession}.
     * </p>
     *
     * @param classicHttpRequest the actual request.
     * @param scope              the execution scope.
     * @param execChain          the next element in the request execution chain.
     */
    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest classicHttpRequest, ExecChain.Scope scope,
            ExecChain execChain) throws IOException, HttpException {
        Header authorizationHeader;
        if (!classicHttpRequest.containsHeader(HttpHeaders.AUTHORIZATION) &&
                (authorizationHeader = provideAuthorizationHeader()) != null) {
            classicHttpRequest.addHeader(authorizationHeader);
        }
        return execChain.proceed(classicHttpRequest, scope);
    }

    /**
     * Provides a {@link HttpHeaders#AUTHORIZATION} {@link Header} for {@link HttpUriRequest}s within the  provided
     * {@link RestSession}. (May return {@code null}, should such a {@link Header} not be required.)
     *
     * @return The matching {@link HttpHeaders#AUTHORIZATION} {@link Header}. (May return {@code null}, should such a
     * {@link Header} not be required.)
     */
    public @Nullable Header provideAuthorizationHeader() {
        if (this.session.getToken() != null && !this.session.getToken().getToken().isEmpty()) {
            return new BasicHeader(HttpHeaders.AUTHORIZATION,
                    "Bearer " + this.session.getToken().getToken());
        } else if (this.session.getCredentials() != null) {
            return new BasicHeader(HttpHeaders.AUTHORIZATION,
                    "Basic " + DatatypeConverter.printBase64Binary(
                            (this.session.getCredentials().getUserPrincipal().getName()
                                    + ":" + new String(this.session.getCredentials().getPassword()))
                                    .getBytes()));
        }

        return null;
    }

    /**
     * Provides a name for the {@link HttpAuthorizationProvider} in the context of a {@link ExecChain}.
     *
     * @return A name for the {@link HttpAuthorizationProvider} in the context of a {@link ExecChain}.
     */
    @Override
    public @NotNull String getExecChainHandlerName() {
        @NotNull String REDIRECT_INTERCEPTOR = "REDIRECT_INTERCEPTOR";
        return REDIRECT_INTERCEPTOR;
    }

}
