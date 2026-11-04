package net.webpdf.wsclient.http;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.http.HttpAuthorizationHandler;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins that the {@link HttpAuthorizationHandler} adds the session´s {@code Authorization} header to every request,
 * including redirect targets on another host or port (the webPDF server builds its redirect targets from proxy
 * headers or its configured server URL), but never to a target without {@code https} when the session uses
 * {@code https}.
 */
public class HttpAuthorizationHandlerTest {

    @ParameterizedTest(name = "{0} -> {1}://{2}:{3} adds header: {4}")
    @CsvSource({
            "http://webpdf.example:8080/webPDF/, http, webpdf.example, 8080, true",
            "http://WebPDF.Example:8080/webPDF/, http, webpdf.example, 8080, true",
            "http://webpdf.example/webPDF/, http, webpdf.example, 80, true",
            "https://webpdf.example/webPDF/, https, webpdf.example, 443, true",
            "http://webpdf.example:8080/webPDF/, https, webpdf.example, 8443, true",
            "http://webpdf.example:8080/webPDF/, http, webpdf.example, 9090, true",
            "http://webpdf.example:8080/webPDF/, http, proxy.example, 8080, true",
            "http://webpdf.example:8080/webPDF/, https, proxy.example, 443, true",
            "https://webpdf.example:8443/webPDF/, https, proxy.example, 443, true",
            "https://webpdf.example:8443/webPDF/, http, webpdf.example, 8443, false",
            "https://webpdf.example:8443/webPDF/, http, proxy.example, 80, false"
    })
    public void testAuthorizationNotSentUnencryptedFromHttpsSession(String sessionUrl, String scheme, String host,
            int port, boolean expectHeader) throws Exception {
        try (Session session = SessionFactory.createInstance(
                new SessionContext(WebServiceProtocol.SOAP, LocalWebPdfServer.createUrl(sessionUrl)),
                new UserAuthProvider("usr", "pwd"))) {
            HttpHost target = new HttpHost(scheme, host, port);
            ClassicHttpRequest request = new HttpGet(URI.create(target.toURI() + "/webPDF/rest/documents/"));
            AtomicReference<ClassicHttpRequest> proceeded = new AtomicReference<>();
            ExecChain chain = (chainRequest, scope) -> {
                proceeded.set(chainRequest);
                return new BasicClassicHttpResponse(200);
            };

            new HttpAuthorizationHandler(session).execute(request, createScope(target, request), chain);

            assertSame(request, proceeded.get(), "The request should have been passed on.");
            assertEquals(expectHeader, request.containsHeader(HttpHeaders.AUTHORIZATION),
                    "Unexpected presence of the authorization header for target " + target);
        }
    }

