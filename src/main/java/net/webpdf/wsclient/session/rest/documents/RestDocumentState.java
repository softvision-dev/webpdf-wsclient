package net.webpdf.wsclient.session.rest.documents;


import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>
 * An instance of {@link RestDocumentState} represents the internal state of a {@link RestDocument},
 * that has been uploaded to a webPDF server.
 * </p>
 * <p>
 * The {@link RestDocumentState} allows a {@link RestDocument} to access and publish the state, without violating the
 * exclusive update rights of the {@link DocumentManager}.<br>
 * (i.e. The {@link DocumentManager} shall always be the only entity, that is allowed to change the internal state of an
 * uploaded document.)
 * </p>
 */
public interface RestDocumentState<T_DOCUMENT extends RestDocument> {

    /**
     * Returns the document ID of the managed {@link RestDocument}.
     *
     * @return The document ID of the managed {@link RestDocument}.
     */
    @NotNull String getDocumentId();

    /**
     * Returns the {@link DocumentFile} of the managed {@link RestDocument}.
     *
     * @return The {@link DocumentFile} of the managed {@link RestDocument}.
     */
    @NotNull DocumentFile getDocumentFile();

    /**
     * Sets the {@link DocumentFile} of the managed {@link RestDocument}.
     *
     * @param documentFile the new {@link DocumentFile} of the managed {@link RestDocument}.
     */
    void setDocumentFile(@NotNull DocumentFile documentFile);

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
    void updateHistoryEntry(@NotNull HistoryEntry historyEntry) throws ResultException;

    /**
     * Returns the most recent {@link HistoryEntry}.
     *
     * @return The most recent {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    @SuppressWarnings("unused")
    @NotNull HistoryEntry lastHistory() throws ResultException;

    /**
     * Returns the number of known {@link HistoryEntry}s for this {@link RestWebServiceDocument}.
     *
     * @return The number of known {@link HistoryEntry}s for this {@link RestWebServiceDocument}.
     */
    @SuppressWarnings("unused")
    int getHistorySize();

    /**
     * Returns the currently active {@link HistoryEntry}.
     *
     * @return The currently active {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    @SuppressWarnings("unused")
    @NotNull HistoryEntry activeHistory() throws ResultException;

    /**
     * Returns the owning {@link DocumentManager}.
     *
     * @return The owning {@link DocumentManager}.
     */
    @NotNull DocumentManager<T_DOCUMENT> getDocumentManager();
}
