package net.webpdf.wsclient;

import com.auth0.client.auth.AuthAPI;
import com.auth0.net.TokenRequest;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.config.IntegrationTestConfig;
import net.webpdf.wsclient.testsuite.integration.annotations.OAuthTestCondition;
import net.webpdf.wsclient.testsuite.integration.annotations.OAuthProviderSelection;
import net.webpdf.wsclient.testsuite.config.oauth.Auth0Config;
import net.webpdf.wsclient.testsuite.config.oauth.AzureConfig;
import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.token.OAuthToken;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.PdfaRestWebService;
import net.webpdf.wsclient.webservice.soap.PdfaWebService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class OauthTokenIntegrationTest {

    private final TestResources testResources = new TestResources(OauthTokenIntegrationTest.class);
    public TestServer testServer = new TestServer();

    /**
     * <p>
     * Use an Auth0 authorization provider to create a webPDF wsclient {@link RestSession}.
     * </p>
     * <p>
     * This serves as an example implementation how such an OAuth session can be created.
     * </p>
     * <p>
     * <b>Be aware:</b><br>
     * - The hereby used Auth0 authorization provider must be known to your webPDF server. (server.xml)<br>
     * - This is using the integrationTest config defined in "config/integrationTestConfig.json".
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Test
    @OAuthTestCondition(provider = OAuthProviderSelection.AUTH_0)
    public void testRestAuth0TokenTest() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            Auth0Config auth0Config = IntegrationTestConfig.getInstance().getAuth0Config();

            // Receive Auth0 access token and provide it to the RestSession:
            assertDoesNotThrow(() -> session.login(
                    // Implement the Auth0 TokenProvider
                    () -> {
                        // Request an access token from the Auth0 authorization provider:
                        AuthAPI auth = new AuthAPI(
                                auth0Config.getAuthority(),
                                auth0Config.getClientID(),
                                auth0Config.getClientSecret()
                        );
                        TokenRequest tokenRequest = auth.requestToken(
                                auth0Config.getAudience()
                        );

                        // Create and return the webPDF wsclient access Token.
                        return new OAuthToken(tokenRequest.execute().getAccessToken());
                    })
            );

            // Execute requests to webPDF webservices using the access token:
            executeWSRequest(session);
        }
    }

    /**
     * <p>
     * Use an Azure authorization provider to create a webPDF wsclient {@link RestSession}.
     * </p>
     * <p>
     * This serves as an example implementation how such an OAuth session can be created.
     * </p>
     * <p>
     * <b>Be aware:</b><br>
     * - The hereby used Azure authorization provider must be known to your webPDF server. (server.xml)<br>
     * - This is using the integrationTest config defined in "config/integrationTestConfig.json".
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Test
    @OAuthTestCondition(provider = OAuthProviderSelection.AZURE)
    public void testRestAzureTokenTest() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            AzureConfig azureConfig = IntegrationTestConfig.getInstance().getAzureConfig();

            // Receive Azure access token and provide it to the RestSession:
            assertDoesNotThrow(
                    () -> session.login(
                            // Implement the Azure TokenProvider
                            () -> {
                                // Request an access token from the Azure authorization provider:
                                ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                                                azureConfig.getClientID(),
                                                ClientCredentialFactory.createFromSecret(
                                                        azureConfig.getClientSecret()
                                                ))
                                        .authority(
                                                azureConfig.getAuthority()
                                        )
                                        .build();
                                ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                                                Collections.singleton(
                                                        azureConfig.getScope()
                                                ))
                                        .build();
                                CompletableFuture<IAuthenticationResult> future =
                                        app.acquireToken(clientCredentialParam);
                                IAuthenticationResult authenticationResult =
                                        assertDoesNotThrow(() -> future.get());

                                // Create and return the webPDF wsclient access Token.
                                return new OAuthToken(authenticationResult.accessToken());
                            })
            );

            // Execute requests to webPDF webservices using the access token:
            executeWSRequest(session);
        }
    }

    /**
     * <p>
     * Use an Auth0 authorization provider to create a webPDF wsclient {@link SoapSession}.
     * </p>
     * <p>
     * This serves as an example implementation how such an OAuth session can be created.
     * </p>
     * <p>
     * <b>Be aware:</b><br>
     * - The hereby used Auth0 authorization provider must be known to your webPDF server. (server.xml)<br>
     * - This is using the integrationTest config defined in "config/integrationTestConfig.json".
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Test
    @OAuthTestCondition(provider = OAuthProviderSelection.AUTH_0)
    public void testSOAPAuth0TokenTest() throws Exception {
        try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL))) {
            Auth0Config auth0Config = IntegrationTestConfig.getInstance().getAuth0Config();

            assertDoesNotThrow(() -> session.setCredentials(
                    // Implement the Auth0 TokenProvider
                    () -> {
                        // Request an access token from the Auth0 authorization provider:
                        AuthAPI auth = new AuthAPI(
                                auth0Config.getAuthority(),
                                auth0Config.getClientID(),
                                auth0Config.getClientSecret()
                        );
                        TokenRequest tokenRequest = auth.requestToken(
                                auth0Config.getAudience()
                        );

                        // Create and return the webPDF wsclient access Token.
                        return new OAuthToken(tokenRequest.execute().getAccessToken());
                    })
            );

            // Execute requests to webPDF webservices using the access token:
            executeWSRequest(session);
        }
    }

    /**
     * <p>
     * Use an Azure authorization provider to create a webPDF wsclient {@link SoapSession}.
     * </p>
     * <p>
     * This serves as an example implementation how such an OAuth session can be created.
     * </p>
     * <p>
     * <b>Be aware:</b><br>
     * - The hereby used Azure authorization provider must be known to your webPDF server. (server.xml)<br>
     * - This is using the integrationTest config defined in "config/integrationTestConfig.json".
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Test
    @OAuthTestCondition(provider = OAuthProviderSelection.AZURE)
    public void testSOAPAzureTokenTest() throws Exception {
        try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL))) {
            AzureConfig azureConfig = IntegrationTestConfig.getInstance().getAzureConfig();

            assertDoesNotThrow(() -> session.setCredentials(
                    // Implement the Azure TokenProvider
                    () -> {
                        // Request an access token from the Azure authorization provider:
                        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                                        azureConfig.getClientID(),
                                        ClientCredentialFactory.createFromSecret(
                                                azureConfig.getClientSecret()
                                        ))
                                .authority(
                                        azureConfig.getAuthority()
                                )
                                .build();
                        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                                        Collections.singleton(
                                                azureConfig.getScope()
                                        ))
                                .build();
                        CompletableFuture<IAuthenticationResult> future =
                                app.acquireToken(clientCredentialParam);
                        IAuthenticationResult authenticationResult =
                                assertDoesNotThrow(() -> future.get());

                        // Create and return the webPDF wsclient access Token.
                        return new OAuthToken(authenticationResult.accessToken());
                    })
            );

            // Execute requests to webPDF webservices using the access token:
            executeWSRequest(session);
        }
    }

    /**
     * Executes an exemplary call to the PDF/A webservice and checks whether a result is created and received.
     *
     * @param session The {@link RestSession} to use.
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    private void executeWSRequest(@NotNull RestSession<RestDocument> session) throws Exception {
        PdfaRestWebService<RestDocument> webService =
                WebServiceFactory.createInstance(session, WebServiceType.PDFA);

        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        File fileOut = testResources.getTempFolder().newFile();

        webService.setDocument(session.getDocumentManager().uploadDocument(file));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        webService.getOperation().setConvert(new PdfaType.Convert());
        webService.getOperation().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
        webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
        webService.getOperation().getConvert().setImageQuality(90);

        RestDocument restDocument = webService.process();
        assertNotNull(restDocument);
        String documentID = restDocument.getDocumentId();
        assertNotNull(documentID);
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
            session.getDocumentManager().downloadDocument(documentID, fileOutputStream);
        }
        assertTrue(fileOut.exists());
    }

    /**
     * Executes an exemplary call to the PDF/A webservice and checks whether a result is created and received.
     *
     * @param session The {@link SoapSession} to use.
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    private void executeWSRequest(@NotNull SoapSession<SoapDocument> session) throws Exception {
        PdfaWebService<SoapDocument> webService =
                WebServiceFactory.createInstance(session, WebServiceType.PDFA);

        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        File fileOut = testResources.getTempFolder().newFile();

        webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

        assertNotNull(webService.getOperation(),
                "Operation should have been initialized");
        webService.getOperation().setConvert(new PdfaType.Convert());
        webService.getOperation().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
        webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
        webService.getOperation().getConvert().setImageQuality(90);

        try (SoapDocument soapDocument = webService.process()) {
            assertNotNull(soapDocument);
            assertTrue(fileOut.exists());
        }
    }

}
