package net.webpdf.examples.rest.administration;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.openapi.AdminTrustStoreKeyStore;
import net.webpdf.wsclient.openapi.ServerConfigServer;
import net.webpdf.wsclient.openapi.ServerConfigTruststoreServer;
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

/**
 * Here you will find a usage example for the webPDF {@link AdministrationManager} demonstrating how you can
 * update the configuration of the server using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class UpdateConfiguration {
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * Adapt the following fields accordingly:
     */
    private static final File truststoreFile = new File("The path to your source file");

    /**
     * <p>
     * This usage example for the webPDF {@link AdministrationManager} shall demonstrate:
     * <ul>
     * <li>how to get a configuration</li>
     * <li>how to update the configuration</li>
     * <li>how to fetch the updated configuration</li>
     * <li>how to remove the updated configuration values</li>
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

            /** Get {@link ServerConfigServer} from {@link AdministrationManager} */
            ServerConfigServer serverConfiguration = administrationManager.getServerConfiguration();

            /** Get currently set {@link AdminTrustStoreKeyStore} */
            AdminTrustStoreKeyStore truststore = administrationManager.getTrustStoreKeyStore();

            /** Create a new {@link ServerConfigTruststoreServer} to use as Parameter for the update */
            ServerConfigTruststoreServer truststoreServer = new ServerConfigTruststoreServer();
            truststoreServer.setFile("myTrustStore.jks");
            truststoreServer.password("webpdf");

            /** replace the current config with the new values */
            serverConfiguration.setTruststore(truststoreServer);

            /** create a new {@link AdminTrustStoreKeyStore} */
            truststore = new AdminTrustStoreKeyStore();
            truststore.setKeyStoreContent(DatatypeConverter.printBase64Binary(
                    FileUtils.readFileToByteArray(truststoreFile)
            ));

            /**
             * update the {@link AdminTrustStoreKeyStore} and {@link ServerConfigServer} in the
             * {@link AdministrationManager}
             */
            administrationManager.setTrustStoreKeyStore(truststore);
            administrationManager.updateServerConfiguration(serverConfiguration);

            /**
             * fetch the newly set configuration and keystore from server
             * <b>note: getServerConfiguration would only return the old configuration not the updated one.</b>
             */
            serverConfiguration = administrationManager.fetchServerConfiguration();
            truststore = administrationManager.fetchTrustStoreKeyStore();

            /** remove the keystore from configuration */
            serverConfiguration.setTruststore(null);
            truststore.setKeyStoreContent(null);
            administrationManager.updateServerConfiguration(serverConfiguration);
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