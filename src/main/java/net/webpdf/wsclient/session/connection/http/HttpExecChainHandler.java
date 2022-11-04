package net.webpdf.wsclient.session.connection.http;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Request execution handler in a classic client side request {@link ExecChain}.
 * Handlers can either be a decorator around another element that implements a cross-cutting aspect or a self-contained
 * executor capable of producing a response for the given request.
 *
 * @see org.apache.hc.client5.http.classic.ExecChainHandler
 */
public interface HttpExecChainHandler extends ExecChainHandler {

    /**
     * Provides a name for the {@link HttpExecChainHandler} in the context of a {@link ExecChain}.
     *
     * @return A name for the {@link HttpExecChainHandler} in the context of a {@link ExecChain}.
     */
    @NotNull String getExecChainHandlerName();

}
