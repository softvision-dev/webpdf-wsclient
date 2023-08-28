package net.webpdf.examples.rest.documents;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.openapi.DocumentFileCompress;
import net.webpdf.wsclient.openapi.DocumentFileExtract;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.administration.AdministrationManager;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Here you will find a usage example for the webPDF {@link AdministrationManager} demonstrating how you can
 * manage documents from the server using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class UseDocumentManager {
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceFile = new File("The path to your source file");
    private static final File targetFile = new File("The path to your target file");

    /**
     * <p>
     * This usage example for the webPDF {@link AdministrationManager} shall demonstrate:
     * <ul>
     * <li>how to upload a document</li>
     * <li>how to get a list of uploaded</li>
     * <li>how to get a specific uploaded document</li>
     * <li>how to rename a document</li>
     * <li>how to download a document</li>
     * <li>how to handle the document history</li>
     * <li>how to compress documents</li>
     * <li>how to extract an archive</li>
     * <li>how to delete a document</li>
     * </ul>
     *
     * <b>Be aware:</b> You have to adapt the fields of this class accordingly, otherwise this example is not runnable.
     * </p>
     * <p>
     */
    public static void main(String... args) throws Exception {
        /** Initialize a simple {@link SessionContext}.*/
        SessionContext sessionContext = new SessionContext(
                WebServiceProtocol.REST,
                new URL(webPDFServerURL)
        );

        try (
                /** Initialize the session with the webPDF Server (using REST): */
                RestSession<RestDocument> session = SessionFactory.createInstance(sessionContext);
                OutputStream outputStream = Files.newOutputStream(targetFile.toPath())
        ) {
            /** Get {@link DocumentManager} from {@link RestSession} */
            DocumentManager<RestDocument> documentManager = session.getDocumentManager();

            /** upload file in {@link DocumentManager} */
            documentManager.uploadDocument(sourceFile);

            /** get a list of all currently uploaded {@link RestDocument}s */
            List<RestDocument> documents = documentManager.getDocuments();

            /** check if the {@link DocumentManager} has a {@link RestDocument} */
            documentManager.containsDocument("Document id");

            /** get a specific {@link RestDocument} by id */
            RestDocument specificDocument = documentManager.getDocument("Document id");

            /** rename a {@link RestDocument} */
            documentManager.renameDocument(specificDocument.getDocumentId(), "new name");
            /** you can also rename the file directly */
            specificDocument.renameDocument("new name");

            /** download a {@link RestDocument} to {@link File} */
            documentManager.downloadDocument(specificDocument.getDocumentId(), outputStream);
            /** you can also download the file directly */
            specificDocument.downloadDocument(outputStream);

            /** get the {@link List<HistoryEntry>} */
            List<HistoryEntry> historyEntries = documentManager.getDocumentHistory(specificDocument.getDocumentId());
            /** you can also get the document history directly */
            List<HistoryEntry> historyEntries = specificDocument.getHistory();

            /** change the active history entry */
            historyEntries.get(0).setActive(true);

            /** update a history entry */
            documentManager.updateDocumentHistory(specificDocument.getDocumentId(), historyEntries.get(0));

            /** compress {@link RestDocument}s to an archive */
            DocumentFileCompress fileCompress = new DocumentFileCompress();

            List<String> documentIdList = new ArrayList<>();
            for (RestDocument document : documents) {
                documentIdList.add(document.getDocumentId());
            }

            fileCompress.setDocumentIdList(documentIdList);
            fileCompress.setArchiveFileName("archive");

            RestDocument archiveFile = documentManager.compressDocuments(fileCompress);

            /** extract all {@link RestDocument}s from an archive */
            List<RestDocument> unzippedFiles = documentManager.extractDocument(
                    archiveFile.getDocumentId(), new DocumentFileExtract()
            );
            /** you can also extract the archive directly */
            archiveFile.extractDocument(new DocumentFileExtract());

            /** delete a {@link RestDocument} from the {@link DocumentManager} */
            documentManager.deleteDocument(archiveFile.getDocumentId());
            /** you can also delete the document directly */
            archiveFile.deleteDocument();
        } catch (ResultException ex) {
            /** Should an exception have occurred, you can use the following methods to request further information
             * about the exception: */
            int errorCode = ex.getErrorCode();
            Error error = ex.getClientError();
            String message = ex.getMessage();
            Throwable cause = ex.getCause();
            String stMessage = ex.getStackTraceMessage();

            /** Also be aware, that you may use the subtypes {@link ClientResultException},
             * {@link ServerResultException} and {@link AuthResultException} to differentiate the different failure
             * sources in your catches. */
        }
    }
}