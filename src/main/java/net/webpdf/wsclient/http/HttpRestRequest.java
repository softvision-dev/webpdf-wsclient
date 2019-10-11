package net.webpdf.wsclient.http;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.ExceptionBean;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpRestRequest {

    @NotNull
    private final CloseableHttpClient httpClient;
    @NotNull
    private final RestSession session;
    @Nullable
    private final DataFormat dataFormat;
    @Nullable
    private String acceptHeader;
    @Nullable
    private HttpUriRequest httpUriRequest;

    /**
     * Creates a Rest request handling the messages of the given {@link RestSession}
     *
     * @param session the {@link RestSession} this Rest request is handling.
     */
    private HttpRestRequest(@NotNull RestSession session) {
        this.session = session;
        this.httpClient = session.getHttpClient();
        this.acceptHeader = session.getDataFormat() != null ? session.getDataFormat().getMimeType() : null;
        this.dataFormat = session.getDataFormat();
    }

    /**
     * Creates a Rest request handling the messages of the given {@link RestSession}
     *
     * @param session the {@link RestSession} this Rest request is handling.
     * @return A Rest request handling the messages of the given {@link RestSession}
     */
    @NotNull
    public static HttpRestRequest createRequest(@NotNull RestSession session) {
        return new HttpRestRequest(session);
    }

    /**
     * Initializes the MIME type accepted by this REST request and returns a reference to this request.
     *
     * @param mimeType The MIME type accepted by this REST request.
     * @return A reference to this REST request.
     */
    @NotNull
    public HttpRestRequest setAcceptHeader(@NotNull String mimeType) {
        this.acceptHeader = mimeType;
        return this;
    }

    /**
     * Build a HTTP request
     *
     * @param httpMethod HTTP method (GET, POST, ...)
     * @param path       REST resource URL
     * @param httpEntity data to send with request (POST)
     * @return a new HTTP request
     * @throws ResultException if the HTTP method is unknown
     */
    @NotNull
    public HttpRestRequest buildRequest(@Nullable HttpMethod httpMethod, @Nullable String path, @Nullable HttpEntity httpEntity)
        throws ResultException {
        if (httpMethod == null) {
            throw new ResultException(Result.build(Error.UNKNOWN_HTTP_METHOD));
        }

        RequestBuilder requestBuilder;

        URI uri = this.session.getURI(path != null ? path : "");

        switch (httpMethod) {
            case GET:
                requestBuilder = RequestBuilder.get(uri);
                break;
            case POST:
                requestBuilder = RequestBuilder.post(uri);
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
     * Check the response of the HTTP request. If the response is an error, it can contain a webPDF server error bean.
     *
     * @param httpResponse HTTP response
     * @throws ResultException unable to convert the error object
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

            ExceptionBean exceptionBean = DataFormat.XML.equals(this.dataFormat)
                                              ? SerializeHelper.fromXML(httpEntity, ExceptionBean.class)
                                              : SerializeHelper.fromJSON(httpEntity, ExceptionBean.class);

            responseOutput = "Server error: " + exceptionBean.getErrorMessage()
                                 + " (" + exceptionBean.getErrorCode() + ")\n"
                                 + (exceptionBean.getStackTrace() != null && !exceptionBean.getStackTrace().isEmpty() ?
                                        "Server stack trace: " + exceptionBean.getStackTrace() + "\n" : "");
        } else {
            try {
                responseOutput = EntityUtils.toString(httpEntity);
            } catch (IOException ex) {
                throw new ResultException(Result.build(Error.HTTP_CUSTOM_ERROR, ex));
            }
        }

        // throw the extracted error message
        throw new ResultException(Result.build(Error.HTTP_CUSTOM_ERROR).addMessage(
            statusLine.getStatusCode() + " " + statusLine.getReasonPhrase() + "\n" + responseOutput));
    }

    /**
     * Executes the HTTP request
     *
     * @param outputStream Target stream for the response
     * @throws ResultException a {@link ResultException}
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
     * Executes the HTTP request
     *
     * @param type class name for response
     * @param <T>  class for response
     * @return response object
     * @throws ResultException if HTTP entity should be save as file
     */
    @Nullable
    public <T> T executeRequest(@Nullable Class<T> type) throws ResultException {

        try (CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpUriRequest)) {

            checkResponse(closeableHttpResponse);

            HttpEntity httpEntity = closeableHttpResponse.getEntity();

            if (type == null || httpEntity.getContent().available() <= 0) {
                return null;
            }

            return DataFormat.XML.equals(this.dataFormat)
                       ? SerializeHelper.fromXML(httpEntity, type)
                       : SerializeHelper.fromJSON(httpEntity, type);
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR, ex));
        }
    }

}
