package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntryBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RestDocument extends AbstractDocument {

    @Nullable
    private String documentId;
    @Nullable
    private DocumentFileBean documentFile;
    @NotNull
    private final Map<Integer, HistoryEntryBean> historyMap = new HashMap<>();

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
        return this.documentId;
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
     * returns a {@link HistoryEntryBean} from the internal history map, by given historyId.
     *
     * @param historyId The history id of the {@link HistoryEntryBean} that shall be returned.
     * @return A {@link HistoryEntryBean} referencing the history of the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unused")
    @NotNull
    public HistoryEntryBean getHistoryElement(int historyId) throws ResultException {
        if (!historyMap.containsKey(historyId)) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }

        HistoryEntryBean historyBean = new HistoryEntryBean();
        historyBean.setId(historyId);

        return this.getHistoryElement(historyBean);
    }

    /**
     * returns a {@link HistoryEntryBean} from the internal history map, by given historyId or if the
     * Document doesn't exist create a new entry from the given {@link HistoryEntryBean}
     *
     * @param historyBean The {@link HistoryEntryBean}, that shall be returned or added for further processing.
     * @return A {@link HistoryEntryBean} referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public HistoryEntryBean getHistoryElement(@Nullable HistoryEntryBean historyBean) throws ResultException {
        if (historyBean == null) {
            throw new ResultException(Result.build(Error.INVALID_HISTORY_DATA));
        }

        int historyId = historyBean.getId();
        if (historyMap.containsKey(historyId)) {
            return historyMap.get(historyId);
        }

        historyMap.put(historyId, historyBean);

        return historyBean;
    }

    /**
     * returns the {@link List}&lt;{@link HistoryEntryBean}&gt; of the managed REST document.
     *
     * @return the {@link List}&lt;{@link HistoryEntryBean}&gt; of the managed REST document.
     */
    @NotNull
    public List<HistoryEntryBean> getHistory() {
        return new ArrayList<>(historyMap.values());
    }
}
