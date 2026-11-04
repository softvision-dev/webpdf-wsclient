package net.webpdf.wsclient.stub;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * A minimal in-process HTTP server on the loopback interface, that stands in for a webPDF server in unit tests.
 * </p>
 * <p>
 * It answers the requests a REST session needs to be established and closed (login, refresh, user info,
 * certificates, logout) and any further request a test registers via {@link #respond(String, String, Responder)}.
 * Every received request is recorded, so tests can check what the wsclient actually sent (e.g. the
 * {@code Authorization} header). Unknown paths are answered with {@code 404}.
 * </p>
 * <p>
 * It exists to test client side behaviour (headers, redirects, error mapping, lifecycle) without a running webPDF
 * server - it does not emulate any webservice.
 * </p>
 */
public final class LocalWebPdfServer implements AutoCloseable {

    /**
     * The context path of the emulated server, as used in {@link #getUrl()}.
     */
    public static final @NotNull String CONTEXT = "/webPDF/";
    /**
     * The path prefix of the REST API.
     */
    public static final @NotNull String REST = CONTEXT + "rest/";
    /**
     * The login path of the REST API.
     */
    public static final @NotNull String LOGIN_PATH = REST + "authentication/user/login/";
    /**
     * The token refresh path of the REST API.
     */
    public static final @NotNull String REFRESH_PATH = REST + "authentication/user/refresh/";
    /**
     * The user info path of the REST API.
     */
    public static final @NotNull String INFO_PATH = REST + "authentication/user/info/";
    /**
     * The user certificates path of the REST API.
     */
    public static final @NotNull String CERTIFICATES_PATH = REST + "authentication/user/certificates/";
    /**
     * The logout path of the REST API.
     */
    public static final @NotNull String LOGOUT_PATH = REST + "authentication/user/logout/";

    private final @NotNull HttpServer server;
    private final @NotNull ExecutorService executor;
    private final @NotNull Map<String, Responder> responders = new ConcurrentHashMap<>();
    private final @NotNull List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
    private final @NotNull AtomicInteger loginCount = new AtomicInteger();
    private final @NotNull AtomicInteger refreshCount = new AtomicInteger();

    private LocalWebPdfServer(@NotNull HttpServer server, @NotNull ExecutorService executor) {
        this.server = server;
        this.executor = executor;
        // Each login issues a new token pair, so tests can tell sessions and refreshes apart.
        respond("POST", LOGIN_PATH, exchange -> {
            int login = loginCount.incrementAndGet();
            sendJson(exchange, 200, "{\"token\":\"access-" + login + "\",\"refreshToken\":\"refresh-" + login
                    + "\",\"expiresIn\":3600}");
        });
        respond("POST", REFRESH_PATH, exchange -> {
            int refresh = refreshCount.incrementAndGet();
            sendJson(exchange, 200, "{\"token\":\"refreshed-access-" + refresh
                    + "\",\"refreshToken\":\"refreshed-refresh-" + refresh + "\",\"expiresIn\":3600}");
        });
        respond("GET", INFO_PATH, exchange -> sendJson(exchange, 200,
                "{\"isAdmin\":true,\"isUser\":true,\"userName\":\"admin\",\"uniqueId\":\"1\"}"));
        respond("GET", CERTIFICATES_PATH, exchange -> sendJson(exchange, 200, "{}"));
        respond("GET", LOGOUT_PATH, exchange -> sendJson(exchange, 200, "{}"));
    }

    /**
     * Starts a {@link LocalWebPdfServer} on a free port of the loopback interface.
     *
     * @return The started {@link LocalWebPdfServer}.
     * @throws IOException Shall be thrown, should the server not be startable.
     */
    public static @NotNull LocalWebPdfServer start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        ExecutorService executor = Executors.newFixedThreadPool(4, runnable -> {
            Thread thread = new Thread(runnable, "local-webpdf-server");
            thread.setDaemon(true);
            return thread;
        });
        server.setExecutor(executor);
        LocalWebPdfServer localServer = new LocalWebPdfServer(server, executor);
        server.createContext("/", localServer::handle);
        server.start();
        return localServer;
    }

    /**
     * Returns the host address of this server, as used in {@link #getUrl()}.
     *
     * @return The host address of this server.
     */
    public @NotNull String getHost() {
        return this.server.getAddress().getAddress().getHostAddress();
    }

    /**
     * Returns the port of this server.
     *
     * @return The port of this server.
     */
    public int getPort() {
        return this.server.getAddress().getPort();
    }

    /**
     * Returns the URL of the emulated webPDF server, that shall be used for a session.
     *
     * @return The URL of the emulated webPDF server.
     */
    public @NotNull URL getUrl() {
        return createUrl("http://" + getHost() + ":" + getPort() + CONTEXT);
    }

    /**
     * Creates a {@link URL} from the given String, which must be valid.
     *
     * @param url The URL String.
     * @return The {@link URL}.
     */
    public static @NotNull URL createUrl(@NotNull String url) {
        try {
            return new URL(url);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Registers the {@link Responder} for the given method and path, replacing any previous one.
     *
     * @param method    The HTTP method.
     * @param path      The exact request path (without query).
     * @param responder The {@link Responder} answering the request.
     */
    public void respond(@NotNull String method, @NotNull String path, @NotNull Responder responder) {
        this.responders.put(method + " " + path, responder);
    }

    /**
     * Registers a fixed response for the given method and path, replacing any previous one.
     *
     * @param method      The HTTP method.
     * @param path        The exact request path (without query).
     * @param status      The HTTP status code of the response.
     * @param contentType The Content-Type header of the response.
     * @param body        The body of the response.
     */
    public void respond(@NotNull String method, @NotNull String path, int status, @NotNull String contentType,
            @NotNull String body) {
        respond(method, path, exchange -> send(exchange, status, contentType, body));
    }

    /**
     * Returns all requests received so far, in the order of their arrival.
     *
     * @return All requests received so far.
     */
    public @NotNull List<RecordedRequest> getRequests() {
        return new ArrayList<>(this.requests);
    }

    /**
     * Returns the requests received so far for the given method and path.
     *
     * @param method The HTTP method.
     * @param path   The exact request path (without query).
     * @return The requests received so far for the given method and path.
     */
    public @NotNull List<RecordedRequest> getRequests(@NotNull String method, @NotNull String path) {
        List<RecordedRequest> result = new ArrayList<>();
        for (RecordedRequest request : this.requests) {
            if (request.getMethod().equals(method) && request.getPath().equals(path)) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Returns the number of logins answered so far.
     *
     * @return The number of logins answered so far.
     */
    public int getLoginCount() {
        return this.loginCount.get();
    }

    /**
     * Stops the server.
     */
    @Override
    public void close() {
        this.server.stop(0);
        this.executor.shutdownNow();
    }

    private void handle(@NotNull HttpExchange exchange) throws IOException {
        try {
            byte[] body;
            try (InputStream inputStream = exchange.getRequestBody()) {
                body = inputStream.readAllBytes();
            }
            String path = exchange.getRequestURI().getRawPath();
            this.requests.add(new RecordedRequest(exchange.getRequestMethod(), path,
                    exchange.getRequestURI().getRawQuery(),
                    exchange.getRequestHeaders().getFirst("Authorization"),
                    new String(body, StandardCharsets.UTF_8)));
            Responder responder = this.responders.get(exchange.getRequestMethod() + " " + path);
            if (responder == null) {
                send(exchange, 404, "text/plain", "Not found: " + path);
            } else {
                responder.respond(exchange);
            }
        } finally {
            exchange.close();
        }
    }

    /**
     * Sends a JSON response.
     *
     * @param exchange The {@link HttpExchange} to answer.
     * @param status   The HTTP status code.
     * @param json     The JSON body.
     * @throws IOException Shall be thrown, should writing the response fail.
     */
    public static void sendJson(@NotNull HttpExchange exchange, int status, @NotNull String json) throws IOException {
        send(exchange, status, "application/json", json);
    }

    /**
     * Sends a response with the given status, Content-Type and UTF-8 encoded body.
     *
     * @param exchange    The {@link HttpExchange} to answer.
     * @param status      The HTTP status code.
     * @param contentType The Content-Type header.
     * @param body        The body.
     * @throws IOException Shall be thrown, should writing the response fail.
     */
    public static void send(@NotNull HttpExchange exchange, int status, @NotNull String contentType,
            @NotNull String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        }
    }

    /**
     * Answers a request received by the {@link LocalWebPdfServer}.
     */
    @FunctionalInterface
    public interface Responder {

        /**
         * Answers the given request.
         *
         * @param exchange The {@link HttpExchange} to answer.
         * @throws IOException Shall be thrown, should writing the response fail.
         */
        void respond(@NotNull HttpExchange exchange) throws IOException;
    }

    /**
     * A request received by the {@link LocalWebPdfServer}.
     */
    public static final class RecordedRequest {

        private final @NotNull String method;
        private final @NotNull String path;
        private final @Nullable String query;
        private final @Nullable String authorization;
        private final @NotNull String body;

        private RecordedRequest(@NotNull String method, @NotNull String path, @Nullable String query,
                @Nullable String authorization, @NotNull String body) {
            this.method = method;
            this.path = path;
            this.query = query;
            this.authorization = authorization;
            this.body = body;
        }

        /**
         * Returns the HTTP method.
         *
         * @return The HTTP method.
         */
        public @NotNull String getMethod() {
            return this.method;
        }

        /**
         * Returns the raw request path.
         *
         * @return The raw request path.
         */
        public @NotNull String getPath() {
            return this.path;
        }

        /**
         * Returns the raw query, if any.
         *
         * @return The raw query, if any.
         */
        public @Nullable String getQuery() {
            return this.query;
        }

        /**
         * Returns the value of the {@code Authorization} header, if any.
         *
         * @return The value of the {@code Authorization} header, if any.
         */
        public @Nullable String getAuthorization() {
            return this.authorization;
        }

        /**
         * Returns the request body, decoded as UTF-8.
         *
         * @return The request body.
         */
        public @NotNull String getBody() {
            return this.body;
        }

        @Override
        public String toString() {
            return this.method + " " + this.path + (this.query != null ? "?" + this.query : "");
        }
    }

}
