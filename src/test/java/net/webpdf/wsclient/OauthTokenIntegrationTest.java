package net.webpdf.wsclient;

import com.auth0.client.auth.AuthAPI;
import com.auth0.net.TokenRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.token.Token;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.PdfaRestWebService;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
     * - The values of the hereby defined claims must be adapted to the used client and authorization provider.
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Disabled("Adapt the values in 'auth0-client-id.json' first, otherwise this can not succeed.")
    @Test
    public void testAuth0TokenTest() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(TestServer.ServerType.LOCAL))) {

            // Receive Auth0 access token and provide it to the RestSession:
            assertDoesNotThrow(() -> session.login(
                    // Implement the Auth0 TokenProvider
                    () -> {
                        Auth0Config auth0Config = new Auth0Config(testResources.getResource(
                                "integration/files/auth0-client-id.json"));
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
                        // Important: Use the method "createAccessToken" instead of "createToken" here.
                        return Token.createAccessToken(tokenRequest.execute().getAccessToken());

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
     * - The values of the hereby defined claims must be adapted to the used client and authorization provider.
     * </p>
     *
     * @throws Exception Shall be thrown, when executing the request failed.
     */
    @Disabled("Adapt the values in 'azure-client-id.json' first, otherwise this can not succeed.")
    @Test
    public void testAzureTokenTest() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(TestServer.ServerType.LOCAL))) {

            // Receive Azure access token and provide it to the RestSession:
            assertDoesNotThrow(
                    () -> session.login(
                            // Implement the Azure TokenProvider
                            () -> {
                                AzureConfig azureConfig = new AzureConfig(testResources.getResource(
                                        "integration/files/azure-client-id.json"));
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
                                // Important: Use the method "createAccessToken" instead of "createToken" here.
                                return Token.createAccessToken(authenticationResult.accessToken());
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
     * <p>
     * Example implementation that reads the value of client claims from the given json config file.<br>
     * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
     * </p>
     */
    private static class ClientConfig {

        private final @NotNull JsonNode clientID;

        /**
         * <p>
         * Example implementation that reads the value of client claims from the given json config file.<br>
         * As said: This is just an example - Feel free to provide the claims in the way most fitting for your
         * application.<br>
         * <b>Be aware:</b> The values and names of the claims, that need to be defined are depending on the used
         * provider.<br>
         * <b>Be aware:</b> Said authorization provider must also be configured for your webPDF server. (server.xml)
         * </p>
         *
         * @param jsonConfigFile A json config file, that contains all claims required to request the access token from
         *                       the authorization provider. (You will find a short documentation of the claims there.)
         * @throws IOException Shall be thrown, if reading the config file failed.
         */
        public ClientConfig(@NotNull File jsonConfigFile) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            clientID = mapper.readTree(
                    FileUtils.readFileToString(jsonConfigFile, StandardCharsets.UTF_8));
        }

        /**
         * Reads the claim with the given name from the client config.
         *
         * @param claimName The name of the claim, that shall be read.
         * @return The value of the claim, that has been read.
         */
        public @NotNull String getClaim(@NotNull String claimName) {
            JsonNode claim = clientID.get(claimName);
            assertNotNull(claim);
            return claim.asText();
        }

    }

    /**
     * <p>
     * Example implementation that reads the value of Auth0 client claims from the given json config file.<br>
     * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
     * </p>
     * <p>
     * You can find a documentation of Auth0 access tokens here:
     * <a href="https://auth0.com/docs/secure/tokens/access-tokens/get-access-tokens">Request Auth0 access tokens</a>
     * </p>
     */
    private static class Auth0Config extends ClientConfig {

        /**
         * <p>
         * Example implementation that reads the value of Auth0 client claims from the given json config file.<br>
         * As said: This is just an example - Feel free to provide the claims in the way most fitting for your
         * application.<br>
         * <b>Be aware:</b> The values and names of the claims, that need to be defined are depending on the used
         * provider.<br>
         * <b>Be aware:</b> Said authorization provider must also be configured for your webPDF server. (server.xml)
         * </p>
         *
         * @param jsonConfigFile A json config file, that contains all claims required to request the access token from
         *                       the authorization provider. (You will find a short documentation of the claims there.)
         * @throws IOException Shall be thrown, if reading the config file failed.
         */
        public Auth0Config(@NotNull File jsonConfigFile) throws IOException {
            super(jsonConfigFile);
        }

        /**
         * Returns the value of the claim "authority".
         *
         * @return The value of the "authority" claim.
         */
        public @NotNull String getAuthority() {
            return getClaim("authority");
        }

        /**
         * Returns the value of the claim "clientId".
         *
         * @return The value of the "clientId" claim.
         */
        public @NotNull String getClientID() {
            return getClaim("clientId");
        }

        /**
         * Returns the value of the claim "clientSecret".
         *
         * @return The value of the "clientSecret" claim.
         */
        public @NotNull String getClientSecret() {
            return getClaim("clientSecret");
        }

        /**
         * Returns the value of the claim "audience".
         *
         * @return The value of the "audience" claim.
         */
        public @NotNull String getAudience() {
            return getClaim("audience");
        }

    }

    /**
     * <p>
     * Example implementation that reads the value of Azure client claims from the given json config file.<br>
     * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
     * </p>
     * <p>
     * You can find a documentation of Azure access tokens here:
     * <a href="https://docs.microsoft.com/en-us/azure/active-directory/develop/msal-acquire-cache-tokens">
     * Request Azure access tokens (MSAL)</a>
     * </p>
     */
    private static class AzureConfig extends ClientConfig {

        /**
         * <p>
         * Example implementation that reads the value of Azure client claims from the given json config file.<br>
         * As said: This is just an example - Feel free to provide the claims in the way most fitting for your
         * application.<br>
         * <b>Be aware:</b> The values and names of the claims, that need to be defined are depending on the used
         * provider.<br>
         * <b>Be aware:</b> Said authorization provider must also be configured for your webPDF server. (server.xml)
         * </p>
         *
         * @param jsonConfigFile A json config file, that contains all claims required to request the access token from
         *                       the authorization provider. (You will find a short documentation of the claims there.)
         * @throws IOException Shall be thrown, if reading the config file failed.
         */
        public AzureConfig(@NotNull File jsonConfigFile) throws IOException {
            super(jsonConfigFile);
        }

        /**
         * Returns the value of the claim "authority".
         *
         * @return The value of the "authority" claim.
         */
        public @NotNull String getAuthority() {
            return getClaim("authority");
        }

        /**
         * Returns the value of the claim "clientId".
         *
         * @return The value of the "clientId" claim.
         */
        public @NotNull String getClientID() {
            return getClaim("clientId");
        }

        /**
         * Returns the value of the claim "clientSecret".
         *
         * @return The value of the "clientSecret" claim.
         */
        public @NotNull String getClientSecret() {
            return getClaim("clientSecret");
        }

        /**
         * Returns the value of the claim "scope".
         *
         * @return The value of the "scope" claim.
         */
        public @NotNull String getScope() {
            return getClaim("scope");
        }

    }

}
