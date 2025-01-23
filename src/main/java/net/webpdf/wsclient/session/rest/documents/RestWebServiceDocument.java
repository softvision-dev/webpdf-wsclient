package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFileExtract;
import net.webpdf.wsclient.openapi.DocumentInfo;
import net.webpdf.wsclient.openapi.DocumentInfoType;
import net.webpdf.wsclient.openapi.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.documents.AbstractDocument;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * An instance of {@link RestWebServiceDocument} represents a document, that has been uploaded to a webPDF server.
 * </p>
 */
public class RestWebServiceDocument extends AbstractDocument implements RestDocument {
    private final @NotNull RestWebServiceDocumentState documentState;

    /**
     * Creates a {@link RestWebServiceDocument} representing the given {@link RestWebServiceDocumentState} to external
     * actors. The owning {@link DocumentManager} shall have exclusive rights to instantiate such documents.
     *
     * @param documentState The internal {@link RestWebServiceDocumentState}, that shall be updated by a
     *                      {@link DocumentManager} and shall be readable via the methods defined by
     *                      {@link RestDocument}.
     */
    RestWebServiceDocument(@NotNull RestWebServiceDocumentState documentState) {
        super(null);
        this.documentState = documentState;
    }

    /**
     * <p>
     * Returns the internal {@link RestWebServiceDocumentState}, that shall be updated by a {@link DocumentManager}.<br>
     * The {@link DocumentManager} shall have exclusive rights to access the internal state of a document.
     * </p>
     *
     * @return the internal {@link RestWebServiceDocumentState}, that shall be updated by a {@link DocumentManager}.
     */
    @NotNull RestWebServiceDocumentState accessInternalState() {
        return this.documentState;
    }

    /**
     * Returns the document ID of the managed {@link RestDocument}.
     *
     * @return The document ID of the managed {@link RestDocument}.
     */
    @Override
    public @NotNull String getDocumentId() {
        return accessInternalState().getDocumentId();
    }

    /**
     * Returns the {@link DocumentFile} of the managed {@link RestDocument}.
     *
     * @return The {@link DocumentFile} of the managed {@link RestDocument}.
     */
    @Override
    public @NotNull DocumentFile getDocumentFile() {
        return accessInternalState().getDocumentFile();
    }

    /**
     * Returns the {@link HistoryEntry}s of the managed {@link RestDocument}.
     *
     * @return The {@link HistoryEntry}s of the managed {@link RestDocument}.
     */
    @Override
    public @NotNull List<HistoryEntry> getHistory() {
        return accessInternalState().getHistory();
    }

    /**
     * Returns a {@link HistoryEntry} from the internal history map, by given history ID.
     *
     * @param historyId The history ID of the {@link HistoryEntry} that shall be returned.
     * @return A {@link HistoryEntry} representing a historic state of the uploaded resource.
     * @throws ResultException Shall be thrown, should accessing the document history fail.
     */
    @Override
    public @NotNull HistoryEntry getHistoryEntry(int historyId) throws ResultException {
        return accessInternalState().getHistoryEntry(historyId);
    }

    /**
     * This is a shortcut for {@link DocumentManager#downloadDocument(String, OutputStream)} and
     * Attempts to download and write the {@link RestDocument} to the given {@link OutputStream}.
     *
     * @param target The target {@link OutputStream} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    public void downloadDocument(@NotNull OutputStream target) throws ResultException {
        accessInternalState().getDocumentManager().downloadDocument(this.getDocumentId(), target);
    }

    /**
     * This is a shortcut for {@link DocumentManager#downloadDocument(String, OutputStream)} and
     * Attempts to download write the {@link RestDocument} to the given {@link File}.
     *
     * @param target The target {@link File} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    public void downloadDocument(@NotNull File target) throws ResultException {
        try (OutputStream outputStream = new FileOutputStream(target)) {
            accessInternalState().getDocumentManager().downloadDocument(this.getDocumentId(), outputStream);
        } catch (IOException e) {
            throw new ClientResultException(Error.REST_EXECUTION);
        }
    }

    /**
     * This is a shortcut for {@link DocumentManager#deleteDocument} and deletes the {@link RestDocument}.
     *
     * @throws ResultException Shall be thrown, should deleting the document fail.
     */
    public void deleteDocument() throws ResultException {
        accessInternalState().getDocumentManager().deleteDocument(this.getDocumentId());
    }

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and renames the {@link RestDocument}.
     *
     * @param fileName The new name for the {@link RestDocument}.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should renaming the document have failed.
     */
    public @NotNull RestDocument renameDocument(@NotNull String fileName) throws ResultException {
        return accessInternalState().getDocumentManager().renameDocument(this.getDocumentId(), fileName);
    }

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and updates the security information the
     * {@link RestDocument}.
     *
     * @param passwordType The security information to update the document with
     * @return The updated {@link RestDocument}.
     * @throws ResultException Shall be thrown, should updating the document security have failed.
     */
    public RestDocument updateDocumentSecurity(@NotNull PdfPasswordType passwordType) throws ResultException {
        return accessInternalState().getDocumentManager().updateDocumentSecurity(this.getDocumentId(), passwordType);
    }

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and returns {@link DocumentInfo} about the
     * {@link RestDocument}.
     *
     * @param infoType Detailed information for the document referenced by the unique documentId
     *                 in the server´s document storage.
     * @return The requested document {@link DocumentInfo}
     * @throws ResultException Shall be thrown, should fetching the document info have failed.
     */
    public DocumentInfo getDocumentInfo(@NotNull DocumentInfoType infoType) throws ResultException {
        return accessInternalState().getDocumentManager().getDocumentInfo(this.getDocumentId(), infoType);
    }

    /**
     * This is a shortcut for {@link DocumentManager#extractDocument} and extracts the {@link RestDocument}.
     *
     * @param fileExtract {@link DocumentFileExtract} settings for unpacking the archive document.
     * @return A list of the extracted {@link RestDocument}s.
     * @throws ResultException Shall be thrown, should the extraction have failed.
     */
    @Override
    public List<RestWebServiceDocument> extractDocument(
            @NotNull DocumentFileExtract fileExtract
    ) throws ResultException {
        return accessInternalState().getDocumentManager().extractDocument(this.getDocumentId(), fileExtract);
    }
}
