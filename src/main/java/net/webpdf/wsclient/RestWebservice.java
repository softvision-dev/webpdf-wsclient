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
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

abstract class RestWebservice<T_OPERATION_TYPE> extends AbstractWebService<RestDocument, T_OPERATION_TYPE, RestDocument> {

    RestWebservice(Session session, WebServiceType webServiceType) {
        super(webServiceType, session);
    }

    @Override
    public RestDocument process() throws ResultException {

        String urlPath = this.webServiceType.equals(WebServiceType.URLCONVERTER)
                ? webServiceType.getRestEndpoint()
                : webServiceType.getRestEndpoint().replace(WebServiceType.ID_PLACEHOLDER, this.document.getSourceDocumentId());

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
    private HttpEntity getWebServiceOptions() throws ResultException {
        try {
            StringEntity stringEntity = new StringEntity(
                    this.session.getDataFormat() == DataFormat.XML
                            ? SerializeHelper.toXML(this.operation, this.operation.getClass())
                            : SerializeHelper.toJSON(this.operation)
            );

            stringEntity.setContentType(this.session.getDataFormat().getMimeType());
            return stringEntity;

        } catch (UnsupportedEncodingException ex) {
            throw new ResultException(Result.build(Error.TO_XML, ex));
        }
    }

}
