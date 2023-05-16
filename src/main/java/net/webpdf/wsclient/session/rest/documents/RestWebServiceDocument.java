package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.session.documents.AbstractDocument;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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
     * This is a shortcut for {@link DocumentManager#downloadDocument(RestDocument, OutputStream)} and
     * Attempts to download and write the {@link RestDocument} to the given {@link OutputStream}.
     *
     * @param target The target {@link OutputStream} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    public void downloadDocument(@NotNull OutputStream target) throws ResultException {
        accessInternalState().getDocumentManager().downloadDocument(this, target);
    }

    /**
     * This is a shortcut for {@link DocumentManager#downloadDocument(RestDocument, OutputStream)} and
     * Attempts to download write the {@link RestDocument} to the given {@link File}.
     *
     * @param target The target {@link File} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    public void downloadDocument(@NotNull File target) throws ResultException {
        try (OutputStream outputStream = new FileOutputStream(target)) {
            accessInternalState().getDocumentManager().downloadDocument(this, outputStream);
        } catch (IOException e) {
            throw new ClientResultException(Error.REST_EXECUTION);
        }
    }

}
