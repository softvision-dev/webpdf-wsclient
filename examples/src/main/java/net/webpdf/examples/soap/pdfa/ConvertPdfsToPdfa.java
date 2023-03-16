package net.webpdf.examples.soap.pdfa;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaSuccessReportType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.PdfaWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link PdfaWebService} demonstrating how you can a PDF document
 * to a PDF/A document using the SOAP API.<br>
 * <b>Be aware:</b> For this to work your source document must meet the requirements of the PDF/A standard of your
 * choice and not all documents will meet the criteria of each PDF/A conformance level.<br>
 * You should expect failures, should you convert some document, that you did not check for conformance to the
 * standard.<br>
 * (To mention it: The {@link PdfaWebService} can check the PDF/A conformance of a document for you.)
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
     * This usage example for the webPDF {@link PdfaWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link PdfaWebService} interface type.</li>
     * <li>The parameterization required to convert a PDF document to a PDF/A document.</li>
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
             * (using {@link WebServiceType.PDFA} here): */
            PdfaWebService<SoapDocument> pdfaWebService =
                    session.createWebServiceInstance(WebServiceType.PDFA);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            PdfaType pdfa = pdfaWebService.getOperationParameters();

            /** Parameterize your webservice call.
             * In this example we want to convert the document to conformance level "3b".
             */
            PdfaType.Convert convert = new PdfaType.Convert();
            pdfa.setConvert(convert);
            convert.setLevel(PdfaLevelType.LEVEL_3B);
            convert.setImageQuality(90);
            /**
             * We want to receive an error report in file form, should our PDF document not meet the conformance
             * criteria, but don't require a report upon success:
             */
            convert.setSuccessReport(PdfaSuccessReportType.NONE);
            convert.setErrorReport(PdfaErrorReportType.FILE);

            /** Execute the webservice and "download" your result document: */
            pdfaWebService.process(soapDocument).writeResult(targetDocument);
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
