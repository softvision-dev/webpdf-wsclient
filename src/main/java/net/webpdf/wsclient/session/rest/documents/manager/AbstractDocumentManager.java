package net.webpdf.wsclient.session.rest.documents.manager;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

/**
 * An instance of {@link AbstractDocumentManager} allows to monitor and interact with the {@link RestDocument}s uploaded
 * to a {@link RestSession} of the webPDF server.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type managed by the {@link AbstractDocumentManager}.
 */
public abstract class AbstractDocumentManager<T_REST_DOCUMENT extends RestDocument>
        implements DocumentManager<T_REST_DOCUMENT> {

    private final @NotNull Map<String, T_REST_DOCUMENT> documentMap = new HashMap<>();
    private final @NotNull RestSession<T_REST_DOCUMENT> session;
    private boolean documentHistoryActive = false;

    /**
     * Initializes a {@link DocumentManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link DocumentManager} shall be created for.
     */
    public AbstractDocumentManager(@NotNull RestSession<T_REST_DOCUMENT> session) {
        this.session = session;
    }

    /**
     * Returns the {@link RestSession} the {@link DocumentManager} is managing {@link RestDocument}s for.
     *
     * @return The {@link RestSession} the {@link DocumentManager} is managing {@link RestDocument}s for.
     */
    @Override
    public @NotNull RestSession<T_REST_DOCUMENT> getSession() {
        return this.session;
    }

    /**
     * Synchronizes the given {@link DocumentFile} with the matching {@link RestDocument} managed by this
     * {@link DocumentManager}.
     *
     * @return The synchronized {@link RestDocument}.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @Override
    public @NotNull T_REST_DOCUMENT synchronize(@NotNull DocumentFile documentFile) throws ResultException {
        if (containsDocument(documentFile.getDocumentId())) {
            synchronizeDocumentInfo(documentFile);
            return getDocument(documentFile.getDocumentId());
        }

        return createDocument(documentFile);
    }

    /**
     * Synchronizes the {@link RestDocument}s of this {@link DocumentManager} with the actually uploaded documents of
     * the webPDF server.
     *
     * @return A list of the synchronized {@link RestDocument}s.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @Override
    public @NotNull List<T_REST_DOCUMENT> synchronize() throws ResultException {
        DocumentFile[] documentFileList = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/list", null)
                .executeRequest(DocumentFile[].class);

        if (documentFileList == null) {
            throw new ResultException(Result.build(Error.HTTP_IO_ERROR));
        }

        this.documentMap.clear();
        for (DocumentFile documentFile : documentFileList) {
            synchronize(documentFile);
        }

        return getDocuments();
    }

    /**
     * Returns the document ID the given {@link DocumentFile} is known by to this {@link DocumentManager}.
     *
     * @param document The {@link DocumentFile} a document ID shall be found for.
     * @return The document ID mapped to the given {@link DocumentFile}.
     * @throws ResultException Shall be thrown, if requesting the document ID failed.
     */
    @Override
    public @NotNull String getDocumentID(@Nullable DocumentFile document) throws ResultException {
        String documentId;
        if (document != null && (documentId = document.getDocumentId()) != null) {
            return documentId;
        }
        throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
    }

    /**
     * Returns the {@link RestDocument} that is known to the {@link DocumentManager} for the given document ID.
     *
     * @param documentId The document ID a {@link RestDocument} shall be found for.
     * @return The {@link RestDocument} mapped to the given document ID.
     * @throws ResultException Shall be thrown, if requesting the document failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT getDocument(@Nullable String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        return this.documentMap.get(documentId);
    }

    /**
     * Returns a list of all {@link RestDocument}s known to this {@link DocumentManager}.
     *
     * @return A list of all {@link RestDocument}s known to this {@link DocumentManager}.
     */
    public @NotNull List<T_REST_DOCUMENT> getDocuments() {
        return new ArrayList<>(documentMap.values());
    }

    /**
     * Returns {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     *
     * @param documentId The document ID, that shall be checked for existence.
     * @return {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     */
    public boolean containsDocument(@Nullable String documentId) {
        return documentId != null && this.documentMap.containsKey(documentId);
    }

    /**
     * Downloads the {@link RestDocument} with the given document ID and writes it to the given {@link OutputStream}.
     *
     * @param documentId   The document ID of the {@link RestDocument} to download.
     * @param outputStream The {@link OutputStream} to write the downloaded {@link RestDocument} to.
     * @throws ResultException Shall be thrown, should the download have failed.
     */
    public void downloadDocument(@NotNull String documentId, @Nullable OutputStream outputStream)
            throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        HttpRestRequest.createRequest(this.session)
                .setAcceptHeader("application/octet-stream")
                .buildRequest(HttpMethod.GET, "documents/" + documentId, null)
                .executeRequest(outputStream);
    }

    /**
     * Uploads the given {@link File} to the webPDF server, adds it to this {@link DocumentManager} and returns the
     * resulting {@link RestDocument} handle.
     *
     * @param file The {@link File} to upload.
     * @return The resulting {@link RestDocument} handle.
     * @throws IOException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT uploadDocument(@Nullable File file) throws IOException {
        if (file == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        try (InputStream data = FileUtils.openInputStream(file)) {
            return uploadDocument(data, file.getName());
        }
    }

    /**
     * Uploads the given {@link InputStream} to the webPDF server as a document resource with the given file name, adds
     * it to this {@link DocumentManager} and returns the resulting {@link RestDocument} handle.
     *
     * @param data     The document {@link InputStream} to upload.
     * @param fileName The name of the uploaded document.
     * @return The resulting {@link RestDocument} handle.
     * @throws IOException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT uploadDocument(@Nullable InputStream data, @Nullable String fileName)
            throws IOException {
        if (data == null || fileName == null) {
            throw new ResultException(Result.build(Error.INVALID_FILE_SOURCE));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.setCharset(StandardCharsets.UTF_8);
        builder.addBinaryBody("filedata", IOUtils.toByteArray(data),
                ContentType.DEFAULT_BINARY, fileName);
        HttpEntity entity = builder.build();

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("history", Boolean.toString(documentHistoryActive)));

        URI uri = this.session.getURI("documents", parameters);
        DocumentFile documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.POST, uri, entity)
                .executeRequest(DocumentFile.class);
        if (documentFile == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        return synchronize(documentFile);
    }

    /**
     * Deletes the {@link RestDocument} with the given document ID from the webPDF server.
     *
     * @param documentId The document ID of the {@link RestDocument} to delete.
     * @throws ResultException Shall be thrown, should deleting the document have failed.
     */
    @Override
    public void deleteDocument(@Nullable String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.DELETE, "documents/" + documentId, null)
                .executeRequest(Object.class);
        this.documentMap.remove(documentId);
    }

    /**
     * Rename the {@link RestDocument} with the given document ID.
     *
     * @param documentId The document ID of the {@link RestDocument} to rename.
     * @param fileName   The new name for the {@link RestDocument}.
     * @return The resulting {@link RestDocument} handle.
     * @throws IOException Shall be thrown, should renaming the document have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT renameDocument(@Nullable String documentId, @Nullable String fileName)
            throws IOException {
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        T_REST_DOCUMENT restDocument = getDocument(documentId);
        DocumentFile documentFile = restDocument.getDocumentFile();
        if (documentFile == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        documentFile.setFileName(fileName);
        documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.POST, "documents/" + documentId + "/update",
                        prepareHttpEntity(documentFile))
                .executeRequest(DocumentFile.class);
        if (documentFile == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }
        return synchronize(documentFile);
    }

    /**
     * Checks whether a document history is collected for managed {@link RestDocument}s.
     *
     * @return {@code true} should collecting the document history be active.
     */
    @Override
    public boolean isDocumentHistoryActive() {
        return documentHistoryActive;
    }

    /**
     * Sets whether a document history shall be collected for managed {@link RestDocument}s.
     *
     * @param documentHistoryActive {@code true} should collecting the document history be activated.
     */
    @Override
    public void setDocumentHistoryActive(boolean documentHistoryActive) throws ResultException {
        this.documentHistoryActive = documentHistoryActive;
        if (documentHistoryActive) {
            for (T_REST_DOCUMENT document : getDocuments()) {
                synchronizeDocumentHistory(document.getDocumentId());
            }
        }
    }

    /**
     * Returns the {@link HistoryEntry}s known for the {@link RestDocument} with the given document ID.
     *
     * @param documentId The document ID of the {@link RestDocument} the history shall be requested for.
     * @return The {@link HistoryEntry}s known for the selected {@link RestDocument}.
     * @throws ResultException Shall be thrown, should requesting the document history have failed.
     */
    @Override
    public @NotNull List<HistoryEntry> getDocumentHistory(@Nullable String documentId) throws ResultException {
        if (!this.isDocumentHistoryActive()) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        return getDocument(documentId).getHistory();
    }

    /**
     * Returns the {@link HistoryEntry} with the given history ID for the {@link RestDocument} with the given document
     * ID.
     *
     * @param documentId The document ID of the {@link RestDocument} the {@link HistoryEntry} shall be requested for.
     * @param historyId  The history ID of the {@link HistoryEntry}, that shall be requested.
     * @return The selected {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, should requesting the document history have failed.
     */
    @Override
    public @NotNull HistoryEntry getDocumentHistoryEntry(@Nullable String documentId, int historyId)
            throws ResultException {
        if (!this.isDocumentHistoryActive()) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        return getDocument(documentId).getHistoryEntry(historyId);
    }

    /**
     * Updates the history of the {@link RestDocument} with the given document ID using the given {@link HistoryEntry}.
     *
     * @param documentId   The document ID of the {@link RestDocument} to update.
     * @param historyEntry The {@link HistoryEntry} to update the contained values for.
     * @return The updated {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, should updating the document history have failed.
     */
    @Override
    public @Nullable HistoryEntry updateDocumentHistory(@Nullable String documentId, @Nullable HistoryEntry historyEntry)
            throws ResultException {
        if (!this.isDocumentHistoryActive() || historyEntry == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        if (!containsDocument(documentId)) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        T_REST_DOCUMENT restDocument = this.documentMap.get(documentId);
        int historyId = historyEntry.getId();

        HistoryEntry resultHistoryBean = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.PUT, "documents/" + documentId + "/history/" + historyId,
                        prepareHttpEntity(historyEntry))
                .executeRequest(HistoryEntry.class);

        restDocument = synchronizeDocumentInfo(restDocument.getDocumentFile());
        restDocument.updateHistoryEntry(resultHistoryBean);
        return resultHistoryBean;
    }

    /**
     * Creates a new {@link RestDocument} for the given document ID.
     *
     * @param documentId The document ID a matching {@link RestDocument} shall be created for.
     * @return The created {@link RestDocument}.
     * @throws ResultException Shall be thrown, should creating the document fail.
     */
    protected abstract @NotNull T_REST_DOCUMENT createDocument(@Nullable String documentId) throws ResultException;

    /**
     * Creates a new {@link RestDocument} for the given {@link DocumentFile}.
     *
     * @param documentFile The {@link DocumentFile} a matching {@link RestDocument} shall be created for.
     * @return The created {@link RestDocument}.
     * @throws ResultException Shall be thrown, should creating the document fail.
     */
    private @NotNull T_REST_DOCUMENT createDocument(@Nullable DocumentFile documentFile)
            throws ResultException {
        String documentId;
        if (documentFile == null || (documentId = documentFile.getDocumentId()) == null) {
            throw new ResultException(Result.build(Error.INVALID_DOCUMENT));
        }

        T_REST_DOCUMENT restDocument = createDocument(documentId);
        restDocument.setDocumentFile(documentFile);
        documentMap.put(documentId, restDocument);
        synchronizeDocumentInfo(documentFile);

        return restDocument;
    }

    /**
     * Synchronize the state of the given {@link DocumentFile} with the webPDF server.
     *
     * @param DocumentFile The {@link DocumentFile} to synchronize.
     * @return The matching {@link RestDocument}.
     * @throws ResultException Shall be thrown, should the synchronization fail.
     */
    private @NotNull T_REST_DOCUMENT synchronizeDocumentInfo(@Nullable DocumentFile DocumentFile)
            throws ResultException {
        String documentId = getDocumentID(DocumentFile);

        DocumentFile documentFile = HttpRestRequest.createRequest(session)
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/info", null)
                .executeRequest(DocumentFile.class);

        T_REST_DOCUMENT restDocument = documentMap.get(documentId);
        restDocument.setDocumentFile(documentFile);
        if (isDocumentHistoryActive()) {
            synchronizeDocumentHistory(documentId);
        }

        return restDocument;
    }

    /**
     * Synchronizes the document history for the {@link RestDocument} with the given document ID of this
     * {@link DocumentManager} and the webPDF server.
     *
     * @param documentId The document ID of the {@link RestDocument} to synchronize the document history for.
     * @throws ResultException Shall be thrown, should synchronizing the document history have failed.
     */
    private void synchronizeDocumentHistory(@Nullable String documentId) throws ResultException {
        T_REST_DOCUMENT restDocument = getDocument(documentId);
        HistoryEntry[] history = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/history", null)
                .executeRequest(HistoryEntry[].class);

        if (history == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }

        for (HistoryEntry historyEntry : history) {
            restDocument.updateHistoryEntry(historyEntry);
        }
    }

    /**
     * Prepares a {@link HttpEntity} for internal requests to the webPDF server.
     *
     * @param parameter The parameters, that shall be used for the request.
     * @param <T>       The parameter type (data transfer object/bean) that shall be used.
     * @return The resulting state of the data transfer object.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private <T> @NotNull HttpEntity prepareHttpEntity(@Nullable T parameter) throws ResultException {
        try {
            if (parameter == null) {
                throw new ResultException(Result.build(Error.NO_OPERATION_DATA));
            }
            return new StringEntity(
                            this.session.getDataFormat() == DataFormat.XML
                                    ? SerializeHelper.toXML(parameter, parameter.getClass())
                                    : SerializeHelper.toJSON(parameter),
                    this.session.getDataFormat() != null ?
                            ContentType.create(this.session.getDataFormat().getMimeType(), StandardCharsets.UTF_8) :
                            null);
        } catch (IOException | UnsupportedCharsetException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

}
