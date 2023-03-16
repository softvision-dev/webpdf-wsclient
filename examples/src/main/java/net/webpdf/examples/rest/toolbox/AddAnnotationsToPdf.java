package net.webpdf.examples.rest.toolbox;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.*;
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
 * Here you will find a usage example for the webPDF {@link ToolboxRestWebService} demonstrating how you can add a
 * markup annotation to a PDF document using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class AddAnnotationsToPdf {

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
     * <li>The parameterization required to add a markup annotation to a PDF document.</li>
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
                    session.createWSInstance(WebServiceType.TOOLBOX);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Initialize and add a toolbox parameter root: */
            OperationBaseToolbox toolboxOperation = new OperationBaseToolbox();
            toolboxWebService.getOperationParameters().add(toolboxOperation);

            /** Initialize and add the annotation operation: */
            OperationToolboxAnnotationAnnotation annotation = new OperationToolboxAnnotationAnnotation();
            toolboxOperation.setAnnotation(annotation);

            /** Parameterize your webservice call.
             * We want to add a new annotation to the document: */
            OperationAddToolboxAnnotation add = new OperationAddToolboxAnnotation();
            annotation.setAdd(add);

            /**
             * We select a markup annotation to add:
             * (You may add multiple annotations using the same operation.)
             */
            OperationMarkupAnnotation markupAnnotation = new OperationMarkupAnnotation();
            add.addMarkupItem(markupAnnotation);
            markupAnnotation.setCreator("Creator");
            markupAnnotation.setName("Annotationsname");
            markupAnnotation.setSubject("Annotationsthema");
            markupAnnotation.setContents("Inhalt der Annotation");
            markupAnnotation.setIntents("Zweck der Annotation");
            markupAnnotation.setPage(1);
            markupAnnotation.setMarkupType(OperationMarkupAnnotation.MarkupTypeEnum.HIGHLIGHT);

            /**
             * Finally we position the annotation on the selected page:
             */
            OperationPositionMarkupAnnotation position = new OperationPositionMarkupAnnotation();
            markupAnnotation.setPosition(position);
            OperationRectangle rectangle = new OperationRectangle();
            position.addPathElementItem(rectangle);
            rectangle.setX(15f);
            rectangle.setY(20f);
            rectangle.setWidth(80f);
            rectangle.setHeight(14f);
            rectangle.setCoordinates(OperationRectangle.CoordinatesEnum.USER);
            rectangle.setMetrics(OperationRectangle.MetricsEnum.PX);

            /** Execute the webservice and download your result document: */
            toolboxWebService.process(restDocument).downloadDocument(targetDocument);
        } catch (ResultException ex) {
            /** Should an exception have occurred, you can use the following methods to request further information
             * about the exception: */
            int errorCode = ex.getErrorCode();
            Error error = ex.getWsclientError();
            String message = ex.getMessage();
            Throwable cause = ex.getCause();
            String stMessage = ex.getStackTraceMessage();

            /** Also be aware, that you may use the subtypes {@link ClientResultException},
             * {@link ServerResultException} and {@link AuthResultException} to differentiate the different failure
             * sources in your catches. */
        }
    }

}
