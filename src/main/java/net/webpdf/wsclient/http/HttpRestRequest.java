package net.webpdf.wsclient.http;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.Failure;
import net.webpdf.wsclient.schema.stubs.FaultInfo;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.DatatypeConverter;

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
    private final @Nullable DataFormat dataFormat;
    private @Nullable String acceptHeader;
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
        this.acceptHeader = session.getDataFormat() != null ? session.getDataFormat().getMimeType() : null;
        this.dataFormat = session.getDataFormat();
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
    public @NotNull HttpRestRequest buildRequest(@Nullable HttpMethod httpMethod, @Nullable String path,
            @Nullable HttpEntity httpEntity) throws ResultException {
        URI uri = this.session.getURI(path != null ? path : "");
        return buildRequest(httpMethod, uri, httpEntity);
    }

    /**
     * Prepare the {@link HttpRestRequest} to execute the selected {@link HttpMethod} on the given ({@link URI}) and
     * providing the given {@link HttpEntity} as it´s data transfer object (parameters).
     *
     * @param httpMethod The {@link HttpMethod} to execute.
     * @param uri        The resource ({@link URI}) to execute the request on.
     * @param httpEntity The data transfer object {@link HttpEntity} to include in the request´s content.
     * @return The {@link HttpRestRequest} instance itself.
     * @throws ResultException Shall be thrown, if creating initializing the {@link HttpRestRequest} failed for the
     *                         given parameters.
     */
    public @NotNull HttpRestRequest buildRequest(@Nullable HttpMethod httpMethod, @Nullable URI uri,
            @Nullable HttpEntity httpEntity) throws ResultException {
        if (httpMethod == null) {
            throw new ResultException(Result.build(Error.UNKNOWN_HTTP_METHOD));
        }

        if (uri == null) {
            uri = this.session.getURI("");
        }

        RequestBuilder requestBuilder;

        switch (httpMethod) {
            case GET:
                requestBuilder = RequestBuilder.get(uri);
                break;
            case POST:
                requestBuilder = RequestBuilder.post(uri);
                break;
            case DELETE:
                requestBuilder = RequestBuilder.delete(uri);
                break;
            case PUT:
                requestBuilder = RequestBuilder.put(uri);
                break;
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_HTTP_METHOD));
        }

        if (this.session.getCredentials() != null) {
            String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(
                    (this.session.getCredentials().getUserPrincipal().getName()
                            + ":" + this.session.getCredentials().getPassword()).getBytes(StandardCharsets.ISO_8859_1));
            requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, basicAuth);
        }

        requestBuilder.addHeader(HttpHeaders.ACCEPT, this.acceptHeader);
        requestBuilder.setCharset(StandardCharsets.UTF_8);

        if (this.session.getToken() != null && !this.session.getToken().getToken().isEmpty()) {
            requestBuilder.addHeader("Token", this.session.getToken().getToken());
        }

        if (httpEntity != null) {
            requestBuilder.setEntity(httpEntity);
        }

        httpUriRequest = requestBuilder.build();

        return this;
    }

    /**
     * <p>
     * Checks whether the given {@link HttpResponse} represents a failure state and in that case shall rethrow the
     * failure state in form of a matching {@link ResultException}.
     * </p>
     * <p>
     * Should the failure state represent a {@link FaultInfo} object, it shall indicate a server side failure to execute
     * the operation and shall be wrapped in a {@link WebServiceException}.<br>
     * The {@link WebServiceException} shall be set as the cause of the thrown {@link ResultException} and shall
     * additionally be returned via the {@link Result#getException()} covered in the {@link ResultException}.
     * </p>
     *
     * @param httpResponse The {@link HttpResponse} to check for a failure state.
     * @throws ResultException Shall be thrown, if the {@link HttpResponse} represents a failure state.
     * @see ResultException
     * @see Result#getException()
     */
    private void checkResponse(@NotNull HttpResponse httpResponse) throws ResultException {

        // any error?
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            return;
        }

        // get the response
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity == null) {
            throw new ResultException(Result.build(Error.HTTP_EMPTY_ENTITY));
        }

        String responseOutput;

        // is this a webPDF server response or a general server error?
        Header header = httpEntity.getContentType();
        if (header != null && (header.getValue().equals(DataFormat.XML.getMimeType())
                || header.getValue().equals(DataFormat.JSON.getMimeType()))) {

            Failure exceptionBean = DataFormat.XML.equals(this.dataFormat)
                    ? SerializeHelper.fromXML(httpEntity, Failure.class)
                    : SerializeHelper.fromJSON(httpEntity, Failure.class);

            responseOutput = "Server error: " + exceptionBean.getErrorMessage()
                    + " (" + exceptionBean.getErrorCode() + ")\n"
                    + (exceptionBean.getStackTrace() != null && !exceptionBean.getStackTrace().isEmpty() ?
                    "Server stack trace: " + exceptionBean.getStackTrace() + "\n" : "");
            if (exceptionBean.getErrorCode() != 0) {
                FaultInfo faultInfo = new FaultInfo();
                faultInfo.setErrorMessage(exceptionBean.getErrorMessage());
                faultInfo.setErrorCode(exceptionBean.getErrorCode());
                faultInfo.setStackTrace(exceptionBean.getStackTrace());
                throw new ResultException(Result.build(Error.REST_EXECUTION,
                        new WebServiceException(responseOutput, faultInfo)));
            }
        } else {
            try {
                responseOutput = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new ResultException(Result.build(Error.HTTP_CUSTOM_ERROR, ex));
            }
        }

        // throw the extracted error message
        throw new ResultException(Result.build(Error.HTTP_CUSTOM_ERROR).appendMessage(
                statusLine.getStatusCode() + " " + statusLine.getReasonPhrase() + "\n" + responseOutput));
    }

    /**
     * Executes this {@link HttpRestRequest} and shall write the contained data transfer object {@link HttpEntity} to
     * the given {@link OutputStream}.
     *
     * @param outputStream The {@link OutputStream} to write the data transfer object {@link HttpEntity} to.
     * @throws ResultException Shall be thrown, if writing to the {@link OutputStream} failed.
     */
    public void executeRequest(@Nullable OutputStream outputStream) throws ResultException {
        if (outputStream == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        try (CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpUriRequest)) {
            closeableHttpResponse.getEntity().writeTo(outputStream);
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR, ex));
        }
    }

    /**
     * <p>
     * Executes {@link HttpRestRequest} and shall attempt to translate the response´s data transfer object
     * ({@link HttpEntity}) to an instance of the given type.
     * </p>
     * <p>
     * The resulting intermediate {@link HttpResponse} shall be checked via {@link #checkResponse(HttpResponse)}.
     * </p>
     *
     * @param type The type to translate the data transfer object {@link HttpEntity} to.
     * @return The resulting data transfer object {@link HttpEntity} translated to an instance of the given type.
     * @throws ResultException Shall be thrown, should the {@link HttpResponse} not be readable or should it´s
     *                         validation via {@link #checkResponse(HttpResponse)} fail.
     */
    public <T> @Nullable T executeRequest(@Nullable Class<T> type) throws ResultException {

        try (CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpUriRequest)) {

            checkResponse(closeableHttpResponse);

            HttpEntity httpEntity = closeableHttpResponse.getEntity();

            ContentType contentType = ContentType.getOrDefault(httpEntity);
            String mimeType = contentType.getMimeType();
            Charset charset = contentType.getCharset() != null ? contentType.getCharset() : StandardCharsets.UTF_8;

            String value = EntityUtils.toString(httpEntity, charset);
            if (StringUtils.isEmpty(value)) {
                return null;
            }

            if (mimeType == null || this.dataFormat == null || !mimeType.equals(this.dataFormat.getMimeType())) {
                return null;
            }
            try (StringReader stringReader = new StringReader(value)) {
                StreamSource streamSource = new StreamSource(stringReader);
                return DataFormat.XML.equals(this.dataFormat)
                        ? SerializeHelper.fromXML(streamSource, type)
                        : SerializeHelper.fromJSON(streamSource, type);
            }
        } catch (ResultException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR, ex));
        }
    }

}
