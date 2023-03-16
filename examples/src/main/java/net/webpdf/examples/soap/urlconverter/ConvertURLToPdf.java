package net.webpdf.examples.soap.urlconverter;


import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.UrlConverterWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link UrlConverterWebService} demonstrating how you can
 * convert a URL to a PDF document using the SOAP API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ConvertURLToPdf {

    /**
     * Adapt the following fields accordingly:
     */
    private static final String sourceURL = "Some URL to convert";
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link UrlConverterWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link UrlConverterWebService} interface type.</li>
     * <li>The parameterization required to convert a URL to a PDF document.</li>
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

        /** Please take note, that the {@link SoapSession} is used in a try-with-resources
         * statement. It is closeable and should be closed. */
        try (
                /** Initialize the session with the webPDF Server (using SOAP): */
                SoapSession<SoapDocument> session = SessionFactory.createInstance(sessionContext)
        ) {
            /** Instantiate the {@link WebService} interface type you want to call.
             * (using {@link WebServiceType.SIGNATURE} here): */
            UrlConverterWebService<SoapDocument> urlConverterWebService =
                    session.createWSInstance(WebServiceType.URLCONVERTER);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            UrlConverterType urlConverter = urlConverterWebService.getOperationParameters();

            /**
             * Parameterize the webservice call.
             * For this example we shall select a URL and shall define the dimensions of the created pages.
             */
            urlConverter.setUrl(sourceURL);
            UrlConverterPageType page = new UrlConverterPageType();
            urlConverter.setPage(page);
            page.setMetrics(MetricsType.MM);
            page.setWidth(800);
            page.setHeight(600);
            page.setTop(10);
            page.setRight(15);
            page.setBottom(10);
            page.setLeft(15);

            /** Execute the webservice and "download" your result document:
             * The URL converter currently is the only webservice that does not require a source document,
             * so you should call the parameterless process method to execute it.
             * Please take note, that the {@link SoapDocument} is used in a try-with-resources
             * statement. It is closeable and should be closed.*/
            try (SoapDocument result = urlConverterWebService.process()) {
                result.writeResult(targetDocument);
            }
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
