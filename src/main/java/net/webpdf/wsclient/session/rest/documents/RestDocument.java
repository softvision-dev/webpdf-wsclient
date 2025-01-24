package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFileExtract;
import net.webpdf.wsclient.openapi.DocumentInfo;
import net.webpdf.wsclient.openapi.DocumentInfoType;
import net.webpdf.wsclient.openapi.DocumentFile;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.documents.Document;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
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

    /**
     * This is a shortcut for {@link DocumentManager#deleteDocument} and deletes the {@link RestDocument}.
     *
     * @throws ResultException Shall be thrown, should deleting the document fail.
     */
    void deleteDocument() throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and renames the {@link RestDocument}.
     *
     * @param fileName The new name for the {@link RestDocument}.
     * @return The resulting {@link RestDocument} handle.
     * @throws ResultException Shall be thrown, should renaming the document have failed.
     */
    @NotNull RestDocument renameDocument(@NotNull String fileName) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and updates the security information the
     * {@link RestDocument}.
     *
     * @param passwordType The security information to update the document with
     * @return The updated {@link RestDocument}.
     * @throws ResultException Shall be thrown, should updating the document security have failed.
     */
    RestDocument updateDocumentSecurity(@NotNull PdfPasswordType passwordType) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#renameDocument} and returns {@link DocumentInfo} about the
     * {@link RestDocument}.
     *
     * @param infoType Detailed information for the document referenced by the unique documentId
     *                 in the server´s document storage.
     * @return The requested document {@link DocumentInfo}
     * @throws ResultException Shall be thrown, should fetching the document info have failed.
     */
    DocumentInfo getDocumentInfo(@NotNull DocumentInfoType infoType) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#extractDocument} and extracts the {@link RestDocument}.
     *
     * @param fileExtract {@link DocumentFileExtract} settings for unpacking the archive document.
     * @return A list of the extracted {@link RestDocument}s.
     * @throws ResultException Shall be thrown, should the extraction have failed.
     */
    List<? extends RestDocument> extractDocument(@NotNull DocumentFileExtract fileExtract) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#extractArchiveFile} and extracts and downloads the given archive
     * path of the {@link RestDocument}.
     *
     * @param archivePath  The path of the file to extract in the given archive.
     * @param target The {@link OutputStream} to write the extracted archive file to.
     * @throws ResultException Shall be thrown should the download have failed.
     */
    void extractArchiveFile(@NotNull String archivePath, @NotNull OutputStream target) throws ResultException;

    /**
     * This is a shortcut for {@link DocumentManager#updateDocument} and updates the {@link RestDocument}.
     *
     * @param data The data {@link InputStream} to update the document with.
     * @return The updated {@link RestDocument}.
     * @throws ResultException Shall be thrown should the update have failed.
     */
    @NotNull RestDocument updateDocument(@NotNull InputStream data) throws ResultException;
}
