package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * A class implementing {@link RestDocument} represents a document, that has been uploaded to a webPDF server.
 * </p>
 */
public interface RestDocument extends Document {

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
     * Returns the {@link HistoryEntry}s of the managed {@link RestDocument}.
     *
     * @return The {@link HistoryEntry}s of the managed {@link RestDocument}.
     */
    @NotNull List<HistoryEntry> getHistory();

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
     * This is a shortcut for {@link DocumentManager#downloadDocument(RestDocument, OutputStream)} and
     * Attempts to download and write the {@link RestDocument} to the given {@link OutputStream}.
     *
     * @param target The target {@link OutputStream} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    void downloadDocument(@NotNull OutputStream target) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#downloadDocument(RestDocument, OutputStream)} and
     * Attempts to download and write the {@link RestDocument} to the given {@link File}.
     *
     * @param target The target {@link File} the {@link RestDocument} shall be
     *               written to.
     * @throws ResultException Shall be thrown, should writing the result document fail.
     */
    void downloadDocument(@NotNull File target) throws ResultException;

}
