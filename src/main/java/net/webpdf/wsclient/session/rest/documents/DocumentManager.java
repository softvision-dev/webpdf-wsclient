package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFileCompress;
import net.webpdf.wsclient.openapi.DocumentFileExtract;
import net.webpdf.wsclient.openapi.DocumentInfo;
import net.webpdf.wsclient.openapi.DocumentInfoType;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * A class implementing {@link DocumentManager} allows to monitor and interact with the {@link RestDocument}s uploaded
 * to a {@link RestSession} of the webPDF server.
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type managed by the {@link DocumentManager}.
 */
public interface DocumentManager<T_REST_DOCUMENT extends RestDocument> {

    /**
     * Returns the {@link RestSession} the {@link DocumentManager} is managing {@link RestDocument}s for.
     *
     * @return The {@link RestSession} the {@link DocumentManager} is managing {@link RestDocument}s for.
     */
    @NotNull RestSession<T_REST_DOCUMENT> getSession();

    /**
     * Synchronizes the given {@link DocumentFile} with the matching {@link RestDocument} managed by this
     * {@link DocumentManager}.
     *
     * @return The synchronized {@link RestDocument}.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @NotNull T_REST_DOCUMENT synchronizeDocument(@NotNull DocumentFile documentFile) throws ResultException;

    /**
     * Synchronizes the {@link RestDocument}s of this {@link DocumentManager} with the actually uploaded documents of
     * the webPDF server or with the given fileList.
     *
     * @param fileList A {@link DocumentFile} list to sync this {@link DocumentManager} with
     * @return A list of the synchronized {@link RestDocument}s.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @SuppressWarnings("unused")
    @NotNull List<T_REST_DOCUMENT> synchronize(@NotNull List<DocumentFile> fileList) throws ResultException;

    /**
     * Synchronizes the {@link RestDocument}s of this {@link DocumentManager} with the actually uploaded documents of
     * the webPDF server.
     *
     * @return A list of the synchronized {@link RestDocument}s.
     * @throws ResultException Shall be thrown upon a synchronization failure.
     */
    @SuppressWarnings("unused")
    @NotNull List<T_REST_DOCUMENT> synchronize() throws ResultException;

    /**
     * Returns the document ID the given {@link DocumentFile} is known by to this {@link DocumentManager}.
     *
     * @param document The {@link DocumentFile} a document ID shall be found for.
     * @return The document ID mapped to the given {@link DocumentFile}.
     * @throws ResultException Shall be thrown, if requesting the document ID failed.
     */
    @NotNull String getDocumentID(@NotNull DocumentFile document) throws ResultException;

    /**
     * Returns the {@link RestDocument} that is known to the {@link DocumentManager} for the given document ID.
     *
     * @param documentId The document ID a {@link RestDocument} shall be found for.
     * @return The {@link RestDocument} mapped to the given document ID.
     * @throws ResultException Shall be thrown, if requesting the document failed.
     */
    @NotNull T_REST_DOCUMENT getDocument(@NotNull String documentId) throws ResultException;

    /**
     * Returns a list of all {@link RestDocument}s known to this {@link DocumentManager}.
     *
     * @return A list of all {@link RestDocument}s known to this {@link DocumentManager}.
     */
    @NotNull List<T_REST_DOCUMENT> getDocuments();

    /**
     * Returns {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     *
     * @param documentId The document ID, that shall be checked for existence.
     * @return {@code true}, if this {@link DocumentManager} contains a {@link RestDocument} with the given ID.
     */
    boolean containsDocument(@NotNull String documentId);

    /**
     * Downloads the {@link RestDocument} with the given document ID and writes it to the given {@link OutputStream}.
     *
     * @param documentId   The document ID of the {@link RestDocument} to download.
     * @param outputStream The {@link OutputStream} to write the downloaded {@link RestDocument} to.
     * @throws ResultException Shall be thrown, should the download have failed.
     */
    void downloadDocument(@NotNull String documentId, @NotNull OutputStream outputStream) throws ResultException;

    /**
     * Downloads the {@link RestDocument} and writes it to the given {@link OutputStream}.
     *
     * @param document     The {@link RestDocument} to download.
     * @param outputStream The {@link OutputStream} to write the downloaded {@link RestDocument} to.
     * @throws ResultException Shall be thrown, should the download have failed.
     */
    @Deprecated
    void downloadDocument(@Nullable RestDocument document, @NotNull OutputStream outputStream) throws ResultException;

