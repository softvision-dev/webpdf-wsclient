package net.webpdf.wsclient.documents.rest;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntryBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RestDocument extends Document {

    /**
     * Returns the document ID of the managed REST document.
     *
     * @return The document ID of the managed REST document.
     */
    @Nullable
    String getSourceDocumentId();

    /**
     * Returns the {@link DocumentFileBean} of the managed REST document.
     *
     * @return The {@link DocumentFileBean} of the managed REST document.
     */
    @Nullable
    DocumentFileBean getDocumentFile();

    /**
     * sets the {@link DocumentFileBean} of the managed REST document.
     *
     * @param documentFile the new {@link DocumentFileBean} of the managed REST document.
     */
    void setDocumentFile(@Nullable DocumentFileBean documentFile);

    /**
     * returns a {@link HistoryEntryBean} from the internal history map, by given historyId.
     *
     * @param historyId The history id of the {@link HistoryEntryBean} that shall be returned.
     * @return A {@link HistoryEntryBean} referencing the history of the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unused")
    @NotNull
    HistoryEntryBean getHistoryElement(int historyId) throws ResultException;

    /**
     * returns a {@link HistoryEntryBean} from the internal history map, by given historyId or if the
     * Document doesn't exist create a new entry from the given {@link HistoryEntryBean}
     *
     * @param historyBean The {@link HistoryEntryBean}, that shall be returned or added for further processing.
     * @return A {@link HistoryEntryBean} referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    HistoryEntryBean getHistoryElement(@Nullable HistoryEntryBean historyBean) throws ResultException;

    /**
     * returns the {@link List}&lt;{@link HistoryEntryBean}&gt; of the managed REST document.
     *
     * @return the {@link List}&lt;{@link HistoryEntryBean}&gt; of the managed REST document.
     */
    @NotNull
    List<HistoryEntryBean> getHistory();
}
