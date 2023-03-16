package net.webpdf.examples.rest.signature;

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
import net.webpdf.wsclient.webservice.rest.SignatureRestWebService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link SignatureRestWebService} demonstrating how you can add a
 * signature to a PDF document using the REST API.
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
     * This usage example for the webPDF {@link SignatureRestWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link RestSession}</li>
     * <li>The creation of a {@link SignatureRestWebService} interface type.</li>
     * <li>The parameterization required to add a signature to a PDF document.</li>
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
             * (using {@link WebServiceType.SIGNATURE} here): */
            SignatureRestWebService<RestDocument> signatureWebService =
                    session.createWebServiceInstance(WebServiceType.SIGNATURE);

            /** Upload your document to the REST sessions´s document storage.
             * You may upload/download/delete/rename/etc. as many {@link RestDocument}s as you wish and at any time,
             * and you may want to use the {@link RestSession}´s {@link DocumentManager} to assist you in such a complex
             * scenario - but for this simple usage example using the following shortcut shall suffice.*/
            RestDocument restDocument = session.uploadDocument(sourceDocument);

            /** Request the parameter tree root, to begin parameterizing your webservice call: */
            OperationSignature signature = signatureWebService.getOperationParameters();

            /** Parameterize your webservice call.
             * For this example, we will entirely prohibit further editing of the document. */
            OperationAddSignature add = new OperationAddSignature();
            signature.setAdd(add);
            add.setReason("webPDF wsclient sample");
            add.setLocation("Main Street, Anytown, USA");
            add.setContact("Any Company");
            add.setCertificationLevel(OperationAddSignature.CertificationLevelEnum.NOCHANGES);
            add.setKeyName("Generic self-signed certificate");

            /** Next we shall position our signature on page 1 of the document: */
            OperationAppearanceAdd appearance = new OperationAppearanceAdd();
            add.setAppearance(appearance);
            appearance.setPage(1);
            OperationSignaturePosition position = new OperationSignaturePosition();
            position.setX(5.0f);
            position.setY(5.0f);
            position.setWidth(80.0f);
            position.setHeight(15.0f);
            appearance.setPosition(position);

            /** And will then add textual and image contents to the visual appearance: */
            OperationSignatureImage image = new OperationSignatureImage();
            OperationSignatureFileData imageData = new OperationSignatureFileData();
            imageData.setValue(FileUtils.readFileToByteArray(signatureImage));
            image.setData(imageData);
            image.setOpacity(40);
            appearance.setImage(image);
            appearance.setIdentifier("John Doe");

            /** Execute the webservice and download your result document: */
            signatureWebService.process(restDocument).downloadDocument(targetDocument);
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