    /**
     * HTTP client versions before 5.5 copy the original request including its {@code Authorization} header into a
     * {@code 307}/{@code 308} redirect to another scheme, and the retry step rebuilds a repeated request from the
     * scope´s original request. The handler must remove the header from both. The chain below rebuilds the request
     * from the scope´s original request the way the retry step does.
     */
    @ParameterizedTest(name = "{0} -> {1}://{2}:{3} keeps existing header: {4}")
    @CsvSource({
            "https://webpdf.example:8443/webPDF/, http, webpdf.example, 8443, false",
            "https://webpdf.example:8443/webPDF/, http, proxy.example, 80, false",
            "https://webpdf.example:8443/webPDF/, https, proxy.example, 443, true",
            "http://webpdf.example:8080/webPDF/, http, proxy.example, 8080, true"
    })
    public void testExistingAuthorizationNotSentUnencryptedFromHttpsSession(String sessionUrl, String scheme,
            String host, int port, boolean expectHeader) throws Exception {
        try (Session session = SessionFactory.createInstance(
                new SessionContext(WebServiceProtocol.SOAP, LocalWebPdfServer.createUrl(sessionUrl)),
                new UserAuthProvider("usr", "pwd"))) {
            HttpHost target = new HttpHost(scheme, host, port);
            ClassicHttpRequest originalRequest =
                    new HttpGet(URI.create(target.toURI() + "/webPDF/rest/documents/"));
            originalRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer copied-token");
            ClassicHttpRequest request = ClassicRequestBuilder.copy(originalRequest).build();
            AtomicReference<ClassicHttpRequest> retried = new AtomicReference<>();
            ExecChain chain = (chainRequest, scope) -> {
                retried.set(ClassicRequestBuilder.copy(scope.originalRequest).build());
                return new BasicClassicHttpResponse(200);
            };

            new HttpAuthorizationHandler(session).execute(request, createScope(target, originalRequest), chain);

            for (ClassicHttpRequest sent : List.of(request, retried.get())) {
                if (expectHeader) {
                    Header[] headers = sent.getHeaders(HttpHeaders.AUTHORIZATION);
                    assertEquals(1, headers.length, "Exactly one authorization header should be sent to " + target);
                    assertEquals("Bearer copied-token", headers[0].getValue(),
                            "An existing authorization header should be passed on unchanged to " + target);
                } else {
                    assertFalse(sent.containsHeader(HttpHeaders.AUTHORIZATION),
                            "An existing authorization header must not be sent unencrypted to " + target);
                }
            }
        }
    }

    @Test
    public void testProvidedAuthorizationSurvivesRetry() throws Exception {
        try (Session session = SessionFactory.createInstance(
                new SessionContext(WebServiceProtocol.SOAP, LocalWebPdfServer.createUrl(
                        "http://webpdf.example:8080/webPDF/")),
                new UserAuthProvider("usr", "pwd"))) {
            HttpHost target = new HttpHost("http", "proxy.example", 8080);
            // A redirect the HTTP client issues as a new request carries no authorization of its own.
            ClassicHttpRequest originalRequest =
                    new HttpGet(URI.create(target.toURI() + "/webPDF/rest/documents/"));
            ClassicHttpRequest request = ClassicRequestBuilder.copy(originalRequest).build();
            AtomicReference<ClassicHttpRequest> retried = new AtomicReference<>();
            ExecChain chain = (chainRequest, scope) -> {
                retried.set(ClassicRequestBuilder.copy(scope.originalRequest).build());
                return new BasicClassicHttpResponse(200);
            };

            new HttpAuthorizationHandler(session).execute(request, createScope(target, originalRequest), chain);

            Header provided = request.getFirstHeader(HttpHeaders.AUTHORIZATION);
            assertNotNull(provided, "The handler should have provided the authorization header.");
            Header[] retriedHeaders = retried.get().getHeaders(HttpHeaders.AUTHORIZATION);
            assertEquals(1, retriedHeaders.length, "A repeated request should carry exactly one authorization header.");
            assertEquals(provided.getValue(), retriedHeaders[0].getValue(),
                    "A repeated request should carry the provided authorization header.");
        }
    }

