package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.WebServiceType;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Document manager - session bounded
 */
public class DocumentManager {

    @NotNull
    private final ConcurrentHashMap<String, RestDocument> documentMap = new ConcurrentHashMap<>();
    @NotNull
    private final RestSession session;
    private boolean useHistory = false;

    /**
     * Initializes a document manager for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a document manager shall be created for.
     */
    public DocumentManager(@NotNull RestSession session) {
        this.session = session;
    }

    /**
     * Downloads {@link SoapDocument} or {@link RestDocument} to target path
     *
     * @param document     {@link SoapDocument} or {@link RestDocument} instance
     * @param outputStream {@link OutputStream} for downloaded content
     * @throws ResultException a {@link ResultException}
     */
    @Deprecated
    public void downloadDocument(@Nullable RestDocument document, @Nullable OutputStream outputStream) throws ResultException {
        if (document == null || outputStream == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }

        String documentId = document.getSourceDocumentId();
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        downloadDocument(documentId, outputStream);
    }

    /**
     * Downloads {@link RestDocument} referenced by document id from server and stores the content in the {@link OutputStream}
     *
     * @param documentId   The document id of the {@link RestDocument}
     * @param outputStream {@link OutputStream} for downloaded content
     * @throws ResultException a {@link ResultException}
     */
    public void downloadDocument(@NotNull String documentId, @Nullable OutputStream outputStream) throws ResultException {

        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }

