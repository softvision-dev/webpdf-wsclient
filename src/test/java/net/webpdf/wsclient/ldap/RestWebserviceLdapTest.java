package net.webpdf.wsclient.ldap;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.testsuite.config.TestConfig;
import net.webpdf.wsclient.testsuite.integration.annotations.LdapTest;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RestWebserviceLdapTest {
    public TestServer testServer = new TestServer();

    @Test
    @LdapTest
    public void testHandleRestSessionLdapCertificates() {
        assertDoesNotThrow(() -> {
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(TestConfig.getInstance().getServerConfig().getLocalUserName(),
                            TestConfig.getInstance().getServerConfig().getLocalUserPassword()))) {
                // check User
                AuthUserCredentials user = session.getUser();
                assertNotNull(user, "User should be set.");
                assertTrue(user.getIsUser(), "User should be user.");

                AuthUserCertificates certificates = session.getCertificates();
                assertNotNull(certificates, "Certificates should be set.");
                assertTrue(certificates.getKeyStores().size() > 0, "User should have keystores.");
                assertTrue(certificates.getCertificates().size() > 0, "User should have certificates.");

                String keyStoreName = "";

                for (KeyStoreEntry keystore : certificates.getKeyStores()) {
                    assertNotNull(keystore.getKeyStoreName(), "keystore should have a name.");

                    if (keystore.getKeyStoreName().contains("PRINCIPAL")) {
                        keyStoreName = keystore.getKeyStoreName();
                        assertNotNull(keystore.getIsKeyStoreAccessible(), "keystore accessibility should be set.");
                        assertFalse(keystore.getIsKeyStoreAccessible(), "Keystore should not be accessible.");
                    }
                }

                // check errorneous
                KeyStorePassword parameter = new KeyStorePassword();
                parameter.setKeyStorePassword("test");

                assertThrows(ClientResultException.class, () -> session.updateCertificates("error", parameter));

                certificates = session.updateCertificates(keyStoreName, parameter);
                assertNotNull(certificates, "Certificates should be set.");

                for (KeyStoreEntry keystore : certificates.getKeyStores()) {
                    assertNotNull(keystore.getKeyStoreName(), "keystore should have a name.");

                    if (keystore.getKeyStoreName().equals(keyStoreName)) {
                        assertNotNull(keystore.getIsKeyStoreAccessible(), "keystore accessibility should be set.");
                        assertFalse(keystore.getIsKeyStoreAccessible(), "Keystore should not be accessible.");
                    }
                }

                // unlock keystore
                parameter.setKeyStorePassword("bmi");
                certificates = session.updateCertificates(keyStoreName, parameter);
                assertNotNull(certificates, "Certificates should be set.");
                assertTrue(certificates.getKeyStores().size() > 0, "User should have keystores.");

                for (KeyStoreEntry keystore : certificates.getKeyStores()) {
                    assertNotNull(keystore.getKeyStoreName(), "keystore should have a name.");

                    if (keystore.getKeyStoreName().equals(keyStoreName)) {
                        assertNotNull(keystore.getIsKeyStoreAccessible(), "keystore accessibility should be set.");
                        assertTrue(keystore.getIsKeyStoreAccessible(), "keystore should be accessible.");
                    }
                }

                for (CertificateEntry certificate : certificates.getCertificates()) {
                    assertNotNull(certificate.getAliasName(), "Certificate should have an alias name.");

                    if (certificate.getAliasName().equals("billymiller")) {
                        assertNotNull(certificate.getKeyStoreName());
                        assertEquals(certificate.getKeyStoreName(), keyStoreName, "keystore name should match this certificate.");
                        assertNotNull(certificate.getHasPrivateKey());
                        assertTrue(certificate.getHasPrivateKey(), "this certificate should have a private key.");
                        assertNotNull(certificate.getIsPrivateKeyReadable());
                        assertFalse(certificate.getIsPrivateKeyReadable(), "this certificates private key should not be readable.");
                    } else if (certificate.getAliasName().equals("billy")) {
                        assertNotNull(certificate.getKeyStoreName());
                        assertEquals(certificate.getKeyStoreName(), keyStoreName, "keystore name should match this certificate.");
                        assertNotNull(certificate.getHasPrivateKey());
                        assertTrue(certificate.getHasPrivateKey(), "this certificate should have a private key.");
                        assertNotNull(certificate.getIsPrivateKeyReadable());
                        assertTrue(certificate.getIsPrivateKeyReadable(), "this certificates private key should be readable.");
                    }
                }

                // unlock with wrong certificate password
                parameter.setKeyStorePassword("bmi");
                Map<String, String> aliases = new HashMap<>();
                aliases.put("billymiller", "error");
                parameter.setAliases(aliases);

                certificates = session.updateCertificates(keyStoreName, parameter);
                assertNotNull(certificates, "Certificates should be set.");

                for (CertificateEntry certificate : certificates.getCertificates()) {
                    assertNotNull(certificate.getAliasName(), "Certificate should have an alias name.");

                    if (certificate.getAliasName().equals("billymiller")) {
                        assertNotNull(certificate.getIsPrivateKeyReadable());
                        assertFalse(certificate.getIsPrivateKeyReadable(), "this certificates private key should not be readable.");
                    }
                }

                // unlock certificate
                parameter.setKeyStorePassword("bmi");
                aliases = new HashMap<>();
                aliases.put("billymiller", "billymiller");
                aliases.put("billy", "");
                parameter.setAliases(aliases);

                certificates = session.updateCertificates(keyStoreName, parameter);
                assertNotNull(certificates, "Certificates should be set.");

                for (CertificateEntry certificate : certificates.getCertificates()) {
                    assertNotNull(certificate.getAliasName(), "Certificate should have an alias name.");

                    if (certificate.getAliasName().equals("billymiller")) {
                        assertNotNull(certificate.getIsPrivateKeyReadable());
                        assertTrue(certificate.getIsPrivateKeyReadable(), "this certificates private key should be readable.");
                    }
                }
            }
        });
    }
}
