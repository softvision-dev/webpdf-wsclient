package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.webservice.soap.ConverterWebService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the error mapping of SOAP webservices: failures the wsclient already classified keep their type and code, and
 * an inaccessible WSDL is reported as {@link ResultException} instead of an unchecked exception.
 */
public class SoapWebServiceTest {

    @Test
    public void testProcessKeepsAuthenticationFailure() throws Exception {
        AuthProvider failingProvider = new AuthProvider() {
            @Override
            public @NotNull AuthMaterial provide(@NotNull Session session) throws AuthResultException {
                throw new AuthResultException(new ClientResultException(Error.AUTHENTICATION_FAILURE));
            }

            @Override
            public @NotNull AuthMaterial refresh(Session session) throws AuthResultException {
                throw new AuthResultException(new ClientResultException(Error.SESSION_REFRESH_FAILURE));
            }
        };
        try (SoapWebServiceSession session = SessionFactory.createInstance(
                new SessionContext(WebServiceProtocol.SOAP,
                        LocalWebPdfServer.createUrl("http://127.0.0.1:1/webPDF/")), failingProvider)) {
            ConverterWebService<SoapWebServiceDocument> webService =
                    WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            try (SoapWebServiceDocument document = session.createDocument()) {
                AuthResultException exception = assertThrows(AuthResultException.class,
                        () -> webService.process(document),
                        "The authentication failure should not have been re-wrapped.");
                assertEquals(Error.AUTH_ERROR, exception.getClientError(), "The error code should have been kept.");
            }
        }
    }

    @Test
    public void testInaccessibleWsdlIsReportedAsResultException() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             SoapWebServiceSession session = SessionFactory.createInstance(
                     new SessionContext(WebServiceProtocol.SOAP, server.getUrl()))) {
            // The server answers the WSDL request with 404.
            session.setUseLocalWsdl(false);

            ClientResultException exception = assertThrows(ClientResultException.class,
                    () -> WebServiceFactory.createInstance(session, WebServiceType.CONVERTER),
                    "An inaccessible WSDL should be reported as ResultException.");
            assertEquals(Error.WSDL_INVALID_FILE, exception.getClientError(), "Unexpected error code.");
            assertNotNull(exception.getCause(), "The JAX-WS failure should have been kept as cause.");
        }
    }

}
