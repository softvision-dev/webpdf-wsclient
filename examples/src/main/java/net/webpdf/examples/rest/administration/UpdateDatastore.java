package net.webpdf.examples.rest.administration;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.openapi.AdminFileGroupDataStore;
import net.webpdf.wsclient.openapi.AdminLogoFileDataStore;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.administration.AdministrationManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Here you will find a usage example for the webPDF {@link AdministrationManager} demonstrating how you can
 * update a file in the datastore of the server using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class UpdateDatastore {
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * Adapt the following fields accordingly:
     */
    private static final File logoFile = new File("The path to your source file");

    /**
     * <p>
     * This usage example for the webPDF {@link AdministrationManager} shall demonstrate:
     * <ul>
     * <li>how to get a file from the datastore</li>
     * <li>how to replace a file in the datastore</li>
     * <li>how to delete a file from the datastore</li>
     * </ul>
     *
     * <b>Be aware:</b> You have to adapt the fields of this class accordingly, otherwise this example is not runnable.
     * </p>
     * <p>
     */
    public static void main(String... args) throws Exception {
        /** Initialize a simple {@link SessionContext}.*/
        SessionContext sessionContext = new SessionContext(
                WebServiceProtocol.REST,
                new URL(webPDFServerURL)
        );

        try (
                /** Initialize the session with the webPDF Server (using REST): */
                RestSession<RestDocument> session = SessionFactory.createInstance(
                        sessionContext,
                        new UserAuthProvider("admin", "admin")
                )
        ) {
            /** Get {@link AdministrationManager} from {@link RestSession} */
            AdministrationManager<RestDocument> administrationManager = session.getAdministrationManager();

            /** get the currently set logo file */
            AdminLogoFileDataStore currentLogo = (AdminLogoFileDataStore) administrationManager.getDatastore(
                    AdminFileGroupDataStore.LOGO
            );

            /** initialize a {@link AdminLogoFileDataStore} parameter to update with */
            AdminLogoFileDataStore fileDataStore = new AdminLogoFileDataStore();

            /** Set the file contents and group you want to update. In this example we will update the logo file. */
            fileDataStore.setFileContent(DatatypeConverter.printBase64Binary(
                    FileUtils.readFileToByteArray(logoFile)
            ));
            fileDataStore.setFileGroup(AdminFileGroupDataStore.LOGO);

            /** update the datastore */
            administrationManager.updateDatastore(fileDataStore);

            /** delete the currently set logo file */
            administrationManager.deleteDatastore(AdminFileGroupDataStore.LOGO);
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