    @Test
    public void testRetriedRedirectToOtherServerKeepsAuthorization() throws Exception {
        try (LocalWebPdfServer sessionServer = LocalWebPdfServer.start();
             LocalWebPdfServer otherServer = LocalWebPdfServer.start()) {
            String otherTarget = "http://" + otherServer.getHost() + ":" + otherServer.getPort() + "/collect";
            sessionServer.respond("POST", LocalWebPdfServer.REST + "redirect", exchange -> {
                exchange.getResponseHeaders().set("Location", otherTarget);
                LocalWebPdfServer.send(exchange, 303, "text/plain", "");
            });
            AtomicInteger attempts = new AtomicInteger();
            otherServer.respond("GET", "/collect", exchange -> {
                if (attempts.incrementAndGet() == 1) {
                    exchange.getResponseHeaders().set("Retry-After", "0");
                    LocalWebPdfServer.send(exchange, 503, "text/plain", "");
                } else {
                    LocalWebPdfServer.send(exchange, 200, "application/json", "{}");
                }
            });

            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, sessionServer.getUrl()),
                    new UserAuthProvider("admin", "secret"))) {
                HttpRestRequest.createRequest(session)
                        .buildRequest(HttpMethod.POST, "redirect",
                                new StringEntity("{}", ContentType.APPLICATION_JSON))
                        .executeRequest(Object.class);
            }

            List<LocalWebPdfServer.RecordedRequest> otherRequests = otherServer.getRequests("GET", "/collect");
            assertEquals(2, otherRequests.size(), "The redirect target should have been requested twice.");
            for (LocalWebPdfServer.RecordedRequest otherRequest : otherRequests) {
                assertEquals("Bearer access-1", otherRequest.getAuthorization(),
                        "Every attempt on the redirect target should be authorized.");
            }
        }
    }

    @Test
    public void testRedirectToOtherServerKeepsAuthorization() throws Exception {
        try (LocalWebPdfServer sessionServer = LocalWebPdfServer.start();
             LocalWebPdfServer otherServer = LocalWebPdfServer.start()) {
            // Stands for a proxy address the server reports in its 303, differing from the address the client uses.
            String otherTarget = "http://" + otherServer.getHost() + ":" + otherServer.getPort() + "/collect";
            sessionServer.respond("POST", LocalWebPdfServer.REST + "redirect", exchange -> {
                exchange.getResponseHeaders().set("Location", otherTarget);
                LocalWebPdfServer.send(exchange, 303, "text/plain", "");
            });
            otherServer.respond("GET", "/collect", 200, "application/json", "{}");
            sessionServer.respond("POST", LocalWebPdfServer.REST + "redirect-same", exchange -> {
                exchange.getResponseHeaders().set("Location", LocalWebPdfServer.REST + "collect-same");
                LocalWebPdfServer.send(exchange, 303, "text/plain", "");
            });
            sessionServer.respond("GET", LocalWebPdfServer.REST + "collect-same", 200, "application/json", "{}");

            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, sessionServer.getUrl()),
                    new UserAuthProvider("admin", "secret"))) {
                HttpRestRequest.createRequest(session)
                        .buildRequest(HttpMethod.POST, "redirect",
                                new StringEntity("{}", ContentType.APPLICATION_JSON))
                        .executeRequest(Object.class);
                HttpRestRequest.createRequest(session)
                        .buildRequest(HttpMethod.POST, "redirect-same",
                                new StringEntity("{}", ContentType.APPLICATION_JSON))
                        .executeRequest(Object.class);
            }

            List<LocalWebPdfServer.RecordedRequest> otherRequests = otherServer.getRequests("GET", "/collect");
            assertEquals(1, otherRequests.size(), "The redirect to the other server should have been followed.");
            assertEquals("Bearer access-1", otherRequests.get(0).getAuthorization(),
                    "A redirect to another host or port should still be authorized.");

            List<LocalWebPdfServer.RecordedRequest> sameRequests =
                    sessionServer.getRequests("GET", LocalWebPdfServer.REST + "collect-same");
            assertEquals(1, sameRequests.size(), "The redirect on the session server should have been followed.");
            assertEquals("Bearer access-1", sameRequests.get(0).getAuthorization(),
                    "A redirect on the session server should still be authorized.");
        }
    }

    private static ExecChain.Scope createScope(HttpHost target, ClassicHttpRequest request) {
        // The handler does not use the runtime, any call would be a test failure.
        ExecRuntime execRuntime = (ExecRuntime) Proxy.newProxyInstance(ExecRuntime.class.getClassLoader(),
                new Class<?>[]{ExecRuntime.class}, (proxy, method, arguments) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
        return new ExecChain.Scope("exchange", new HttpRoute(target), request, execRuntime,
                HttpClientContext.create());
    }

}
