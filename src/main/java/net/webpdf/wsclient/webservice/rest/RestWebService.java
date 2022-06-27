package net.webpdf.wsclient.webservice.rest;

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
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * An instance of {@link RestWebService} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT>  The expected {@link RestDocument} type for the documents used by the webPDF server.
 * @param <T_OPERATION_TYPE> The {@link WebServiceType} of the targeted webservice endpoint.
 */
public abstract class RestWebService<T_REST_DOCUMENT extends RestDocument, T_OPERATION_TYPE>
        extends AbstractWebService<T_REST_DOCUMENT, RestSession<T_REST_DOCUMENT>, T_OPERATION_TYPE, T_REST_DOCUMENT> {

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link RestSession}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link RestSession} the webservice interface shall be created for.
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
        if (getDocument() == null) {
            return null;
        }

        String urlPath = getWebServiceType().equals(WebServiceType.URLCONVERTER) ?
                getWebServiceType().getRestEndpoint() : getWebServiceType().getRestEndpoint().replace(
                WebServiceType.ID_PLACEHOLDER, getDocument().getDocumentId() != null ?
                        getDocument().getDocumentId() : ""
        );

        DocumentManager<T_REST_DOCUMENT> documentManager = getSession().getDocumentManager();

        DocumentFile documentFile = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.POST, urlPath, getWebServiceOptions())
                .executeRequest(DocumentFile.class);

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
            StringEntity stringEntity = new StringEntity(
                    getSession().getDataFormat() == DataFormat.XML ?
                            SerializeHelper.toXML(getOperationData(), getOperationData().getClass()) :
                            SerializeHelper.toJSON(getOperationData()),
                    Charsets.UTF_8);

            if (getSession().getDataFormat() != null) {
                stringEntity.setContentType(getSession().getDataFormat().getMimeType());
            }
            return stringEntity;

        } catch (IOException | UnsupportedCharsetException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

}
