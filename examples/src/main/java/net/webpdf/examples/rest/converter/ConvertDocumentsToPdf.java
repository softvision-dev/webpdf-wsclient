package net.webpdf.examples.rest.converter;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.OperationConverter;
import net.webpdf.wsclient.openapi.OperationConverterPage;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link ConverterRestWebService} demonstrating the conversion of
 * some document to a PDF document using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ConvertDocumentsToPdf {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link ConverterRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link ConverterRestWebService} interface type.</li>
     * <li>The parameterization required to convert a document to a PDF document.</li>
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
             * (using {@link WebServiceType.CONVERTER} here): */
            ConverterRestWebService<RestDocument> converterWebService =
                    session.createWebServiceInstance(WebServiceType.CONVERTER);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OperationConverter converterOperation = converterWebService.getOperationParameters();

            /** Select further parameters for your webservice call.
             * In this example, we order the converter to always create pages with fixed dimensions of 300x100
             * millimetres.
             * Which might not be a perfect choice for your selected document, but demonstrates how to add parameters
             * to the converter call.
             * (Most of the time it is preferable to let the converter select page formats automatically.) */
            OperationConverterPage page = new OperationConverterPage();
            converterOperation.setPage(page);
            page.setWidth(300);
            page.setHeight(100);
            page.setMetrics(OperationConverterPage.MetricsEnum.MM);

            /** Execute the webservice and download your result document: */
            converterWebService.process(restDocument).downloadDocument(targetDocument);
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
