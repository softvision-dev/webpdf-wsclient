package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.manager.DocumentManager;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.AbstractWebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * An instance of {@link RestWebService} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_OPERATION_DATA>      The operation type of the targeted webservice endpoint.
 * @param <T_OPERATION_PARAMETER> The parameter type of the targeted webservice endpoint.
 * @param <T_REST_DOCUMENT>       The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public abstract class RestWebService<T_OPERATION_DATA, T_OPERATION_PARAMETER, T_REST_DOCUMENT extends RestDocument>
        extends AbstractWebService<RestSession<T_REST_DOCUMENT>, T_OPERATION_DATA, T_OPERATION_PARAMETER,
        T_REST_DOCUMENT, OperationBilling, OperationPdfPassword, OperationSettings> {

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link RestSession}.
     *
     * @param session        The {@link RestSession} the webservice interface shall be created for.
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     */
    public RestWebService(@NotNull RestSession<T_REST_DOCUMENT> session, @NotNull WebServiceType webServiceType) {
        super(webServiceType, session);
    }

    /**
     * Execute the webservice operation and returns the resulting {@link RestDocument}.
     *
     * @return The resulting {@link RestDocument}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    public @Nullable T_REST_DOCUMENT process() throws ResultException {
        if (getSourceDocument() == null) {
            return null;
        }

        String urlPath = getWebServiceType().equals(WebServiceType.URLCONVERTER) ?
                getWebServiceType().getRestEndpoint() : getWebServiceType().getRestEndpoint().replace(
                WebServiceType.ID_PLACEHOLDER, getSourceDocument().getDocumentId() != null ?
                        getSourceDocument().getDocumentId() : ""
        );

        DocumentManager<T_REST_DOCUMENT> documentManager = getSession().getDocumentManager();

        HttpRestRequest request = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.POST, urlPath, getWebServiceOptions());
        DocumentFile documentFile = request.executeRequest(DocumentFile.class);

        T_REST_DOCUMENT restDocument = null;
        if (documentFile != null) {
            restDocument = documentManager.synchronize(documentFile);
        }
        return restDocument;
    }

    /**
     * Creates a {@link HttpEntity} reflecting the webservice parameters.
     *
     * @return A {@link HttpEntity} reflecting the webservice parameters.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private @NotNull HttpEntity getWebServiceOptions() throws ResultException {
        try {
            return new StringEntity(
                    getSession().getDataFormat() == DataFormat.XML ?
                            SerializeHelper.toXML(getOperationData(), getOperationData().getClass()) :
                            SerializeHelper.toJSON(getOperationData()),
                    getSession().getDataFormat() != null ?
                            ContentType.create(getSession().getDataFormat().getMimeType(), StandardCharsets.UTF_8) :
                            null);
        } catch (IOException | UnsupportedCharsetException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

}