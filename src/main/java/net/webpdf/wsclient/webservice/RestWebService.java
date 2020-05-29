package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

public abstract class RestWebService
        <T_REST_DOCUMENT extends RestDocument, T_OPERATION_TYPE>
        extends AbstractWebService<T_REST_DOCUMENT, RestSession<T_REST_DOCUMENT>, T_OPERATION_TYPE, T_REST_DOCUMENT> {

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    public RestWebService(@NotNull RestSession<T_REST_DOCUMENT> session, @NotNull WebServiceType webServiceType) {
        super(webServiceType, session);
    }

    /**
     * Execute webservice operation and returns result
     *
     * @return the result
     * @throws ResultException an {@link ResultException}
     */
    @Override
    @Nullable
    public T_REST_DOCUMENT process() throws ResultException {
        if (getDocument() == null) {
            return null;
        }
        String urlPath = getWebServiceType().equals(WebServiceType.URLCONVERTER)
                ? getWebServiceType().getRestEndpoint()
                : getWebServiceType().getRestEndpoint().replace(
                WebServiceType.ID_PLACEHOLDER,
                getDocument().getSourceDocumentId() != null ? getDocument().getSourceDocumentId() : ""
        );

        DocumentManager<T_REST_DOCUMENT> documentManager = getSession().getDocumentManager();

        DocumentFileBean documentFileBean = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.POST, urlPath, getWebServiceOptions())
                .executeRequest(DocumentFileBean.class);
        T_REST_DOCUMENT restDocument = documentManager.getDocument(documentFileBean);
        restDocument.setDocumentFile(documentFileBean);

        if (documentManager.isActiveDocumentHistory()) {
            documentManager.updateDocumentHistory(restDocument.getDocumentFile());
        }

        return restDocument;
    }

    /**
     * Creates {@link HttpEntity} with webservice parameters
     *
     * @return {@link HttpEntity} with webservice parameters
     * @throws ResultException an {@link ResultException}
     */
    @NotNull
    private HttpEntity getWebServiceOptions() throws ResultException {
        try {
            StringEntity stringEntity = new StringEntity(
                    getSession().getDataFormat() == DataFormat.XML
                            ? SerializeHelper.toXML(getOperationData(), getOperationData().getClass())
                            : SerializeHelper.toJSON(getOperationData()),
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
