package net.webpdf.wsclient.testsuite.integration.certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GenericCertificate {

    private Certificate certificate;
    private KeyPair keyPair;
    private static final SecureRandom RANDOM = new SecureRandom();

    public GenericCertificate(@NotNull String issuerName) {

        assertDoesNotThrow(() -> {
            AsymmetricCipherKeyPairGenerator keyPairGenerator = RSA();
            this.keyPair = convertBcToJceKeyPair(keyPairGenerator.generateKeyPair());
            BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            Calendar notBefore = Calendar.getInstance();
            Calendar notAfter = Calendar.getInstance();
            notAfter.add(Calendar.MONTH, 1);
            final ContentSigner contentSigner = assertDoesNotThrow(
                    () -> new JcaContentSignerBuilder("SHA256withRSA")
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
        });
    }

    private AsymmetricCipherKeyPairGenerator RSA() {
        AsymmetricCipherKeyPairGenerator keyPairGen = new RSAKeyPairGenerator();
        keyPairGen.init(new RSAKeyGenerationParameters(new BigInteger("10001", 16), RANDOM, 1024, 80));
        return keyPairGen;
    }

    @SuppressWarnings("unused")
    private AsymmetricCipherKeyPairGenerator DSA() {
        DSAParametersGenerator dsaParametersGenerator = new DSAParametersGenerator();
        dsaParametersGenerator.init(1024, 80, RANDOM);
        DSAParameters dsaParameters = dsaParametersGenerator.generateParameters();
        AsymmetricCipherKeyPairGenerator keyPairGen = new DSAKeyPairGenerator();
        keyPairGen.init(new DSAKeyGenerationParameters(RANDOM, dsaParameters));
        return keyPairGen;
    }

    private KeyPair convertBcToJceKeyPair(AsymmetricCipherKeyPair bcKeyPair) throws Exception {
        byte[] pkcs8Encoded = PrivateKeyInfoFactory.createPrivateKeyInfo(bcKeyPair.getPrivate()).getEncoded();
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pkcs8Encoded);
        byte[] encoded = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(bcKeyPair.getPublic()).getEncoded();
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        return new KeyPair(keyFac.generatePublic(encodedKeySpec), keyFac.generatePrivate(pkcs8KeySpec));
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
