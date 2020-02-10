package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.RestDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

public abstract class RestWebservice<T_OPERATION_TYPE> extends AbstractWebService<RestDocument, T_OPERATION_TYPE, RestDocument> {

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    RestWebservice(@NotNull Session session, @NotNull WebServiceType webServiceType) {
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
    public RestDocument process() throws ResultException {
        if (this.document == null) {
            return null;
        }
        String urlPath = this.webServiceType.equals(WebServiceType.URLCONVERTER)
                             ? webServiceType.getRestEndpoint()
                             : webServiceType.getRestEndpoint().replace(
            WebServiceType.ID_PLACEHOLDER,
            this.document.getSourceDocumentId() != null ? this.document.getSourceDocumentId() : ""
        );

        return ((RestSession) this.session).getDocumentManager().getDocument(
            HttpRestRequest.createRequest((RestSession) this.session)
                .buildRequest(HttpMethod.POST, urlPath, getWebServiceOptions())
                .executeRequest(DocumentFileBean.class)
        );
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
                this.session.getDataFormat() == DataFormat.XML
                    ? SerializeHelper.toXML(this.operation, this.operation.getClass())
                    : SerializeHelper.toJSON(this.operation),
                Charsets.UTF_8);

            if (this.session.getDataFormat() != null) {
                stringEntity.setContentType(this.session.getDataFormat().getMimeType());
            }
            return stringEntity;

        } catch (IOException | UnsupportedCharsetException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

}
