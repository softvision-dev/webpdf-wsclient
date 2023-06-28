package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFileCompress;
import net.webpdf.wsclient.openapi.DocumentFileFilter;
import net.webpdf.wsclient.openapi.DocumentInfo;
import net.webpdf.wsclient.openapi.DocumentInfoType;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An instance of {@link AbstractDocumentManager} allows to monitor and interact with the {@link RestDocument}s uploaded
 * to a {@link RestSession} of the webPDF server.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type managed by the {@link AbstractDocumentManager}.
 */
public abstract class AbstractDocumentManager<T_REST_DOCUMENT extends RestDocument>
        implements DocumentManager<T_REST_DOCUMENT> {

    private final @NotNull Map<String, T_REST_DOCUMENT> documentMap = new ConcurrentHashMap<>();
    private final @NotNull RestSession<T_REST_DOCUMENT> session;
    private final @NotNull AtomicBoolean documentHistoryActive = new AtomicBoolean(false);

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
    public synchronized @NotNull T_REST_DOCUMENT synchronizeDocument(@NotNull DocumentFile documentFile) throws ResultException {
        if (documentFile.getDocumentId() == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        T_REST_DOCUMENT synchronizedDocument;
        if (containsDocument(documentFile.getDocumentId())) {
            synchronizedDocument = getDocument(documentFile.getDocumentId());
            accessInternalState(synchronizedDocument).setDocumentFile(documentFile);
        } else {
            synchronizedDocument = createDocument(documentFile);
        }

        if (this.isDocumentHistoryActive()) {
            this.synchronizeDocumentHistory(documentFile.getDocumentId());
        }

        return synchronizedDocument;
    }

    /**
     * Synchronizes the {@link RestDocument}s of this {@link DocumentManager} with the actually uploaded documents of
     * the webPDF server or with the given fileList.
     *
     * @param fileList A {@link DocumentFile} list to sync this {@link DocumentManager} with
     * @return A list of the synchronized {@link RestDocument}s.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @Override
    public @NotNull List<T_REST_DOCUMENT> synchronize(@NotNull List<DocumentFile> fileList) throws ResultException {
        for (DocumentFile documentFile : fileList) {
            this.synchronizeDocument(documentFile);
        }

        this.documentMap.entrySet().removeIf(entry -> fileList.stream().noneMatch(remove -> {
            String documentId = remove.getDocumentId();
            return documentId != null && documentId.equals(entry.getValue().getDocumentId());
        }));

        return getDocuments();
    }

    /**
     * Synchronizes the {@link RestDocument}s of this {@link DocumentManager} with the actually uploaded documents of
     * the webPDF server.
     *
     * @return A list of the synchronized {@link RestDocument}s.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @Override
    public synchronized @NotNull List<T_REST_DOCUMENT> synchronize() throws ResultException {
        DocumentFile[] documentFileList = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.GET, "documents/list")
                .executeRequest(DocumentFile[].class);

        if (documentFileList == null) {
            throw new ClientResultException(Error.HTTP_IO_ERROR);
        }

        return this.synchronize(Arrays.asList(documentFileList));
    }

    /**
     * Returns the document ID the given {@link DocumentFile} is known by to this {@link DocumentManager}.
     *
     * @param document The {@link DocumentFile} a document ID shall be found for.
     * @return The document ID mapped to the given {@link DocumentFile}.
     * @throws ResultException Shall be thrown, if requesting the document ID failed.
     */
    @Override
    public @NotNull String getDocumentID(@NotNull DocumentFile document) throws ResultException {
        String documentId;
        if ((documentId = document.getDocumentId()) != null) {
            return documentId;
        }
        throw new ClientResultException(Error.INVALID_DOCUMENT);
    }

    /**
     * Returns the {@link RestDocument} that is known to the {@link DocumentManager} for the given document ID.
     *
     * @param documentId The document ID a {@link RestDocument} shall be found for.
     * @return The {@link RestDocument} mapped to the given document ID.
     * @throws ResultException Shall be thrown, if requesting the document failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT getDocument(@NotNull String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }
        return this.documentMap.get(documentId);
    }

    /**
     * Returns a list of all {@link RestDocument}s known to this {@link DocumentManager}.
     *
     * @return A list of all {@link RestDocument}s known to this {@link DocumentManager}.
     */
    @Override
    public @NotNull List<T_REST_DOCUMENT> getDocuments() {
        return new ArrayList<>(documentMap.values());
    }

    /**
     * Returns {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     *
     * @param documentId The document ID, that shall be checked for existence.
     * @return {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     */
    @Override
    public boolean containsDocument(@NotNull String documentId) {
        return this.documentMap.containsKey(documentId);
    }

    /**
     * Downloads the {@link RestDocument} with the given document ID and writes it to the given {@link OutputStream}.
     *
     * @param documentId   The document ID of the {@link RestDocument} to download.
     * @param outputStream The {@link OutputStream} to write the downloaded {@link RestDocument} to.
     * @throws ResultException Shall be thrown, should the download have failed.
     */
    @Override
    public void downloadDocument(@NotNull String documentId, @NotNull OutputStream outputStream)
            throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        HttpRestRequest.createRequest(getSession())
                .setAcceptHeader(DataFormat.OCTET_STREAM.getMimeType())
                .buildRequest(HttpMethod.GET, "documents/" + documentId)
                .executeRequest(outputStream);
    }

    /**
     * Downloads the {@link RestDocument} and writes it to the given {@link OutputStream}.
     *
     * @param document     The {@link RestDocument} to download.
     * @param outputStream The {@link OutputStream} to write the downloaded {@link RestDocument} to.
     * @throws ResultException Shall be thrown, should the download have failed.
     */
    @Override
    @Deprecated
    public void downloadDocument(@Nullable RestDocument document, @NotNull OutputStream outputStream)
            throws ResultException {
        if (document == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }
        downloadDocument(document.getDocumentId(), outputStream);
    }

    /**
     * Uploads the given {@link File} to the webPDF server, adds it to this {@link DocumentManager} and returns the
     * resulting {@link RestDocument} handle.
     *
     * @param file The {@link File} to upload.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT uploadDocument(@NotNull File file) throws ResultException {
        try (InputStream data = FileUtils.openInputStream(file)) {
            return uploadDocument(data, file.getName());
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT, ex);
        }
    }

    /**
     * Uploads the given {@link InputStream} to the webPDF server as a document resource with the given file name, adds
     * it to this {@link DocumentManager} and returns the resulting {@link RestDocument} handle.
     *
     * @param data     The document {@link InputStream} to upload.
     * @param fileName The name of the uploaded document.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT uploadDocument(@NotNull InputStream data, @NotNull String fileName)
            throws ResultException {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.LEGACY);
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addBinaryBody("filedata", IOUtils.toByteArray(data),
                    ContentType.DEFAULT_BINARY, fileName);
            HttpEntity entity = builder.build();

            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("history", Boolean.toString(documentHistoryActive.get())));

            URI uri = getSession().getURI("documents", parameters);
            DocumentFile documentFile = HttpRestRequest.createRequest(getSession())
                    .buildRequest(HttpMethod.POST, uri, entity)
                    .executeRequest(DocumentFile.class);
            if (documentFile == null) {
                throw new ClientResultException(Error.INVALID_DOCUMENT);
            }
            return synchronizeDocument(documentFile);
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_SOURCE_DOCUMENT, ex);
        }
    }

    /**
     * Deletes the {@link RestDocument} with the given document ID from the webPDF server.
     *
     * @param documentId The document ID of the {@link RestDocument} to delete.
     * @throws ResultException Shall be thrown, should deleting the document have failed.
     */
    @Override
    public void deleteDocument(@NotNull String documentId) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.DELETE, "documents/" + documentId)
                .executeRequest(Object.class);

        this.documentMap.remove(documentId);
    }

    /**
     * Rename the {@link RestDocument} with the given document ID.
     *
     * @param documentId The document ID of the {@link RestDocument} to rename.
     * @param fileName   The new name for the {@link RestDocument}.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should renaming the document have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT renameDocument(@NotNull String documentId, @NotNull String fileName)
            throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        T_REST_DOCUMENT restDocument = getDocument(documentId);
        DocumentFile documentFile = restDocument.getDocumentFile();
        documentFile.setFileName(fileName);

        documentFile = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.POST, "documents/" + documentId + "/update",
                        prepareHttpEntity(documentFile))
                .executeRequest(DocumentFile.class);

        if (documentFile == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        return synchronizeDocument(documentFile);
    }

    /**
     * Checks whether a document history is collected for managed {@link RestDocument}s.
     *
     * @return {@code true} should collecting the document history be active.
     */
    @Override
    public boolean isDocumentHistoryActive() {
        return documentHistoryActive.get();
    }

    /**
     * Sets whether a document history shall be collected for managed {@link RestDocument}s.
     *
     * @param documentHistoryActive {@code true} should collecting the document history be activated.
     */
    @Override
    public synchronized void setDocumentHistoryActive(boolean documentHistoryActive) throws ResultException {
        this.documentHistoryActive.set(documentHistoryActive);

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
    public @NotNull List<HistoryEntry> getDocumentHistory(@NotNull String documentId) throws ResultException {
        if (!this.isDocumentHistoryActive()) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
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
    public @NotNull HistoryEntry getDocumentHistoryEntry(@NotNull String documentId, int historyId)
            throws ResultException {
        if (!this.isDocumentHistoryActive()) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
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
    public synchronized @Nullable HistoryEntry updateDocumentHistory(
            @NotNull String documentId, @NotNull HistoryEntry historyEntry
    ) throws ResultException {
        if (!this.isDocumentHistoryActive()) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
        }

        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        T_REST_DOCUMENT restDocument = this.documentMap.get(documentId);
        int historyId = historyEntry.getId();

        HistoryEntry resultHistoryBean = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.PUT, "documents/" + documentId + "/history/" + historyId,
                        prepareHttpEntity(historyEntry))
                .executeRequest(HistoryEntry.class);

        restDocument = synchronizeDocumentInfo(restDocument.getDocumentFile());

        if (resultHistoryBean != null) {
            accessInternalState(restDocument).updateHistoryEntry(resultHistoryBean);
        }

        return resultHistoryBean;
    }

    /**
     * Creates a new {@link RestDocument} for the given document ID.
     *
     * @param documentId The document ID a matching {@link RestDocument} shall be created for.
     * @return The created {@link RestDocument}.
     * @throws ResultException Shall be thrown, should creating the document fail.
     */
    protected abstract @NotNull T_REST_DOCUMENT createDocument(@NotNull String documentId) throws ResultException;

    /**
     * Creates a new {@link RestDocument} for the given {@link DocumentFile}.
     *
     * @param documentFile The {@link DocumentFile} a matching {@link RestDocument} shall be created for.
     * @return The created {@link RestDocument}.
     * @throws ResultException Shall be thrown, should creating the document fail.
     */
    private @NotNull T_REST_DOCUMENT createDocument(@NotNull DocumentFile documentFile)
            throws ResultException {
        String documentId;
        if ((documentId = documentFile.getDocumentId()) == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        T_REST_DOCUMENT restDocument = createDocument(documentId);
        accessInternalState(restDocument).setDocumentFile(documentFile);
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
    private synchronized @NotNull T_REST_DOCUMENT synchronizeDocumentInfo(@NotNull DocumentFile DocumentFile)
            throws ResultException {
        String documentId = getDocumentID(DocumentFile);

        DocumentFile documentFile = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/info")
                .executeRequest(DocumentFile.class);

        if (documentFile == null || (documentId = documentFile.getDocumentId()) == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        T_REST_DOCUMENT restDocument = documentMap.get(documentId);
        accessInternalState(restDocument).setDocumentFile(documentFile);

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
    private synchronized void synchronizeDocumentHistory(@NotNull String documentId) throws ResultException {
        T_REST_DOCUMENT restDocument = getDocument(documentId);

        HistoryEntry[] history = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/history")
                .executeRequest(HistoryEntry[].class);

        if (history != null) {
            for (HistoryEntry historyEntry : history) {
                accessInternalState(restDocument).updateHistoryEntry(historyEntry);
            }
        }
    }

    /**
     * Updates the security information of a selected document in the server´s document storage.
     *
     * @param documentId   The unique documentId of the document in the server´s document storage.
     * @param passwordType The security information to update the document with
     * @return The updated {@link RestDocument}.
     * @throws ResultException Shall be thrown, should updating the document security have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT updateDocumentSecurity(
            @NotNull String documentId, @NotNull PdfPasswordType passwordType
    ) throws ResultException {
        DocumentFile documentFile = HttpRestRequest.createRequest(getSession())
                .buildRequest(
                        HttpMethod.PUT, "documents/" + documentId + "/security/password", prepareHttpEntity(passwordType)
                )
                .executeRequest(DocumentFile.class);

        if (documentFile == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        return this.synchronizeDocument(documentFile);
    }

    /**
     * Prepares a {@link HttpEntity} for internal requests to the webPDF server.
     *
     * @param parameter The parameters, that shall be used for the request.
     * @param <T>       The parameter type (data transfer object/bean) that shall be used.
     * @return The resulting state of the data transfer object.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private <T> @NotNull HttpEntity prepareHttpEntity(@NotNull T parameter) throws ResultException {
        try {
            return new StringEntity(SerializeHelper.toJSON(parameter),
                    ContentType.create(DataFormat.JSON.getMimeType(), StandardCharsets.UTF_8));
        } catch (UnsupportedCharsetException ex) {
            throw new ClientResultException(Error.XML_OR_JSON_CONVERSION_FAILURE, ex);
        }
    }

    /**
     * Requests access to the internal {@link RestDocumentState}.
     *
     * @param document The {@link RestDocument} to request access for.
     * @return The internal {@link RestDocumentState}.
     */
    protected abstract @NotNull RestDocumentState<T_REST_DOCUMENT> accessInternalState(@NotNull T_REST_DOCUMENT document);

    /**
     * Returns information about the document selected by documentId in the document storage.
     *
     * @param documentId The unique documentId of the document in the server´s document storage.
     * @param infoType   Detailed information for the document referenced by the unique documentId
     *                   in the server´s document storage.
     * @return The requested document {@link DocumentInfo}
     * @throws ResultException Shall be thrown, should fetching the document info has failed.
     */
    @Override
    public @NotNull DocumentInfo getDocumentInfo(
            @NotNull String documentId, @NotNull DocumentInfoType infoType
    ) throws ResultException {
        DocumentInfo information = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.GET, "documents/" + documentId + "/info/" + infoType.getValue())
                .executeRequest(DocumentInfo.class);

        if (information == null) {
            throw new ClientResultException(Error.HTTP_EMPTY_ENTITY);
        }

        return information;
    }

    /**
     * <p>
     * Extracts the {@link RestDocument} with the given document ID in the document storage.
     * <ul>
     * <li>The document referenced by documentId must be a valid archive. If not, the operation will be aborted.</li>
     * <li>For each file in the archive, a new DocumentFile is created in the document storage with a new documentId.</li>
     * <li>Each newly created DocumentFile holds as parentDocumentId the documentId of the archive.</li>
     * </ul>
     * </p>
     *
     * @param documentId The document ID of the {@link RestDocument} to extract.
     * @param fileFilter An optional {@link DocumentFileFilter} with a list of "include" and "exclude" filter rules. First,
     *                   the "include rules" are applied. If a file matches, the "exclude rules" are applied. Only if
     *                   both rules apply, the file will be passed through the filter.
     * @return A list of the extracted {@link RestDocument}s.
     * @throws ResultException Shall be thrown, should the extraction has failed.
     */
    @Override
    public @NotNull List<T_REST_DOCUMENT> extractDocument(
            @NotNull String documentId, @NotNull DocumentFileFilter fileFilter
    ) throws ResultException {
        if (!containsDocument(documentId)) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        DocumentFile[] documentFileList = HttpRestRequest.createRequest(getSession())
                .buildRequest(
                        HttpMethod.POST, "documents/" + documentId + "/extract", prepareHttpEntity(fileFilter)
                )
                .executeRequest(DocumentFile[].class);

        if (documentFileList == null) {
            throw new ClientResultException(Error.HTTP_IO_ERROR);
        }

        List<T_REST_DOCUMENT> resultDocuments = new ArrayList<>();
        for (DocumentFile documentFile : documentFileList) {
            resultDocuments.add(this.synchronizeDocument(documentFile));
        }

        return resultDocuments;
    }

    /**
     * <p>
     * Extracts the {@link RestDocument} with the given document ID in the document storage.
     * <ul>
     * <li>The document referenced by documentId must be a valid archive. If not, the operation will be aborted.</li>
     * <li>For each file in the archive, a new DocumentFile is created in the document storage with a new documentId.</li>
     * <li>Each newly created DocumentFile holds as parentDocumentId the documentId of the archive.</li>
     * </ul>
     * </p>
     *
     * @param documentId The document ID of the {@link RestDocument} to extract.
     * @return A list of the extracted {@link RestDocument}s.
     * @throws ResultException Shall be thrown, should the extraction has failed.
     */
    @Override
    public @NotNull List<T_REST_DOCUMENT> extractDocument(@NotNull String documentId) throws ResultException {
        return this.extractDocument(documentId, new DocumentFileFilter());
    }

    /**
     * <p>
     * Compresses a list of {@link RestDocument}s selected by documentId or file filter into a new archive document
     * in the document storage.
     * <ul>
     * <li>The list of documents that should be in the archive are selected via the documentId or a file filter.</li>
     * <li>The selection specifications can be made individually or together and act additively in the order documentId
     * and then file filter.</li>
     * <li>If the id is invalid for documents selected via documentId or documents are locked, then the call is aborted
     * with an error.</li>
     * <li>The created archive is stored as a new document with a new documentId in the document storage.</li>
     * </ul>
     * </p>
     *
     * @param documentIdList  The list of documentIds to be added to the archive document.
     * @param archiveFileName the file name for the archive document.
     * @param fileFilter      Defines a {@link DocumentFileFilter} with a list of "include" and "exclude" filter
     *                        rules. First, the "include rules" are applied. If a file matches, the "exclude rules"
     *                        are applied. Only if both rules apply, the file will be passed through the filter.
     * @return The compressed {@link RestDocument}.
     * @throws ResultException Shall be thrown, should the compression have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT compressDocuments(
            @NotNull List<String> documentIdList, @NotNull String archiveFileName,
            @NotNull DocumentFileFilter fileFilter
    ) throws ResultException {
        for (String documentId : documentIdList) {
            if (!this.containsDocument(documentId)) {
                throw new ClientResultException(Error.INVALID_DOCUMENT);
            }
        }

        DocumentFileCompress parameter = new DocumentFileCompress();
        parameter.setDocumentIdList(documentIdList);
        parameter.setArchiveFileName(archiveFileName);
        parameter.setFileFilter(fileFilter);

        DocumentFile documentFile = HttpRestRequest.createRequest(getSession())
                .buildRequest(HttpMethod.POST, "documents/compress", prepareHttpEntity(parameter))
                .executeRequest(DocumentFile.class);

        if (documentFile == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        return this.synchronizeDocument(documentFile);
    }

    /**
     * <p>
     * Compresses a list of {@link RestDocument}s selected by documentId or file filter into a new archive document
     * in the document storage.
     * <ul>
     * <li>The list of documents that should be in the archive are selected via the documentId or a file filter.</li>
     * <li>The selection specifications can be made individually or together and act additively in the order documentId
     * and then file filter.</li>
     * <li>If the id is invalid for documents selected via documentId or documents are locked, then the call is aborted
     * with an error.</li>
     * <li>The created archive is stored as a new document with a new documentId in the document storage.</li>
     * </ul>
     * </p>
     *
     * @param documentIdList  The list of documentIds to be added to the archive document.
     * @param archiveFileName the file name for the archive document.
     * @return The compressed {@link RestDocument}.
     * @throws ResultException Shall be thrown, should the compression have failed.
     */
    @Override
    public @NotNull T_REST_DOCUMENT compressDocuments(
            @NotNull List<String> documentIdList, @NotNull String archiveFileName
    ) throws ResultException {
        return this.compressDocuments(documentIdList, archiveFileName, new DocumentFileFilter());
    }
}
