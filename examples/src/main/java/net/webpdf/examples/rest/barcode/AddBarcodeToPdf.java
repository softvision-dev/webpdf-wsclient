package net.webpdf.examples.rest.barcode;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.openapi.OperationAddBarcode;
import net.webpdf.wsclient.openapi.OperationAztecBarcode;
import net.webpdf.wsclient.openapi.OperationBarcode;
import net.webpdf.wsclient.openapi.OperationRectangle;
import net.webpdf.wsclient.schema.operation.BaseBarcodeType;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.BarcodeRestWebService;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link BarcodeRestWebService} demonstrating the creation of a
 * barcode for a PDF document using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "HttpUrlsUsage", "DanglingJavadoc", "unused"})
public class AddBarcodeToPdf {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link BarcodeRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link BarcodeRestWebService} interface type.</li>
     * <li>The parameterization required to add barcodes to pages of a PDF document.</li>
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
             * (using {@link WebServiceType.BARCODE} here): */
            BarcodeRestWebService<RestDocument> barcodeWebService =
                    session.createWebServiceInstance(WebServiceType.BARCODE);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OperationBarcode barcode = barcodeWebService.getOperationParameters();

            /** Order the webservice to add barcodes to the source document.
             * You may add multiple barcodes to the hereby created element: */
            OperationAddBarcode add = new OperationAddBarcode();
            barcode.setAdd(add);

            /** Select and parameterize the barcode type, that you want to add to your document.
             * You may select any of the types extending the {@link BaseBarcodeType} here. */
            OperationAztecBarcode aztec = new OperationAztecBarcode();
            add.getAztec().add(aztec);
            aztec.setCharset("utf-8");
            aztec.setMargin(5);
            aztec.setPages("1-5");
            aztec.setRotation(90);
            aztec.setValue("http://www.softvision.de");
            aztec.setErrorCorrection(50);
            aztec.setLayers(10);

            /** Position the barcode on the selected pages: */
            OperationRectangle position = new OperationRectangle();
            aztec.setPosition(position);
            position.setMetrics(OperationRectangle.MetricsEnum.PX);
            position.setX(15f);
            position.setY(20f);
            position.setWidth(50f);
            position.setHeight(50f);
            position.setCoordinates(OperationRectangle.CoordinatesEnum.USER);

            /** Execute the webservice and download your result document: */
            barcodeWebService.process(restDocument).downloadDocument(targetDocument);
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
