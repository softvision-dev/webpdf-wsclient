package net.webpdf.wsclient.documents.rest;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <p>
 * A class implementing {@link RestDocument} represents a document, that has been uploaded to a webPDF server.
 * </p>
 * <p>
 * Such a {@link RestDocument} defines a document, that has been uploaded to the webPDF server so that it and it´s
 * history can be accessed via a {@link DocumentManager} by an assigned document ID.
 * </p>
 */
@SuppressWarnings("unused")
public interface RestDocument extends Document {

    /**
     * Returns the document ID of the managed {@link RestDocument}.
     *
     * @return The document ID of the managed {@link RestDocument}.
     */
    @Nullable String getDocumentId();

    /**
     * Returns the {@link DocumentFile} of the managed {@link RestDocument}.
     *
     * @return The {@link DocumentFile} of the managed {@link RestDocument}.
     */
    @Nullable DocumentFile getDocumentFile();

    /**
     * Sets the {@link DocumentFile} of the managed {@link RestDocument}.
     *
     * @param documentFile the new {@link DocumentFile} of the managed {@link RestDocument}.
     */
    void setDocumentFile(@Nullable DocumentFile documentFile);

    /**
     * Returns the {@link HistoryEntry}s of the managed {@link RestDocument}.
     *
     * @return The {@link HistoryEntry}s of the managed {@link RestDocument}.
     */
    @NotNull List<HistoryEntry> getHistory();

    /**
     * Replaces the internally stored {@link HistoryEntry} list of the managed {@link RestDocument}
     *
     * @param historyEntries The new {@link HistoryEntry}s to be set.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    void setHistory(@NotNull HistoryEntry[] historyEntries) throws ResultException;

    /**
     * Returns a {@link HistoryEntry} from the internal history map, by given history ID.
     *
     * @param historyId The history ID of the {@link HistoryEntry} that shall be returned.
     * @return A {@link HistoryEntry} representing a historic state of the uploaded resource.
     * @throws ResultException Shall be thrown, should accessing the document history fail.
     */
    @SuppressWarnings("unused")
    @NotNull HistoryEntry getHistoryEntry(int historyId) throws ResultException;

    /**
     * Updates the given {@link HistoryEntry} in the internally managed document history.
     *
     * @param historyEntry The {@link HistoryEntry} containing the values to be set.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    void updateHistoryEntry(@Nullable HistoryEntry historyEntry) throws ResultException;

}
