package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
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
    @NotNull T_REST_DOCUMENT synchronize(@NotNull DocumentFile documentFile) throws ResultException;

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

}