        HttpRestRequest.createRequest(this.session)
                .setAcceptHeader("application/octet-stream")
                .buildRequest(HttpMethod.GET, "documents/" + documentId, null)
                .executeRequest(outputStream);
    }

    /**
     * Uploads the given {@link File} to the server and returns the {@link RestDocument} reference
     * to the uploaded resource.
     *
     * @param file The file, that shall be uploaded for further processing.
     * @return a {@link RestDocument} referencing the uploaded document.
     * @throws IOException an {@link IOException}
     */
    @NotNull
    public RestDocument uploadDocument(@Nullable File file) throws IOException {
        if (file == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(StandardCharsets.UTF_8);
        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
        HttpEntity entity = builder.build();

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("history", Boolean.toString(useHistory)));

        URI uri = this.session.getURI("documents", parameters);
        DocumentFileBean documentFileBean = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, uri, entity)
                .executeRequest(DocumentFileBean.class);

        return createRestDocument(documentFileBean);
    }

    /**
     * Creates a {@link RestDocument} based on the {@link DocumentFileBean} ans tores the document in internal
     * document map. Gets the history for the document, too.
     *
     * @param documentFileBean document information
     * @return created {@link RestDocument}
     * @throws ResultException unable to create the document
     */
    private RestDocument createRestDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        if (documentFileBean == null || documentFileBean.getDocumentId() == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        RestDocument restDocument = new RestDocument(documentFileBean.getDocumentId());
        restDocument.setDocumentFile(documentFileBean);

        if (this.useHistory) {
            fetchHistoryForDocument(restDocument);
        }

        this.documentMap.put(documentFileBean.getDocumentId(), restDocument);
        return restDocument;
    }

    /**
     * Returns the {@link RestDocument} from the internal document map, by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be returned.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException if {@link RestDocument} wasn't found in the internal document map
     */
    @NotNull
    public RestDocument findDocument(@NotNull String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        return this.documentMap.get(documentId);
    }

    /**
     * returns the {@link RestDocument} from the internal document map, by given {@link DocumentFileBean} or if the
     * Document doesn't exist create a new entry from the given {@link DocumentFileBean}
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be returned or added for further processing.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @NotNull
    @Deprecated
    public RestDocument getDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String id = getDocumentID(documentFileBean);
        if (containsDocument(id)) {
            return this.documentMap.get(id);
        }
        return createRestDocument(documentFileBean);
    }

    /**
     * Deletes the given {@link RestDocument} from the webPDF server by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be deleted.
     * @throws ResultException If document does not exist
     */
    public void deleteDocument(@NotNull String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.DELETE, "documents/" + documentId, null)
                .executeRequest(Object.class);

        this.documentMap.remove(documentId);
    }

    /**
     * Renames the {@link RestDocument} on the webPDF server, by given documentId, to the given filename.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be renamed.
     * @param fileName   The new file name
     * @return A {@link RestDocument} referencing the renamed resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public RestDocument renameDocument(@NotNull String documentId, @Nullable String fileName) throws IOException {

        RestDocument restDocument = findDocument(documentId);

        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setFileName(fileName);

        DocumentFileBean documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.POST, "documents/" + documentId + "/update", this.getWebServiceOptions(documentFileBean))
                .executeRequest(DocumentFileBean.class);

        restDocument.setDocumentFile(documentFile);
        return restDocument;
    }

    /**
     * Fetches file history information from the server for the given {@link RestDocument}.
     *
     * @param restDocument The {@link RestDocument} for whom the history shall be updated.
     * @throws ResultException a {@link ResultException}
     */
    private void fetchHistoryForDocument(@NotNull RestDocument restDocument) throws ResultException {

        HistoryEntry[] history = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.GET, "documents/" + restDocument.getSourceDocumentId() + "/history", null)
                .executeRequest(HistoryEntry[].class);

        if (history == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }

        restDocument.storeHistory(history);
    }

    /**
     * Activates a history entry for the {@link RestDocument}
     *
     * @param documentId unique document id
     * @param historyId  unique history id to activate
     * @return document for which the active history was changed
     * @throws ResultException unable to change history
     */
    public RestDocument activateHistory(@NotNull String documentId, int historyId) throws ResultException {
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setId(historyId);
        historyEntry.setActive(true);
        return setDocumentHistoryElement(documentId, historyEntry);
    }

    /**
     * Updates the operation value for a history entry in a {@link RestDocument}
     *
     * @param documentId unique document id
     * @param historyId  unique history id to activate
     * @return operation new operation text
     * @throws ResultException unable to change history
     */
    public RestDocument updateHistoryOperation(@NotNull String documentId, int historyId, @NotNull String operation) throws ResultException {
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setId(historyId);
        historyEntry.setOperation(StringUtils.isEmpty(operation) ? "" : operation);
        return setDocumentHistoryElement(documentId, historyEntry);
    }

    /**
     * Change the {@link HistoryEntry} of a specific {@link HistoryEntry} in a {@link RestDocument}, by given documentId.
     *
     * @param documentId   The document id of the {@link RestDocument} from whom the history shall be changed.
     * @param historyEntry The {@link HistoryEntry} of the {@link HistoryEntry} to change to.
     * @return The changed {@link HistoryEntry}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    private RestDocument setDocumentHistoryElement(@NotNull String documentId, @NotNull HistoryEntry historyEntry) throws ResultException {
        if (!this.useHistory) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        RestDocument restDocument = this.documentMap.get(documentId);
        int historyId = historyEntry.getId();

        HistoryEntry resultHistoryBean = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.PUT, "documents/" + documentId + "/history/" + historyId, getWebServiceOptions(historyEntry))
                .executeRequest(HistoryEntry.class);

        restDocument = updateDocument(restDocument.getDocumentFile());

        restDocument.replaceHistoryEntry(resultHistoryBean);
        return restDocument;
    }

    /**
     * updates file information for the given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} which shall be updated.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    private RestDocument updateDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String documentId = getContainedDocumentID(documentFileBean);

        DocumentFileBean documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/info", null)
                .executeRequest(DocumentFileBean.class);

        RestDocument restDocument = this.documentMap.get(documentId);
        restDocument.setDocumentFile(documentFile);

        return restDocument;
    }

    /**
     * Gets file information for all documents on the server for current session.
     *
     * @return A {@link List} referencing the found {@link RestDocument}s.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public List<RestDocument> updateDocumentList() throws ResultException {
        DocumentFileBean[] documentFileList = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/list", null)
                .executeRequest(DocumentFileBean[].class);

        if (documentFileList == null) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR));
        }

        this.documentMap.clear();

        for (DocumentFileBean documentFile : documentFileList) {
            String documentId = documentFile.getDocumentId();
            if (!StringUtils.isEmpty(documentId)) {
                createRestDocument(documentFile);
            }
        }

        return getDocumentList();
    }

    /**
     * Gets a copy of all {@link List}&lt;{@link RestDocument}&gt; from the internal document map.
     *
     * @return a {@link List} referencing the {@link RestDocument}s.
     */
    @NotNull
    public List<RestDocument> getDocumentList() {
        return new ArrayList<>(this.documentMap.values());
    }

    /**
     * Creates {@link HttpEntity} with webservice parameters
     *
     * @param parameter The data to build {@link HttpEntity} with
     * @param <T>       The parameter type
     * @return {@link HttpEntity} with webservice parameters
     * @throws ResultException an {@link ResultException}
     */
    @NotNull
    private <T> HttpEntity getWebServiceOptions(@Nullable T parameter) throws ResultException {
        try {
            if (parameter == null) {
                throw new ResultException(Result.build(Error.NO_OPERATION_DATA));
            }

            StringEntity stringEntity = new StringEntity(
                    this.session.getDataFormat() == DataFormat.XML
                            ? SerializeHelper.toXML(parameter, parameter.getClass())
                            : SerializeHelper.toJSON(parameter),
                    Charsets.UTF_8);

            if (this.session.getDataFormat() != null) {
                stringEntity.setContentType(this.session.getDataFormat().getMimeType());
            }
            return stringEntity;

        } catch (IOException | UnsupportedCharsetException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

    /**
     * checks if the creation of a document history is active
     *
     * @return is the document history active?
     */
    public boolean isUseHistory() {
        return this.useHistory;
    }

    /**
     * De/Activates the creation of a document history
     *
     * @param useHistory De/Activates the creation of a document history
     */
    public void setUseHistory(boolean useHistory) {
        this.useHistory = useHistory;
    }

    /**
     * Checks whether the given document id is listed in the document manager.
     *
     * @param documentId The id, that shall be checked.
     * @return True, if the document is contained.
     */
    private boolean containsDocument(@Nullable String documentId) {
        return documentId != null && this.documentMap.containsKey(documentId);
    }

    /**
     * Checks whether the given document is listed in the document manager and returns the document's id.
     *
     * @param document The document, that shall be checked.
     * @return The id of the contained document.
     * @throws ResultException Shall be thrown, if the document id can not be resolved, or the document is not contained.
     */
    @NotNull
    private String getContainedDocumentID(@Nullable DocumentFileBean document) throws ResultException {
        String id = getDocumentID(document);
        if (containsDocument(id)) {
            return id;
        }
        throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
    }

    /**
     * Returns the document's id.
     *
     * @param document The document, that shall be checked.
     * @return The id of the document.
     * @throws ResultException Shall be thrown, if the document id can not be resolved.
     */
    @NotNull
    private String getDocumentID(@Nullable DocumentFileBean document) throws ResultException {
        if (document != null && document.getDocumentId() != null) {
            return document.getDocumentId();
        }
        throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
    }

    /**
     * Executes a webservice operation for a document stored on the server. The document is referenced by it's
     * documentID and the web service operation specified with {@link WebServiceType}}. The body content (payload) is
     * defined with {@link HttpEntity}.
     *
     * @param documentId     The document ID on which the webservice should be executed
     * @param webServiceType Webservice which should be executed
     * @param httpEntity     the entity that should be send with the webservice call (body payload)
     * @return the processed {@link RestDocument}
     * @throws ResultException an {@link ResultException}
     */
    public RestDocument processDocument(@NotNull String documentId, @NotNull WebServiceType webServiceType, @NotNull HttpEntity httpEntity) throws ResultException {
        String urlPath = webServiceType.equals(WebServiceType.URLCONVERTER)
                ? webServiceType.getRestEndpoint()
                : webServiceType.getRestEndpoint().replace(
                WebServiceType.ID_PLACEHOLDER,
                documentId
        );

        DocumentFileBean documentFileBean = HttpRestRequest.createRequest(this.session)
                .buildRequest(HttpMethod.POST, urlPath, httpEntity)
                .executeRequest(DocumentFileBean.class);

        RestDocument restDocument = getDocument(documentFileBean);
        restDocument.setDocumentFile(documentFileBean);

        if (isUseHistory()) {
            fetchHistoryForDocument(restDocument);
        }

        return restDocument;
    }
}
