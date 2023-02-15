package net.webpdf.wsclient.testsuite.integration.certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.Certificate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GenericCertificate {

    private final @NotNull Certificate certificate;
    private final @NotNull KeyPair keyPair;

    public GenericCertificate(@NotNull String issuerName) {
        KeyPairGenerator keyPairGen = assertDoesNotThrow(
                () -> KeyPairGenerator.getInstance("DSA", new BouncyCastleProvider()));
        keyPairGen.initialize(1024);

        this.keyPair = keyPairGen.generateKeyPair();

        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.MONTH, 1);
        final ContentSigner contentSigner = assertDoesNotThrow(
                () -> new JcaContentSignerBuilder("SHA256withDSA")
                        .build(keyPair.getPrivate()));
        final X500Name issuer = new X500Name("CN=" + issuerName);
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(issuer,
                        serialNumber,
                        notBefore.getTime(),
                        notAfter.getTime(),
                        issuer,
                        keyPair.getPublic());
        this.certificate = assertDoesNotThrow(
                () -> new JcaX509CertificateConverter().setProvider(
                        new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner)));
    }

    public @NotNull String getCertificatesAsPEM() {
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(stringWriter)) {
            jcaPEMWriter.writeObject(this.certificate);
        } catch (Exception ignored) {
        }
        return stringWriter.toString();
    }

    public @NotNull String getPrivateKeyAsPEM() {
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(stringWriter)) {
            jcaPEMWriter.writeObject(this.keyPair.getPrivate());
        } catch (Exception ignored) {
        }
        return stringWriter.toString();
    }

}
