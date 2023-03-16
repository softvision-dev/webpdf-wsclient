package net.webpdf.examples.soap.barcode;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.BarcodeWebService;
import net.webpdf.wsclient.webservice.WebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link BarcodeWebService} demonstrating the creation of a barcode
 * for a PDF document using the SOAP API.
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
     * This usage example for the webPDF {@link BarcodeWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link BarcodeWebService} interface type.</li>
     * <li>The parameterization required to add barcodes to pages of a PDF document.</li>
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
             * (using {@link WebServiceType.BARCODE} here): */
            BarcodeWebService<SoapDocument> barcodeWebService =
                    session.createWSInstance(WebServiceType.BARCODE);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            BarcodeType barcode = barcodeWebService.getOperationParameters();

            /** Order the webservice to add barcodes to the source document.
             * You may add multiple barcodes to the hereby created element: */
            BarcodeType.Add add = new BarcodeType.Add();
            barcode.setAdd(add);

            /** Select and parameterize the barcode type, that you want to add to your document.
             * You may select any of the types extending the {@link BaseBarcodeType} here. */
            AztecBarcodeType aztec = new AztecBarcodeType();
            add.getAztec().add(aztec);
            aztec.setCharset("utf-8");
            aztec.setMargin(5);
            aztec.setPages("1-5");
            aztec.setRotation(90);
            aztec.setValue("http://www.softvision.de");
            aztec.setErrorCorrection(50);
            aztec.setLayers(10);

            /** Position the barcode on the selected pages: */
            RectangleType position = new RectangleType();
            aztec.setPosition(position);
            position.setMetrics(MetricsType.PX);
            position.setX(15);
            position.setY(20);
            position.setWidth(50);
            position.setHeight(50);
            position.setCoordinates(CoordinatesType.USER);

            /** Execute the webservice and "download" your result document: */
            barcodeWebService.process(soapDocument).writeResult(targetDocument);
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
