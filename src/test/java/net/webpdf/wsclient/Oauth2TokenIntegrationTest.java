package net.webpdf.wsclient;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.TokenRequest;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.openapi.OperationConvertPdfa;
import net.webpdf.wsclient.session.auth.material.token.OAuth2Token;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.config.TestConfig;
import net.webpdf.wsclient.testsuite.integration.annotations.OAuthTest;
import net.webpdf.wsclient.testsuite.integration.annotations.OAuthProviderSelection;
import net.webpdf.wsclient.testsuite.config.integration.oauth.Auth0Config;
import net.webpdf.wsclient.testsuite.config.integration.oauth.AzureConfig;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.PdfaRestWebService;
import net.webpdf.wsclient.webservice.soap.PdfaWebService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class Oauth2TokenIntegrationTest {

    private final TestResources testResources = new TestResources(Oauth2TokenIntegrationTest.class);
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
     * - The hereby used Auth0 authorization provider must be known to your webPDF server. (server.xml)
     * </p>
     */
    @Test
    @OAuthTest(provider = OAuthProviderSelection.AUTH_0)
    public void testRestAuth0TokenTest() {
        assertDoesNotThrow(() -> {
            Auth0Config auth0Config = TestConfig.getInstance().getIntegrationTestConfig().getAuth0Config();
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)),
                    // Implement the Auth0 TokenProvider
                    (auth0Session) -> {
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
                        try {
                            return new OAuth2Token(tokenRequest.execute().getAccessToken());
                        } catch (Auth0Exception ex) {
                            throw new AuthResultException(ex);
                        }
                    }
            )) {
                // Execute requests to webPDF webservices using the access token:
                executeWSRequest(session);
            }
        });
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
     * - The hereby used Azure authorization provider must be known to your webPDF server. (server.xml)
     * </p>
     */
    @Test
    @OAuthTest(provider = OAuthProviderSelection.AZURE)
    public void testRestAzureTokenTest() {
        AzureConfig azureConfig = TestConfig.getInstance().getIntegrationTestConfig().getAzureConfig();
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)),
                    // Implement the Azure TokenProvider
                    (azureSession) -> {
                        // Request an access token from the Azure authorization provider:
                        ConfidentialClientApplication app;
                        try {
                            app = ConfidentialClientApplication.builder(
                                            azureConfig.getClientID(),
                                            ClientCredentialFactory.createFromSecret(
                                                    azureConfig.getClientSecret()
                                            ))
                                    .authority(
                                            azureConfig.getAuthority()
                                    )
                                    .build();
                        } catch (MalformedURLException ex) {
                            throw new AuthResultException(ex);
                        }
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
                        return new OAuth2Token(authenticationResult.accessToken());
                    }
            )) {
                // Execute requests to webPDF webservices using the access token:
                executeWSRequest(session);
            }
        });
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
     * - The hereby used Auth0 authorization provider must be known to your webPDF server. (server.xml)
     * </p>
     */
    @Test
    @OAuthTest(provider = OAuthProviderSelection.AUTH_0)
    public void testSOAPAuth0TokenTest() {
        Auth0Config auth0Config = TestConfig.getInstance().getIntegrationTestConfig().getAuth0Config();
        assertDoesNotThrow(() -> {
            try (SoapSession<?> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)),
                    // Implement the Auth0 TokenProvider
                    (auth0Session) -> {
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
                        try {
                            return new OAuth2Token(tokenRequest.execute().getAccessToken());
                        } catch (Auth0Exception ex) {
                            throw new AuthResultException(ex);
                        }
                    }
            )) {
                // Execute requests to webPDF webservices using the access token:
                executeWSRequest(session);
            }
        });
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
     * - The hereby used Azure authorization provider must be known to your webPDF server. (server.xml)
     * </p>
     */
    @Test
    @OAuthTest(provider = OAuthProviderSelection.AZURE)
    public void testSOAPAzureTokenTest() {
        AzureConfig azureConfig = TestConfig.getInstance().getIntegrationTestConfig().getAzureConfig();
        assertDoesNotThrow(() -> {
            try (SoapSession<?> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)),
                    // Implement the Azure TokenProvider
                    (azureSession) -> {
                        // Request an access token from the Azure authorization provider:
                        ConfidentialClientApplication app;
                        try {
                            app = ConfidentialClientApplication.builder(
                                            azureConfig.getClientID(),
                                            ClientCredentialFactory.createFromSecret(
                                                    azureConfig.getClientSecret()
                                            ))
                                    .authority(
                                            azureConfig.getAuthority()
                                    )
                                    .build();
                        } catch (MalformedURLException ex) {
                            throw new AuthResultException(ex);
                        }
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
                        return new OAuth2Token(authenticationResult.accessToken());
                    }
            )) {
                // Execute requests to webPDF webservices using the access token:
                executeWSRequest(session);
            }
        });
    }

    /**
     * Executes an exemplary call to the PDF/A webservice and checks whether a result is created and received.
     *
     * @param session The {@link RestSession} to use.
     */
    private void executeWSRequest(@NotNull RestSession<RestDocument> session) {
        assertDoesNotThrow(() -> {
            PdfaRestWebService<RestDocument> webService = session.createWSInstance(WebServiceType.PDFA);
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            OperationConvertPdfa pdfa = new OperationConvertPdfa();
            webService.getOperationParameters().setConvert(pdfa);
            pdfa.setLevel(OperationConvertPdfa.LevelEnum._3B);
            pdfa.setErrorReport(OperationConvertPdfa.ErrorReportEnum.MESSAGE);
            pdfa.setImageQuality(90);
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                webService.process(session.uploadDocument(file))
                        .downloadDocument(fileOutputStream);
            }
            assertTrue(fileOut.exists());
        });
    }

    /**
     * Executes an exemplary call to the PDF/A webservice and checks whether a result is created and received.
     *
     * @param session The {@link SoapSession} to use.
     */
    private void executeWSRequest(@NotNull SoapSession<?> session) {
        assertDoesNotThrow(() -> {
            PdfaWebService<SoapDocument> webService = session.createWSInstance(WebServiceType.PDFA);
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            webService.getOperationParameters().setConvert(new PdfaType.Convert());
            webService.getOperationParameters().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
            webService.getOperationParameters().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
            webService.getOperationParameters().getConvert().setImageQuality(90);
            try (SoapDocument soapDocument = webService.process(
                    session.createDocument(file.toURI()));
                 OutputStream out = new FileOutputStream(fileOut)) {
                soapDocument.writeResult(out);
                assertTrue(fileOut.exists());
            }
        });
    }

}