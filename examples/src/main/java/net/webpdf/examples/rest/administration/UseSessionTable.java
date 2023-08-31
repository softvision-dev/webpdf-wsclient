package net.webpdf.examples.rest.administration;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.*;
import net.webpdf.wsclient.openapi.SessionTable;
import net.webpdf.wsclient.openapi.SessionTableEntry;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.administration.AdministrationManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;

import java.net.URL;
import java.util.List;

/**
 * Here you will find a usage example for the webPDF {@link AdministrationManager} demonstrating how you can
 * use the session table of the server using the REST API.
 */
@SuppressWarnings({"DuplicatedCode", "DanglingJavadoc", "unused"})
public class UseSessionTable {
    private static final String webPDFServerURL = ("http://localhost:8080/webPDF/");

    /**
     * <p>
     * This usage example for the webPDF {@link AdministrationManager} shall demonstrate:
     * <ul>
     * <li>how to get the session table</li>
     * <li>close a specific session</li>
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

            /** Get {@link SessionTable} from {@link AdministrationManager} */
            SessionTable sessionTable = administrationManager.fetchSessionTable();

            /** get the {@link List<SessionTableEntry>} */
            List<SessionTableEntry> sessionTableEntries = sessionTable.getSessionList();

            /** close a specific session */
            administrationManager.closeSession(sessionTableEntries.get(0).getSessionId());
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