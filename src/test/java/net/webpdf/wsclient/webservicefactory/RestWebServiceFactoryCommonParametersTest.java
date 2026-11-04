package net.webpdf.wsclient.webservicefactory;

import net.webpdf.wsclient.openapi.OperationBaseToolbox;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import net.webpdf.wsclient.webservice.rest.ToolboxRestWebService;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the REST side of {@link WebServiceFactory#createInstance(net.webpdf.wsclient.session.Session, StreamSource)}
 * for the webservice independent operation parameters, and the REST toolbox parameter handling. A
 * {@link LocalWebPdfServer} stands in for the webPDF server, as a REST session requires a login.
 */
public class RestWebServiceFactoryCommonParametersTest {

    @Test
    public void testFactoryCommonParametersFromStream() throws Exception {
        String json = "{\"billing\":{\"customerCode\":\"customer\",\"userName\":\"billingUser\"},"
                + "\"password\":{\"open\":\"openPwd\",\"permission\":\"permissionPwd\"},"
                + "\"settings\":{\"compress\":{}},"
                + "\"converter\":{\"pages\":\"1\"}}";
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server);
             StringReader stringReader = new StringReader(json)) {
            ConverterRestWebService<RestWebServiceDocument> webService =
                    WebServiceFactory.createInstance(session, new StreamSource(stringReader));

            assertEquals("1", webService.getOperationParameters().getPages(),
                    "The converter parameters should have been read.");
            assertNotNull(webService.getBilling(), "The billing element should have been applied.");
            assertEquals("customer", webService.getBilling().getCustomerCode(),
                    "Value of customerCode is unexpected.");
            assertEquals("billingUser", webService.getBilling().getUserName(),
                    "Value of userName is unexpected.");
            assertNotNull(webService.getPassword(), "The password element should have been applied.");
            assertEquals("openPwd", webService.getPassword().getOpen(), "Value of open is unexpected.");
            assertEquals("permissionPwd", webService.getPassword().getPermission(),
                    "Value of permission is unexpected.");
            assertNotNull(webService.getSettings(), "The settings element should have been applied.");
            assertNotNull(webService.getSettings().getCompress(), "The compress element should have been applied.");
        }
    }

    @Test
    public void testToolboxAcceptsItsOwnOperationList() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server)) {
            ToolboxRestWebService<RestWebServiceDocument> webService = new ToolboxRestWebService<>(session);
            List<OperationBaseToolbox> operations = webService.getOperationParameters();
            OperationBaseToolbox rotate = new OperationBaseToolbox();
            operations.add(rotate);

            webService.setOperationParameters(webService.getOperationParameters());

            assertEquals(1, webService.getOperationParameters().size(),
                    "Passing the current operation list should keep its operations.");
            assertSame(rotate, webService.getOperationParameters().get(0), "The operation should have been kept.");
        }
    }

    private static RestWebServiceSession createSession(LocalWebPdfServer server) throws Exception {
        return SessionFactory.createInstance(new SessionContext(WebServiceProtocol.REST, server.getUrl()),
                new UserAuthProvider("admin", "secret"));
    }

}