    /**
     * Uploads the given {@link File} to the webPDF server, adds it to this {@link DocumentManager} and returns the
     * resulting {@link RestDocument} handle.
     *
     * @param file The {@link File} to upload.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @NotNull T_REST_DOCUMENT uploadDocument(@NotNull File file) throws ResultException;

    /**
     * Uploads the given {@link InputStream} to the webPDF server as a document resource with the given file name, adds
     * it to this {@link DocumentManager} and returns the resulting {@link RestDocument} handle.
     *
     * @param data     The document {@link InputStream} to upload.
     * @param fileName The name of the uploaded document.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @NotNull T_REST_DOCUMENT uploadDocument(@NotNull InputStream data, @NotNull String fileName) throws ResultException;

    /**
     * Deletes the {@link RestDocument} with the given document ID from the webPDF server.
     *
     * @param documentId The document ID of the {@link RestDocument} to delete.
     * @throws ResultException Shall be thrown, should deleting the document have failed.
     */
    void deleteDocument(@NotNull String documentId) throws ResultException;

    /**
     * Rename the {@link RestDocument} with the given document ID.
     *
     * @param documentId The document ID of the {@link RestDocument} to rename.
     * @param fileName   The new name for the {@link RestDocument}.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should renaming the document have failed.
     */
    @NotNull T_REST_DOCUMENT renameDocument(@NotNull String documentId, @NotNull String fileName) throws ResultException;

    /**
     * Checks whether a document history is collected for managed {@link RestDocument}s.
     *
     * @return {@code true} should collecting the document history be active.
     */
    boolean isDocumentHistoryActive();

    /**
     * Sets whether a document history shall be collected for managed {@link RestDocument}s.
     *
     * @param documentHistoryActive {@code true} should collecting the document history be activated.
     */
    void setDocumentHistoryActive(boolean documentHistoryActive) throws ResultException;

    /**
     * Returns the {@link HistoryEntry}s known for the {@link RestDocument} with the given document ID.
     *
     * @param documentId The document ID of the {@link RestDocument} the history shall be requested for.
     * @return The {@link HistoryEntry}s known for the selected {@link RestDocument}.
     * @throws ResultException Shall be thrown, should requesting the document history have failed.
     */
    @NotNull List<HistoryEntry> getDocumentHistory(@NotNull String documentId) throws ResultException;

    /**
     * Returns the {@link HistoryEntry} with the given history ID for the {@link RestDocument} with the given document
     * ID.
     *
     * @param documentId The document ID of the {@link RestDocument} the {@link HistoryEntry} shall be requested for.
     * @param historyId  The history ID of the {@link HistoryEntry}, that shall be requested.
     * @return The selected {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, should requesting the document history have failed.
     */
    @SuppressWarnings("unused")
    @NotNull HistoryEntry getDocumentHistoryEntry(@NotNull String documentId, int historyId) throws ResultException;

    /**
     * Updates the history of the {@link RestDocument} with the given document ID using the given {@link HistoryEntry}.
     *
     * @param documentId   The document ID of the {@link RestDocument} to update.
     * @param historyEntry The {@link HistoryEntry} to update the contained values for.
     * @return The updated {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, should updating the document history have failed.
     */
    @Nullable HistoryEntry updateDocumentHistory(@NotNull String documentId, @NotNull HistoryEntry historyEntry)
            throws ResultException;

    /**
     * Updates the security information of a selected document in the server´s document storage.
     *
     * @param documentId   The unique documentId of the document in the server´s document storage.
     * @param passwordType The security information to update the document with
     * @return The updated {@link RestDocument}.
     * @throws ResultException Shall be thrown, should updating the document security have failed.
     */
    @NotNull T_REST_DOCUMENT updateDocumentSecurity(
            @NotNull String documentId, @NotNull PdfPasswordType passwordType
    ) throws ResultException;

    /**
     * Returns information about the document selected by documentId in the document storage.
     *
     * @param documentId The unique documentId of the document in the server´s document storage.
     * @param infoType   Detailed information for the document referenced by the unique documentId
     *                   in the server´s document storage.
     * @return The requested document {@link DocumentInfo}
     * @throws ResultException Shall be thrown, should fetching the document info have failed.
     */
    @NotNull DocumentInfo getDocumentInfo(
            @NotNull String documentId, @NotNull DocumentInfoType infoType
    ) throws ResultException;

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
     * @param documentId  The document ID of the {@link RestDocument} to extract.
     * @param fileExtract {@link DocumentFileExtract} settings for unpacking the archive document.
     * @return A list of the extracted {@link RestDocument}s.
     * @throws ResultException Shall be thrown, should the extraction has failed.
     */
    @NotNull List<T_REST_DOCUMENT> extractDocument(
            @NotNull String documentId, @NotNull DocumentFileExtract fileExtract
    ) throws ResultException;

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
     * @param fileCompress The {@link DocumentFileCompress} settings for creating the archive document and for
     *                     selecting and filtering the documents to be added to the archive.
     * @return The compressed {@link RestDocument}.
     * @throws ResultException Shall be thrown, should the compression have failed.
     */
    @NotNull T_REST_DOCUMENT compressDocuments(@NotNull DocumentFileCompress fileCompress) throws ResultException;
}
