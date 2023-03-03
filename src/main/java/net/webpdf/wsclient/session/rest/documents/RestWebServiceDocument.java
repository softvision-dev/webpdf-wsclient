package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.documents.AbstractDocument;
import net.webpdf.wsclient.session.rest.documents.manager.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An instance of {@link RestWebServiceDocument} represents a document, that has been uploaded to a webPDF server.
 * </p>
 * <p>
 * Such a {@link RestWebServiceDocument} defines a document, that has been uploaded to the webPDF server so that it and
 * it´s history can be accessed via a {@link DocumentManager} by an assigned document ID.
 * </p>
 */
public class RestWebServiceDocument extends AbstractDocument implements RestDocument {

    private final @NotNull ConcurrentHashMap<Integer, HistoryEntry> historyMap = new ConcurrentHashMap<>();
    private final @NotNull AtomicReference<DocumentFile> documentFile = new AtomicReference<>();
    private final @NotNull String documentId;

    /**
     * Creates a {@link RestWebServiceDocument} known to the webPDF server by the given document ID.
     *
     * @param documentId The document ID of the managed {@link RestWebServiceDocument}.
     */
    public RestWebServiceDocument(@NotNull String documentId) {
        super(null);
        this.documentId = documentId;
    }

    /**
     * Returns the document ID of the managed {@link RestWebServiceDocument}.
     *
     * @return The document ID of the managed {@link RestWebServiceDocument}.
     */
    @Override
    public @NotNull String getDocumentId() {
        return this.documentId;
    }

    /**
     * Returns the {@link DocumentFile} of the managed {@link RestWebServiceDocument}.
     *
     * @return The {@link DocumentFile} of the managed {@link RestWebServiceDocument}.
     */
    @Override
    public @NotNull DocumentFile getDocumentFile() {
        return documentFile.get();
    }

    /**
     * Sets the {@link DocumentFile} of the managed {@link RestWebServiceDocument}.
     *
     * @param documentFile the new {@link DocumentFile} of the managed {@link RestWebServiceDocument}.
     */
    @Override
    public void setDocumentFile(@NotNull DocumentFile documentFile) {
        this.documentFile.set(documentFile);
    }

    /**
     * Returns the {@link HistoryEntry}s of the managed {@link RestWebServiceDocument}.
     *
     * @return The {@link HistoryEntry}s of the managed {@link RestWebServiceDocument}.
     */
    @Override
    public @NotNull List<HistoryEntry> getHistory() {
        return new ArrayList<>(historyMap.values());
    }

    /**
     * Replaces the internally stored {@link HistoryEntry} list of the managed {@link RestDocument}
     *
     * @param historyEntries The new {@link HistoryEntry}s to be set.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    @Override
    public void setHistory(@NotNull HistoryEntry[] historyEntries) throws ResultException {
        this.historyMap.clear();

        for (HistoryEntry historyEntry : historyEntries) {
            updateHistoryEntry(historyEntry);
        }
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
        if (!this.historyMap.containsKey(historyId)) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
        }
        return this.historyMap.get(historyId);
    }

    /**
     * Updates the given {@link HistoryEntry} in the internally managed document history.
     *
     * @param historyEntry The {@link HistoryEntry} containing the values to be set.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    public void updateHistoryEntry(@Nullable HistoryEntry historyEntry) throws ResultException {
        if (historyEntry == null) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
        }
        int historyId = historyEntry.getId();

        // disable active state for all entries, because the new entry is active
        if (historyEntry.isActive()) {
            for (Map.Entry<Integer, HistoryEntry> entry : this.historyMap.entrySet()) {
                entry.getValue().setActive(false);
            }
        }
        this.historyMap.put(historyId, historyEntry);
    }

    /**
     * Returns the most recent {@link HistoryEntry}.
     *
     * @return The most recent {@link HistoryEntry}.
     * @throws ResultException Shall be thrown, when updating the document history failed.
     */
    @SuppressWarnings("unused")
    public @NotNull HistoryEntry lastHistory() throws ResultException {
        if (this.historyMap.isEmpty()) {
            throw new ClientResultException(Error.INVALID_HISTORY_DATA);
        }
        return this.historyMap.get(this.historyMap.size());
    }

    /**
     * Returns the number of known {@link HistoryEntry}s for this {@link RestWebServiceDocument}.
     *
     * @return The number of known {@link HistoryEntry}s for this {@link RestWebServiceDocument}.
     */
    @SuppressWarnings("unused")
    public int getHistorySize() {
        return this.historyMap.size();
    }

}
