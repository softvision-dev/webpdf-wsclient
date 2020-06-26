package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestDocument extends AbstractDocument {

    @NotNull
    private final ConcurrentHashMap<Integer, HistoryEntry> historyMap = new ConcurrentHashMap<>();
    @Nullable
    private final String documentId;
    @Nullable
    private DocumentFileBean documentFile;

    /**
     * Manages a REST document with the given document ID.
     *
     * @param documentId The document ID of the managed REST document.
     */
    public RestDocument(@Nullable String documentId) {
        super(null);
        this.documentId = documentId;
    }

    /**
     * Returns the document ID of the managed REST document.
     *
     * @return The document ID of the managed REST document.
     */
    @Nullable
    public String getSourceDocumentId() {
        if (this.documentId != null) {
            return this.documentId;
        }
        return this.documentFile != null ? this.documentFile.getDocumentId() : null;
    }

    /**
     * Returns the {@link DocumentFileBean} of the managed REST document.
     *
     * @return The {@link DocumentFileBean} of the managed REST document.
     */
    @Nullable
    public DocumentFileBean getDocumentFile() {
        return documentFile;
    }

    /**
     * sets the {@link DocumentFileBean} of the managed REST document.
     *
     * @param documentFile the new {@link DocumentFileBean} of the managed REST document.
     */
    public void setDocumentFile(@Nullable DocumentFileBean documentFile) {
        this.documentFile = documentFile;
    }

    /**
     * returns a {@link HistoryEntry} from the internal history map, by given historyId or if the
     * Document doesn't exist create a new entry from the given {@link HistoryEntry}
     *
     * @param historyBean The {@link HistoryEntry}, that shall be returned or added for further processing.
     * @throws ResultException a {@link ResultException}
     */
    void replaceHistoryEntry(@Nullable HistoryEntry historyBean) throws ResultException {
        if (historyBean == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }
        int historyId = historyBean.getId();
        if (!this.historyMap.containsKey(historyId)) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }

        // disable active state for all entries, because the new entry is active
        if (historyBean.isActive()) {
            for (Map.Entry<Integer, HistoryEntry> historyEntry : this.historyMap.entrySet()) {
                historyEntry.getValue().setActive(false);
            }
        }
        this.historyMap.put(historyId, historyBean);
    }

    /**
     * Store the list of {@link HistoryEntry} entries in the internal history map
     *
     * @param historyEntries list of {@link HistoryEntry} entries
     */
    void storeHistory(HistoryEntry[] historyEntries) {
        this.historyMap.clear();

        for (HistoryEntry historyEntry : historyEntries) {
            this.historyMap.put(historyEntry.getId(), historyEntry);
        }
    }

    /**
     * Returns a copied {@link List}&lt;{@link HistoryEntry}&gt; of the managed REST document.
     *
     * @return the {@link List}&lt;{@link HistoryEntry}&gt; of the managed REST document.
     */
    @NotNull
    public List<HistoryEntry> getHistory() {
        return new ArrayList<>(historyMap.values());
    }
}
