package net.webpdf.wsclient.documents.rest.documentmanager;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.schema.beans.HistoryEntryBean;
import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface DocumentManager<T_REST_DOCUMENT extends RestDocument> {

    /**
     * Downloads {@link RestDocument} to target path
     *
     * @param document     {@link RestDocument} instance
     * @param outputStream {@link OutputStream} for downloaded content
     * @throws ResultException a {@link ResultException}
     */
    void downloadDocument(@Nullable T_REST_DOCUMENT document, @Nullable OutputStream outputStream)
            throws ResultException;

    /**
     * Uploads the given {@link DocumentFileBean} to the webPDF server and returns the {@link RestDocument} reference
     * to the uploaded resource.
     *
     * @param file The file, that shall be uploaded for further processing.
     * @return A {@link RestDocument} referencing the uploaded document.
     * @throws IOException an {@link IOException}
     */
    @NotNull
    T_REST_DOCUMENT uploadDocument(@Nullable File file) throws IOException;

    /**
     * returns the {@link RestDocument} from the internal document map, by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be returned.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    T_REST_DOCUMENT getDocument(@Nullable String documentId) throws ResultException;

    /**
     * returns the {@link RestDocument} from the internal document map, by given {@link DocumentFileBean} or if the
     * Document doesn't exist create a new entry from the given {@link DocumentFileBean}
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be returned or added for further processing.
     * @return A {@link RestDocument} referencing the uploaded resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    T_REST_DOCUMENT getDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException;

    /**
     * Deletes the given {@link RestDocument} from the webPDF server by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be returned.
     * @throws ResultException a {@link ResultException}
     */
    void deleteDocument(@Nullable String documentId) throws ResultException;

    /**
     * Deletes the given {@link RestDocument} from the webPDF server by given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be deleted.
     * @throws ResultException a {@link ResultException}
     */
    void deleteDocument(@Nullable DocumentFileBean documentFileBean) throws ResultException;

    /**
     * Renames the {@link RestDocument} on the webPDF server, by given documentId, to the given filename.
     *
     * @param documentId The document id of the {@link RestDocument} that shall be renamed.
     * @param fileName   The new file name
     * @return A {@link RestDocument} referencing the renamed resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    T_REST_DOCUMENT renameDocument(@Nullable String documentId, @Nullable String fileName) throws IOException;

    /**
     * Renames the {@link RestDocument} on the webPDF server, by given {@link DocumentFileBean}, to the given filename.
     *
     * @param documentFileBean The {@link DocumentFileBean}, that shall be renamed.
     * @param fileName         The new file name
     * @return A {@link RestDocument} referencing the renamed resource.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    T_REST_DOCUMENT renameDocument(@Nullable DocumentFileBean documentFileBean, @Nullable String fileName)
            throws IOException;

    /**
     * updates file history information from the server for the given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be
     *                        updated.
     * @throws ResultException a {@link ResultException}
     */
    void updateDocumentHistory(@Nullable DocumentFileBean documentFileBean) throws ResultException;

    /**
     * returns the {@link List}&lt;{@link HistoryEntryBean}&gt; from the internal document history map, by given documentId.
     *
     * @param documentId The document id of the {@link RestDocument} from whom the history shall be returned.
     * @return A {@link List}&lt;{@link HistoryEntryBean}&gt; referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    List<HistoryEntryBean> getDocumentHistory(@Nullable String documentId) throws ResultException;

    /**
     * returns the {@link List HistoryElement}&lt;{@link HistoryEntryBean}&gt; from the internal document history map,
     * by given {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be
     *                        returned.
     * @return A {@link List}&lt;{@link HistoryEntryBean}&gt; referencing the uploaded resource history.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    List<HistoryEntryBean> getDocumentHistory(@Nullable DocumentFileBean documentFileBean) throws ResultException;

    /**
     * Change the {@link HistoryEntryBean} of a specific {@link HistoryEntryBean} in a {@link RestDocument}, by given
     * documentId.
     *
     * @param documentId  The document id of the {@link RestDocument} from whom the history shall be changed.
     * @param historyBean The {@link HistoryEntryBean} of the {@link HistoryEntryBean} to change to.
     * @return The changed {@link HistoryEntryBean}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    HistoryEntryBean setDocumentHistoryElement(@Nullable String documentId, HistoryEntryBean historyBean)
            throws ResultException;

    /**
     * Change the {@link HistoryEntryBean} of a specific {@link HistoryEntryBean} in a {@link RestDocument}, by given
     * {@link DocumentFileBean}.
     *
     * @param documentFileBean The {@link DocumentFileBean} of the {@link RestDocument} from whom the history shall be
     *                         changed.
     * @param historyBean      The {@link HistoryEntryBean} of the {@link HistoryEntryBean} to change to.
     * @return The changed {@link HistoryEntryBean}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    HistoryEntryBean setDocumentHistoryElement(
            @Nullable DocumentFileBean documentFileBean, @Nullable HistoryEntryBean historyBean
    ) throws ResultException;

    /**
     * Gets file information for all documents on the server for current session.
     *
     * @return A {@link List} referencing the found {@link RestDocument}s.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    List<T_REST_DOCUMENT> updateDocumentList() throws ResultException;

    /**
     * Gets all {@link RestDocument} from the internal document map.
     *
     * @return A {@link List} referencing the found {@link RestDocument}s.
     */
    @NotNull
    List<T_REST_DOCUMENT> getDocumentList();

    /**
     * checks if the creation of a document history is active
     *
     * @return is the document history active?
     */
    boolean isActiveDocumentHistory();

    /**
     * De/Activates the creation of a document history
     *
     * @param activeDocumentHistory De/Activates the creation of a document history
     */
    void setActiveDocumentHistory(boolean activeDocumentHistory);

    /**
     * Checks whether the given document id is listed in the document manager.
     *
     * @param documentId The id, that shall be checked.
     * @return True, if the document is contained.
     */
    boolean containsDocument(@Nullable String documentId);

    /**
     * Checks whether the given document is listed in the document manager.
     *
     * @param document The document, that shall be checked.
     * @return True, if the document is contained.
     */
    @SuppressWarnings("unused")
    boolean containsDocument(@Nullable DocumentFileBean document);

    /**
     * Checks whether the given document is listed in the document manager and returns the document's id.
     *
     * @param document The document, that shall be checked.
     * @return The id of the contained document.
     * @throws ResultException Shall be thrown, if the document id can not be resolved, or the document is not contained.
     */
    @NotNull
    String getContainedDocumentID(@Nullable DocumentFileBean document) throws ResultException;

    /**
     * Returns the document's id.
     *
     * @param document The document, that shall be checked.
     * @return The id of the document.
     * @throws ResultException Shall be thrown, if the document id can not be resolved.
     */
    @NotNull
    String getDocumentID(@Nullable DocumentFileBean document) throws ResultException;

    /**
     * Returns the current {@link RestSession} of this document manager.
     * @return The current {@link RestSession} of this document manager.
     */
    @NotNull
    RestSession<T_REST_DOCUMENT> getSession();
}
