package net.webpdf.examples.soap.signature;

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
import net.webpdf.wsclient.webservice.soap.SignatureWebService;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

/**
 * Here you will find a usage example for the webPDF {@link SignatureWebService} demonstrating how you can add a
 * signature to a PDF document using the SOAP API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class AddSignaturesToPdf {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final File signatureImage = new File("The path to some image file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link SignatureWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link SignatureWebService} interface type.</li>
     * <li>The parameterization required to add a signature to a PDF document.</li>
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
             * (using {@link WebServiceType.SIGNATURE} here): */
            SignatureWebService<SoapDocument> signatureWebService =
                    session.createWebServiceInstance(WebServiceType.SIGNATURE);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            SignatureType signature = signatureWebService.getOperationParameters();

            /** Parameterize your webservice call.
             * For this example, we will entirely prohibit further editing of the document.
             */
            SignatureType.Add add = new SignatureType.Add();
            signature.setAdd(add);
            add.setReason("webPDF wsclient sample");
            add.setLocation("Main Street, Anytown, USA");
            add.setContact("Any Company");
            add.setCertificationLevel(CertificationLevelType.NO_CHANGES);
            add.setKeyName("Generic self-signed certificate");

            /**
             * Next we shall position our signature on page 1 of the document:
             */
            SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
            add.setAppearance(appearance);
            appearance.setPage(1);
            SignaturePositionType position = new SignaturePositionType();
            position.setX(5.0f);
            position.setY(5.0f);
            position.setWidth(80.0f);
            position.setHeight(15.0f);
            appearance.setPosition(position);

            /**
             * And will then add textual and image contents to the visual appearance:
             */
            SignatureImageType image = new SignatureImageType();
            SignatureFileDataType imageData = new SignatureFileDataType();
            imageData.setValue(Files.readAllBytes(signatureImage.toPath()));
            image.setData(imageData);
            image.setOpacity(40);
            appearance.setImage(image);
            appearance.setIdentifier("John Doe");

            /** Execute the webservice and "download" your result document: */
            signatureWebService.process(soapDocument).writeResult(targetDocument);
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
