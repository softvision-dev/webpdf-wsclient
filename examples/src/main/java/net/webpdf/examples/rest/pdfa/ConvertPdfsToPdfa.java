package net.webpdf.examples.rest.pdfa;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.OperationConvertPdfa;
import net.webpdf.wsclient.openapi.OperationPdfa;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.PdfaRestWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link PdfaRestWebService} demonstrating how you can a PDF document
 * to a PDF/A document using the REST API.<br>
 * <b>Be aware:</b> For this to work your source document must meet the requirements of the PDF/A standard of your
 * choice and not all documents will meet the criteria of each PDF/A conformance level.<br>
 * You should expect failures, should you convert some document, that you did not check for conformance to the
 * standard.<br>
 * (To mention it: The {@link PdfaRestWebService} can check the PDF/A conformance of a document for you.)
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ConvertPdfsToPdfa {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link PdfaRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link PdfaRestWebService} interface type.</li>
     * <li>The parameterization required to convert a PDF document to a PDF/A document.</li>
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
             * (using {@link WebServiceType.PDFA} here): */
            PdfaRestWebService<RestDocument> pdfaWebService =
                    session.createWSInstance(WebServiceType.PDFA);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OperationPdfa pdfa = pdfaWebService.getOperationParameters();

            /** Parameterize your webservice call.
             * In this example we want to convert the document to conformance level "3b".
             */
            OperationConvertPdfa convert = new OperationConvertPdfa();
            pdfa.setConvert(convert);
            convert.setLevel(OperationConvertPdfa.LevelEnum._3B);
            convert.setImageQuality(90);
            /**
             * We want to receive an error report in file form, should our PDF document not meet the conformance
             * criteria, but don't require a report upon success:
             */
            convert.setSuccessReport(OperationConvertPdfa.SuccessReportEnum.NONE);
            convert.setErrorReport(OperationConvertPdfa.ErrorReportEnum.FILE);

            /** Execute the webservice and download your result document: */
            pdfaWebService.process(restDocument).downloadDocument(targetDocument);
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
