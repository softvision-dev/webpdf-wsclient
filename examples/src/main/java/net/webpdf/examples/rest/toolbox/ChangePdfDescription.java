package net.webpdf.examples.rest.toolbox;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.OperationBaseToolbox;
import net.webpdf.wsclient.openapi.OperationToolboxDescriptionDescription;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ToolboxRestWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link ToolboxRestWebService} demonstrating how you can change the
 * description of your PDF document using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ChangePdfDescription {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link ToolboxRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link ToolboxRestWebService} interface type.</li>
     * <li>The parameterization required to change the description of a PDF document.</li>
     * <li>The handling of the source and result {@link RestDocument} for a {@link RestSession}.</li>
     * </ul>
     *
     * <b>Be aware:</b> You have to adapt the fields of this class accordingly, otherwise this example is not runnable.
     * </p>
     */
    public static void main(String... args) throws Exception {
        /** Initialize a simple {@link SessionContext}. */
        SessionContext sessionContext = new SessionContext(
                WebServiceProtocol.REST,
                new URL(webPDFServerURL)
        );

        try (
                /** Initialize the session with the webPDF Server (using REST): */
                RestSession<RestDocument> session = SessionFactory.createInstance(sessionContext)
        ) {
            /** Instantiate the {@link WebService} interface type you want to call.
             * (using {@link WebServiceType.TOOLBOX} here): */
            ToolboxRestWebService<RestDocument> toolboxWebService =
                    session.createWebServiceInstance(WebServiceType.TOOLBOX);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Initialize and add a toolbox parameter root: */
            OperationBaseToolbox toolboxOperation = new OperationBaseToolbox();
            toolboxWebService.getOperationParameters().add(toolboxOperation);

            /** Initialize and add the description operation: */
            OperationToolboxDescriptionDescription description = new OperationToolboxDescriptionDescription();
            toolboxOperation.setDescription(description);

            /**
             * Adapt the documents description:
             */
            description.setAllowEmptyValues(true);
            description.setAuthor("Author1;Author2");
            description.setCreator("Creator");
            description.setKeywords("Keyword1;Keyword2;Keyword3");
            description.setProducer("Producer");
            description.setSubject("Subject");
            description.setTitle("Title");

            /** Execute the webservice and download your result document: */
            toolboxWebService.process(restDocument).downloadDocument(targetDocument);
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
