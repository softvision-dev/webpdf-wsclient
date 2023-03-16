package net.webpdf.examples.rest.urlconverter;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.OperationUrlConverter;
import net.webpdf.wsclient.openapi.OperationUrlConverterPage;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.UrlConverterRestWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link UrlConverterRestWebService} demonstrating how you can
 * convert a URL to a PDF document using the REST API.
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
     * This usage example for the webPDF {@link UrlConverterRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link UrlConverterRestWebService} interface type.</li>
     * <li>The parameterization required to convert a URL to a PDF document.</li>
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
             * (using {@link WebServiceType.URLCONVERTER} here): */
            UrlConverterRestWebService<RestDocument> urlConverterWebService =
                    session.createWSInstance(WebServiceType.URLCONVERTER);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OperationUrlConverter urlConverter = urlConverterWebService.getOperationParameters();

            /**
             * Parameterize the webservice call.
             * For this example we shall select a URL and shall define the dimensions of the created pages.
             */
            urlConverter.setUrl(sourceURL);
            OperationUrlConverterPage page = new OperationUrlConverterPage();
            urlConverter.setPage(page);
            page.setMetrics(OperationUrlConverterPage.MetricsEnum.MM);
            page.setWidth(800);
            page.setHeight(600);
            page.setTop(10);
            page.setRight(15);
            page.setBottom(10);
            page.setLeft(15);

            /** Execute the webservice and download your result document:
             * The URL converter currently is the only webservice that does not require a source document,
             * so you should call the parameterless process method to execute it. */
            urlConverterWebService.process().downloadDocument(targetDocument);
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
