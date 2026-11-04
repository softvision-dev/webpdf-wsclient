package net.webpdf.wsclient.session.rest.administration;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.RestSession;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Reads the Prometheus metrics of a webPDF server. The request is shared by
 * {@link AbstractAdministrationManager#fetchMetrics()} and the default implementation of
 * {@link AdministrationManager#fetchMetrics()}, which keeps implementations of {@link AdministrationManager} written
 * against an earlier 10.x version compatible.
 */
final class MetricsRequest {

    private MetricsRequest() {
    }

    /**
     * Fetches the Prometheus metrics of the server the given {@link RestSession} is connected to.
     *
     * @param session The {@link RestSession} connected to the webPDF server.
     * @return The Prometheus metrics as a plain-text string.
     * @throws ResultException Shall be thrown if the request failed.
     */
    static @NotNull String fetch(@NotNull RestSession<?> session) throws ResultException {
        try {
            // Build the path explicitly and drop user info, query and fragment of the server URL: HttpClient rejects
            // URIs containing user info, and appending to the URL string would place the path behind a query.
            URIBuilder uriBuilder = new URIBuilder(session.getSessionContext().getUrl().toURI())
                    .setUserInfo(null)
                    .removeQuery()
                    .setFragment(null);
            String basePath = uriBuilder.getPath() == null ? "" : uriBuilder.getPath();
            if (!basePath.endsWith("/")) {
                basePath += "/";
            }
            URI metricsUri = uriBuilder.setPath(basePath + "metrics").build();

            String metrics = HttpRestRequest.createRequest(session)
                    .setAcceptHeader(DataFormat.PLAIN.getMimeType())
                    .buildRequest(HttpMethod.GET, metricsUri)
                    .executeRequest(String.class);

            if (metrics == null) {
                throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
            }

            return metrics;
        } catch (URISyntaxException ex) {
            throw new ClientResultException(Error.INVALID_URL, ex);
        }
    }

}
