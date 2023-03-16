package net.webpdf.examples.soap.ocr;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.OcrRestWebService;
import net.webpdf.wsclient.webservice.soap.OcrWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link OcrWebService} Here you will find a usage example for the
 * webPDF {@link OcrRestWebService} demonstrating how you can extract text from a document using the SOAP API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ApplyOCRToPdf {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link OcrWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link OcrWebService} interface type.</li>
     * <li>The parameterization required to OCR text of a document.</li>
     * <li>The handling of the source and result {@link SoapDocument} for a {@link SoapSession}.</li>
     * </ul>
     *
     * <b>Be aware:</b> You have to adapt the fields of this class accordingly, otherwise this example is not runnable.
     * </p>
     */
    public static void main(String... args) throws Exception {
        /** Initialize a simple {@link SessionContext}. */
        SessionContext sessionContext = new SessionContext(
                WebServiceProtocol.SOAP,
                new URL(webPDFServerURL)
        );

        /** Please take note, that the {@link SoapSession} and {@link SoapDocument} are used in a try-with-resources
         * statement. Both are closeable and should be closed. */
        try (
                /** Initialize the session with the webPDF Server (using SOAP): */
                SoapSession<SoapDocument> session = SessionFactory.createInstance(sessionContext);
                /** Prepare the source {@link SoapDocument}, that shall be processed.
                 * This shall only demonstrate: It is possible to initialize the source document here using one
                 * try statement less. You can always choose to do that elsewhere.*/
                SoapDocument soapDocument = new SoapWebServiceDocument(sourceDocument)
        ) {
            /** Instantiate the {@link WebService} interface type you want to call.
             * (using {@link WebServiceType.OCR} here): */
            OcrWebService<SoapDocument> ocrWebService =
                    session.createWebServiceInstance(WebServiceType.OCR);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OcrType ocr = ocrWebService.getOperationParameters();

            /** Parameterize your webservice call.
             * For this simple example, we want to extract text contents to a *.txt file.
             */
            ocr.setLanguage(OcrLanguageType.DEU);
            ocr.setCheckResolution(false);
            ocr.setForceEachPage(true);
            ocr.setImageDpi(300);
            ocr.setOutputFormat(OcrOutputType.TEXT);

            /** Searching for text in a 800x600 pixel area of the contained pages */
            OcrPageType page = new OcrPageType();
            ocr.setPage(page);
            page.setWidth(800);
            page.setHeight(600);
            page.setMetrics(MetricsType.PX);

            /** Execute the webservice and download your result document: */
            ocrWebService.process(soapDocument).writeResult(targetDocument);
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
