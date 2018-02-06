package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.session.RestSession;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Document manager - session bounded
 */
public class DocumentManager {

    private final Map<String, RestDocument> documentMap = new HashMap<>();
    private final RestSession session;

    /**
     * Initializes a document manager for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a document manager shall be created for.
     */
    public DocumentManager(RestSession session) {
        this.session = session;
    }

    /**
     * Downloads {@link SoapDocument} or {@link RestDocument} to target path
     *
     * @param document     {@link SoapDocument} or {@link RestDocument} instance
     * @param outputStream {@link OutputStream} for downloaded content
     * @return if the saving of the result was successfully
     * @throws ResultException a {@link ResultException}
     */
    public boolean downloadDocument(RestDocument document, OutputStream outputStream) throws ResultException {

        if (document == null || outputStream == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }

        HttpRestRequest.createRequest(this.session)
            .setAcceptHeader("application/octet-stream")
            .buildRequest(HttpMethod.GET, "documents/" + document.getSourceDocumentId(), null)
            .executeRequest(outputStream);

        return true;
    }

    /**
     * Uploads the given file to the webPDF server and returns the {@link RestDocument} reference to the uploaded files.
     *
     * @param file The file, that shall be uploaded for further processing.
     * @return A {@link RestDocument} referencing the uploaded document.
     * @throws IOException an {@link IOException}
     */
    public RestDocument uploadDocument(File file) throws IOException {
        if (file == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
        HttpEntity entity = builder.build();

        return getDocument(HttpRestRequest.createRequest(session)
                               .buildRequest(HttpMethod.POST, "documents/", entity)
                               .executeRequest(DocumentFileBean.class));
    }

    /**
     * Uploads the given {@link DocumentFileBean} to the webPDF server and returns the {@link RestDocument} reference
     * to the uploaded resource.
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be uploaded for further processing.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    public RestDocument getDocument(DocumentFileBean documentFileBean) throws ResultException {
        if (documentFileBean == null || documentFileBean.getDocumentId() == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        String id = documentFileBean.getDocumentId();
        if (documentMap.containsKey(id)) {
            return this.documentMap.get(id);
        }

        RestDocument restDocument = new RestDocument(id);
        documentMap.put(documentFileBean.getDocumentId(), new RestDocument(documentFileBean.getDocumentId()));
        return restDocument;
    }
}
