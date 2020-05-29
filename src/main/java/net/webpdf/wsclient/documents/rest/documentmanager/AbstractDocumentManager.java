package net.webpdf.wsclient.documents.rest.documentmanager;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntryBean;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.codec.Charsets;
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
import java.util.*;

public abstract class AbstractDocumentManager<T_REST_DOCUMENT extends RestDocument>
        implements DocumentManager<T_REST_DOCUMENT> {

    @NotNull
    private final Map<String, T_REST_DOCUMENT> documentMap = new HashMap<>();
    @NotNull
    private final RestSession<T_REST_DOCUMENT> session;
    private boolean activeDocumentHistory = false;

    /**
     * Initializes a document manager for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a document manager shall be created for.
     */
    public AbstractDocumentManager(@NotNull RestSession<T_REST_DOCUMENT> session) {
        this.session = session;
    }

    /**
     * Downloads {@link RestDocument} to target path
     *
     * @param document     {@link RestDocument} instance
     * @param outputStream {@link OutputStream} for downloaded content
     * @throws ResultException a {@link ResultException}
     */
    public void downloadDocument(@Nullable T_REST_DOCUMENT document, @Nullable OutputStream outputStream)
            throws ResultException {
        if (document == null || outputStream == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        HttpRestRequest.createRequest(this.session)
                .setAcceptHeader("application/octet-stream")
                .buildRequest(HttpMethod.GET, "documents/" + document.getSourceDocumentId(), null)
                .executeRequest(outputStream);
    }

    /**
     * Uploads the given {@link DocumentFileBean} to the webPDF server and returns the {@link RestDocument} reference
     * to the uploaded resource.
     *
     * @param file The file, that shall be uploaded for further processing.
     * @return A {@link RestDocument} referencing the uploaded document.
     * @throws IOException an {@link IOException}
     */
    @NotNull
    public T_REST_DOCUMENT uploadDocument(@Nullable File file) throws IOException {
        if (file == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(StandardCharsets.UTF_8);
        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
        HttpEntity entity = builder.build();

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("history", Boolean.toString(activeDocumentHistory)));

        URI uri = this.session.getURI("documents", parameters);
        T_REST_DOCUMENT restDocument = getDocument(HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.POST, uri, entity)
                .executeRequest(DocumentFileBean.class));

        if (activeDocumentHistory) {
            updateDocumentHistory(restDocument.getDocumentFile());
        }

        return restDocument;
    }

    /**
     * returns the {@link RestDocument} from the internal document map, by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be returned.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public T_REST_DOCUMENT getDocument(@Nullable String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }

        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setDocumentId(documentId);

        return this.getDocument(documentFileBean);
    }

    /**
     * returns the {@link RestDocument} from the internal document map, by given {@link DocumentFileBean} or if the
     * Document doesn't exist create a new entry from the given {@link DocumentFileBean}
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be returned or added for further processing.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public T_REST_DOCUMENT getDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String documentId = getDocumentID(documentFileBean);
        if (containsDocument(documentId)) {
            return documentMap.get(documentId);
        }

        T_REST_DOCUMENT restDocument = createDocument(documentId);
        restDocument.setDocumentFile(documentFileBean);

        documentMap.put(documentId, restDocument);

        return restDocument;
    }

    /**
     * Deletes the given {@link RestDocument} from the webPDF server by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be returned.
     * @throws ResultException a {@link ResultException}
     */
    public void deleteDocument(@Nullable String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }

        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setDocumentId(documentId);
        this.deleteDocument(documentFileBean);
    }

    /**
     * Deletes the given {@link RestDocument} from the webPDF server by given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be deleted.
     * @throws ResultException a {@link ResultException}
     */
    public void deleteDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String id = getContainedDocumentID(documentFileBean);

        this.documentMap.remove(id);

        HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.DELETE, "documents/" + id, null)
                .executeRequest(Object.class);
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
    public T_REST_DOCUMENT renameDocument(@Nullable String documentId, @Nullable String fileName) throws IOException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setDocumentId(documentId);
        return this.renameDocument(documentFileBean, fileName);
    }

    /**
     * Renames the {@link RestDocument} on the webPDF server, by given {@link DocumentFileBean}, to the given filename.
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be renamed.
     * @param fileName         The new file name
     * @return A {@link RestDocument} referencing the renamed resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public T_REST_DOCUMENT renameDocument(@Nullable DocumentFileBean documentFileBean, @Nullable String fileName)
            throws IOException {
        String id = getContainedDocumentID(documentFileBean);

        Objects.requireNonNull(documentFileBean).setFileName(fileName);

        DocumentFileBean documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.POST, "documents/" + id + "/update", this.getWebServiceOptions(documentFileBean))
                .executeRequest(DocumentFileBean.class);

        T_REST_DOCUMENT restDocument = documentMap.get(id);
        restDocument.setDocumentFile(documentFile);

        return restDocument;
    }

    /**
     * updates file history information from the server for the given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be updated.
     * @throws ResultException a {@link ResultException}
     */
    public void updateDocumentHistory(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String id = getContainedDocumentID(documentFileBean);

        T_REST_DOCUMENT restDocument = documentMap.get(id);

        HistoryEntryBean[] history = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/" + id + "/history", null)
                .executeRequest(HistoryEntryBean[].class);

        if (history == null) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR));
        }

        for (HistoryEntryBean historyBean : history) {
            restDocument.getHistoryElement(historyBean);
        }
    }

    /**
     * returns the {@link List}&lt;{@link HistoryEntryBean}&gt; from the internal document history map, by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} from whom the history shall be returned.
     * @return A {@link List}&lt;{@link HistoryEntryBean}&gt; referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public List<HistoryEntryBean> getDocumentHistory(@Nullable String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setDocumentId(documentId);

        return this.getDocumentHistory(documentFileBean);
    }

    /**
     * returns the {@link List HistoryElement}&lt;{@link HistoryEntryBean}&gt; from the internal document history map,
     * by given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be returned.
     * @return A {@link List}&lt;{@link HistoryEntryBean}&gt; referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public List<HistoryEntryBean> getDocumentHistory(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String id = getContainedDocumentID(documentFileBean);

        T_REST_DOCUMENT restDocument = documentMap.get(id);
        return restDocument.getHistory();
    }

    /**
     * Change the {@link HistoryEntryBean} of a specific {@link HistoryEntryBean} in a {@link RestDocument}, by given
     * documentId.
     *
     * @param documentId  The document id of the {@link RestDocument} from whom the history shall be changed.
     * @param historyBean The {@link HistoryEntryBean} of the {@link HistoryEntryBean} to change to.
     * @return The changed {@link HistoryEntryBean}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public HistoryEntryBean setDocumentHistoryElement(@Nullable String documentId, HistoryEntryBean historyBean)
            throws ResultException {
        DocumentFileBean documentFileBean = new DocumentFileBean();
        documentFileBean.setDocumentId(documentId);

        return this.setDocumentHistoryElement(documentFileBean, historyBean);
    }

    /**
     * Change the {@link HistoryEntryBean} of a specific {@link HistoryEntryBean} in a {@link RestDocument}, by given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be changed.
     * @param historyBean      The {@link HistoryEntryBean} of the {@link HistoryEntryBean} to change to.
     * @return The changed {@link HistoryEntryBean}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public HistoryEntryBean setDocumentHistoryElement(
            @Nullable DocumentFileBean documentFileBean, @Nullable HistoryEntryBean historyBean
    ) throws ResultException {
        if (historyBean == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        String documentId = getContainedDocumentID(documentFileBean);

        T_REST_DOCUMENT restDocument = documentMap.get(documentId);
        int historyId = historyBean.getId();

        HistoryEntryBean resultHistoryBean = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.PUT, "documents/" + documentId + "/history/" + historyId,
                        getWebServiceOptions(historyBean))
                .executeRequest(HistoryEntryBean.class);

        if (historyBean.isActive()) {
            restDocument = updateDocument(restDocument.getDocumentFile());
            getDocumentHistory(documentId);
        }

        return restDocument.getHistoryElement(resultHistoryBean);
    }

    /**
     * updates file information for the given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} which shall be updated.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    private T_REST_DOCUMENT updateDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException {
        String documentId = getContainedDocumentID(documentFileBean);

        DocumentFileBean documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/info", null)
                .executeRequest(DocumentFileBean.class);

        T_REST_DOCUMENT restDocument = documentMap.get(documentId);
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
    public List<T_REST_DOCUMENT> updateDocumentList() throws ResultException {
        DocumentFileBean[] documentFileList = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/list", null)
                .executeRequest(DocumentFileBean[].class);

        if (documentFileList == null) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR));
        }

        this.documentMap.clear();

        for (DocumentFileBean documentFile : documentFileList) {
            String documentId = documentFile.getDocumentId();
            T_REST_DOCUMENT restDocument = createDocument(documentId);
            restDocument.setDocumentFile(documentFile);
            this.documentMap.put(documentId, restDocument);
        }

        return getDocumentList();
    }

    /**
     * Gets all {@link RestDocument} from the internal document map.
     *
     * @return A {@link List} referencing the found {@link RestDocument}s.
     */
    @NotNull
    public List<T_REST_DOCUMENT> getDocumentList() {
        return new ArrayList<>(documentMap.values());
    }

    /**
     * Creates {@link HttpEntity} with webservice parameters
     *
     * @param parameter The data to build {@link HttpEntity} with
     * @param <T_PARAMETER>       The parameter type
     * @return {@link HttpEntity} with webservice parameters
     * @throws ResultException an {@link ResultException}
     */
    @NotNull
    private <T_PARAMETER> HttpEntity getWebServiceOptions(@Nullable T_PARAMETER parameter) throws ResultException {
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
    public boolean isActiveDocumentHistory() {
        return activeDocumentHistory;
    }

    /**
     * De/Activates the creation of a document history
     *
     * @param activeDocumentHistory De/Activates the creation of a document history
     */
    public void setActiveDocumentHistory(boolean activeDocumentHistory) {
        this.activeDocumentHistory = activeDocumentHistory;
    }

    /**
     * Checks whether the given document id is listed in the document manager.
     *
     * @param documentId The id, that shall be checked.
     * @return True, if the document is contained.
     */
    public boolean containsDocument(@Nullable String documentId) {
        return documentId != null && this.documentMap.containsKey(documentId);
    }

    /**
     * Checks whether the given document is listed in the document manager.
     *
     * @param document The document, that shall be checked.
     * @return True, if the document is contained.
     */
    @SuppressWarnings("unused")
    public boolean containsDocument(@Nullable DocumentFileBean document) {
        return document != null && document.getDocumentId() != null && containsDocument(document.getDocumentId());
    }

    /**
     * Checks whether the given document is listed in the document manager and returns the document's id.
     *
     * @param document The document, that shall be checked.
     * @return The id of the contained document.
     * @throws ResultException Shall be thrown, if the document id can not be resolved, or the document is not contained.
     */
    @NotNull
    public String getContainedDocumentID(@Nullable DocumentFileBean document) throws ResultException {
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
    public String getDocumentID(@Nullable DocumentFileBean document) throws ResultException {
        if (document != null && document.getDocumentId() != null) {
            return document.getDocumentId();
        }
        throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
    }

    /**
     * Returns the current {@link RestSession} of this document manager.
     *
     * @return The current {@link RestSession} of this document manager.
     */
    @NotNull
    public RestSession<T_REST_DOCUMENT> getSession() {
        return this.session;
    }

    /**
     * Creates a new {@link RestDocument} matching the given generic type.
     *
     * @param id The document's id.
     * @return The created {@link RestDocument}.
     */
    @NotNull
    protected abstract T_REST_DOCUMENT createDocument(@Nullable String id);
}
