package net.webpdf.wsclient.session.connection.http;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.openapi.WebserviceException;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An instance of {@link HttpRestRequest} monitors and executes a webPDF wsclient request executed within a
 * {@link RestSession} and handles the server´s response.
 */
public class HttpRestRequest {

    private final @NotNull CloseableHttpClient httpClient;
    private final @NotNull RestSession<?> session;
    private @Nullable String acceptHeader = DataFormat.JSON.getMimeType();
    private @Nullable HttpUriRequest httpUriRequest;

    /**
     * Creates a {@link HttpRestRequest} preparing and executing a request for a given {@link RestSession} to provide
     * a matching response object.
     *
     * @param session the {@link RestSession} this {@link HttpRestRequest} is handling the request and response for.
     */
    private HttpRestRequest(@NotNull RestSession<?> session) {
        this.session = session;
        this.httpClient = session.getHttpClient();
    }

    /**
     * Creates a {@link HttpRestRequest} preparing and executing a request for a given {@link RestSession} to provide
     * a matching response object.
     *
     * @param session the {@link RestSession} this Rest request is handling.
     * @return A {@link HttpRestRequest} preparing and executing a request for a given {@link RestSession} to provide
     * a matching response object.
     */
    public static @NotNull HttpRestRequest createRequest(@NotNull RestSession<?> session) {
        return new HttpRestRequest(session);
    }

    /**
     * Selects the MIME type  of the data transfer object ({@link HttpEntity}) that shall be accepted as a valid
     * response payload for this {@link HttpRestRequest}.
     *
     * @param mimeType The MIME type  of the transfer data object that shall be accepted as a valid response payload for
     *                 this {@link HttpRestRequest}.
     * @return The {@link HttpRestRequest} instance itself.
     */
    public @NotNull HttpRestRequest setAcceptHeader(@NotNull String mimeType) {
        this.acceptHeader = mimeType;
        return this;
    }

    /**
     * Prepare the {@link HttpRestRequest} to execute the selected {@link HttpMethod} on the given resource path
     * ({@link URI}) and providing the given {@link HttpEntity} as it´s data transfer object (parameters).
     *
     * @param httpMethod The {@link HttpMethod} to execute.
     * @param path       The resource path ({@link URI}) to execute the request on.
     * @param httpEntity The data transfer object {@link HttpEntity} to include in the request´s content.
     * @return The {@link HttpRestRequest} instance itself.
     * @throws ResultException Shall be thrown, if creating initializing the {@link HttpRestRequest} failed for the
     *                         given parameters.
     */
    public @NotNull HttpRestRequest buildRequest(@NotNull HttpMethod httpMethod, @NotNull String path,
            @Nullable HttpEntity httpEntity) throws ResultException {
        URI uri = this.session.getURI(path);
        return buildRequest(httpMethod, uri, httpEntity, null);
    }

    /**
     * Prepare the {@link HttpRestRequest} to execute the selected {@link HttpMethod} on the given resource path
     * ({@link URI}) and providing the given {@link HttpEntity} as it´s data transfer object (parameters).
     *
     * @param httpMethod   The {@link HttpMethod} to execute.
     * @param path         The resource path ({@link URI}) to execute the request on.
     * @param httpEntity   The data transfer object {@link HttpEntity} to include in the request´s content.
     * @param authMaterial The {@link AuthMaterial} to use for authorization.
     * @return The {@link HttpRestRequest} instance itself.
     * @throws ResultException Shall be thrown, if creating initializing the {@link HttpRestRequest} failed for the
     *                         given parameters.
     */
    public @NotNull HttpRestRequest buildRequest(@NotNull HttpMethod httpMethod, @NotNull String path,
            @Nullable HttpEntity httpEntity, @Nullable AuthMaterial authMaterial) throws ResultException {
        URI uri = this.session.getURI(path);
        return buildRequest(httpMethod, uri, httpEntity, authMaterial);
    }

    /**
     * Prepare the {@link HttpRestRequest} to execute the selected {@link HttpMethod} on the given resource path
     * ({@link URI}) and providing the given {@link HttpEntity} as it´s data transfer object (parameters).
     *
     * @param httpMethod The {@link HttpMethod} to execute.
     * @param uri        The resource path ({@link URI}) to execute the request on.
     * @param httpEntity The data transfer object {@link HttpEntity} to include in the request´s content.
     * @return The {@link HttpRestRequest} instance itself.
     * @throws ResultException Shall be thrown, if creating initializing the {@link HttpRestRequest} failed for the
     *                         given parameters.
     */
    public @NotNull HttpRestRequest buildRequest(@NotNull HttpMethod httpMethod, @NotNull URI uri,
            @Nullable HttpEntity httpEntity) throws ResultException {
        return buildRequest(httpMethod, uri, httpEntity, null);
    }

