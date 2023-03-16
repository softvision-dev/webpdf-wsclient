package net.webpdf.examples.soap.toolbox;

import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.schema.operation.DescriptionType;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ToolboxRestWebService;
import net.webpdf.wsclient.webservice.soap.ToolboxWebService;

import java.io.File;
import java.net.URL;

/**
 * Here you will find a usage example for the webPDF {@link ToolboxRestWebService} demonstrating how you can change the
 * description of your PDF document using the SOAP API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class ChangePdfDescription {

    /**
     * Adapt the following fields accordingly:
     */
    private static final File sourceDocument = new File("The path to your source file");
    private static final File targetDocument = new File("The path to your target file");
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link ToolboxWebService} shall demonstrate:
     * <ul>
     * <li>The creation of a simple {@link SoapSession}</li>
     * <li>The creation of a {@link ToolboxWebService} interface type.</li>
     * <li>The parameterization required to change the description of a PDF document.</li>
     * <li>The handling of the source and result {@link SoapDocument} for a {@link SoapSession}.</li>
     * </ul>
     *
     * <b>Be aware:</b> You have to adapt the fields of this class accordingly, otherwise this example is not runnable.
     * </p>
     */
    public static void main(String... args) throws Exception{
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
             * (using {@link WebServiceType.TOOLBOX} here): */
            ToolboxWebService<SoapDocument> toolboxWebService =
                    session.createWebServiceInstance(WebServiceType.TOOLBOX);

            /** Initialize and add a toolbox description parameter root: */
            DescriptionType description = new DescriptionType();
            toolboxWebService.getOperationParameters().add(description);

            /**
             * Adapt the documents description:
             */
            description.setAllowEmptyValues(true);
            description.setAuthor("Author1;Author2");
            description.setCreator("Creator");
            description.setKeywords("Keyword1;Keyword2;Keyword3");
            description.setProducer("Producer");
            description.setSubject("Subject");
            description.setTitle("Title");

            /** Execute the webservice and "download" your result document: */
            toolboxWebService.process(soapDocument).writeResult(targetDocument);
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