    /**
     * Prepare the {@link HttpRestRequest} to execute the selected {@link HttpMethod} on the given ({@link URI}) and
     * providing the given {@link HttpEntity} as it´s data transfer object (parameters).
     *
     * @param httpMethod   The {@link HttpMethod} to execute.
     * @param uri          The resource ({@link URI}) to execute the request on.
     * @param httpEntity   The data transfer object {@link HttpEntity} to include in the request´s content.
     * @param authMaterial The {@link AuthMaterial} to use for authorization.
     * @return The {@link HttpRestRequest} instance itself.
     * @throws ResultException Shall be thrown, if creating initializing the {@link HttpRestRequest} failed for the
     *                         given parameters.
     */
    public @NotNull HttpRestRequest buildRequest(@NotNull HttpMethod httpMethod, @NotNull URI uri,
            @Nullable HttpEntity httpEntity, @Nullable AuthMaterial authMaterial) throws ResultException {
        switch (httpMethod) {
            case GET:
                httpUriRequest = new HttpGet(uri);
                break;
            case POST:
                httpUriRequest = new HttpPost(uri);
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(uri);
                break;
            case PUT:
                httpUriRequest = new HttpPut(uri);
                break;
            default:
                throw new ClientResultException(Error.UNKNOWN_HTTP_METHOD);
        }

        httpUriRequest.addHeader(HttpHeaders.ACCEPT, this.acceptHeader);
        Header authorizationHeader = authMaterial != null ?
                authMaterial.getAuthHeader() : session.getAuthMaterial().getAuthHeader();
        if (authorizationHeader != null) {
            httpUriRequest.addHeader(authorizationHeader);
        }
        if (httpEntity != null) {
            httpUriRequest.setEntity(httpEntity);
        }

        return this;
    }

    /**
     * <p>
     * Checks whether the given {@link HttpResponse} represents a failure state and in that case shall rethrow the
     * failure state in form of a matching {@link ResultException}.
     * </p>
     * <p>
     * Should the failure state represent a server side failure is shall throw a {@link ServerResultException}.<br>
     * </p>
     *
     * @param httpResponse The {@link HttpResponse} to check for a failure state.
     * @throws ResultException Shall be thrown, if the {@link HttpResponse} represents a failure state.
     * @see ServerResultException
     */
    private void checkResponse(@NotNull ClassicHttpResponse httpResponse) throws ResultException {

        // any error?
        int code = httpResponse.getCode();
        if (code == HttpStatus.SC_OK) {
            return;
        }

        // get the response
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        String exceptionMessage = "";

        // is this a webPDF server response or a general server error?
        String contentType = httpEntity.getContentType();
        if (DataFormat.JSON.matches(contentType)) {
            WebserviceException wsException = SerializeHelper.fromJSON(httpEntity, WebserviceException.class);
            if (wsException.getErrorCode() != 0) {
                throw new ServerResultException(wsException);
            }
        } else {
            try {
                exceptionMessage = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            } catch (ParseException | IOException ex) {
                throw new ClientResultException(Error.HTTP_CUSTOM_ERROR, ex);
            }
        }

        // throw the extracted error message
        throw new ClientResultException(Error.HTTP_CUSTOM_ERROR).appendMessage(
                code + " " + httpResponse.getReasonPhrase() + "\n" + exceptionMessage);
    }

    /**
     * Executes this {@link HttpRestRequest} and shall write the contained data transfer object {@link HttpEntity} to
     * the given {@link OutputStream}.
     *
     * @param outputStream The {@link OutputStream} to write the data transfer object {@link HttpEntity} to.
     * @throws ResultException Shall be thrown, if writing to the {@link OutputStream} failed.
     */
    public void executeRequest(@NotNull OutputStream outputStream) throws ResultException {
        try {
            this.httpClient.execute(httpUriRequest, response -> {
                response.getEntity().writeTo(outputStream);
                return null;
            });
        } catch (IOException ex) {
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

    /**
     * <p>
     * Executes {@link HttpRestRequest} and shall attempt to translate the response´s data transfer object
     * ({@link HttpEntity}) to an instance of the given type.
     * </p>
     * <p>
     * The resulting intermediate {@link HttpResponse} shall be checked via {@link #checkResponse(ClassicHttpResponse)}}.
     * </p>
     *
     * @param type The type to translate the data transfer object {@link HttpEntity} to.
     * @return The resulting data transfer object {@link HttpEntity} translated to an instance of the given type.
     * @throws ResultException Shall be thrown, should the {@link HttpResponse} not be readable or should it´s
     *                         validation via {@link #checkResponse(ClassicHttpResponse)} fail.
     */
    public <T> @Nullable T executeRequest(@NotNull Class<T> type) throws ResultException {
        try {
            return this.httpClient.execute(httpUriRequest, response -> {
                try {
                    checkResponse(response);
                } catch (ResultException ex) {
                    throw new IOException(ex);
                }

                HttpEntity httpEntity = response.getEntity();

                ContentType contentType = httpEntity.getContentType() == null ? ContentType.DEFAULT_TEXT :
                        ContentType.create(httpEntity.getContentType(), StandardCharsets.UTF_8);
                String mimeType = contentType.getMimeType();
                Charset charset = contentType.getCharset() != null ? contentType.getCharset() : StandardCharsets.UTF_8;

                String value = EntityUtils.toString(httpEntity, charset);
                if (StringUtils.isEmpty(value)) {
                    return null;
                }

                if (!DataFormat.JSON.matches(mimeType)) {
                    return null;
                }
                try (StringReader stringReader = new StringReader(value)) {
                    StreamSource streamSource = new StreamSource(stringReader);
                    return SerializeHelper.fromJSON(streamSource, type);
                } catch (ResultException ex) {
                    throw new IOException(ex);
                }
            });
        } catch (IOException ex) {
            if (ex.getCause() instanceof ResultException) {
                throw (ResultException) ex.getCause();
            }
            throw new ClientResultException(Error.HTTP_IO_ERROR, ex);
        }
    }

}
