package net.webpdf.wsclient.testsuite;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;

public class TestResources {
    private final String resourcePath;

    public TestResources(Class testClass) {
        this.resourcePath = testClass.getPackage().getName().replaceAll("\\.", "/");
    }

    public File getResource(String fileName) {
        URL resUrl = this.getClass().getClassLoader().getResource(resourcePath + "/" + fileName);
        assert resUrl != null;
        return new File(resUrl.getFile());
    }

    public TestArguments getArguments() {
        return new TestArguments();
    }

    public TestArguments getArguments(boolean useCredentials, boolean useSSLPorts) {
        return new TestArguments().setUseCredentials(useCredentials).setUseSSLPort(useSSLPorts);
    }

    public File getDemoKeystoreFile() throws Exception {
        URL serverUrl = new URL("https://portal.webpdf.de/webPDF");
        HttpsURLConnection conn = (HttpsURLConnection) serverUrl.openConnection();
        conn.connect();
        Certificate[] certs = conn.getServerCertificates();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        for (Certificate cert : certs) {
            ks.setCertificateEntry("webPDF", cert);
        }
        File keystoreFile = getResource("integration/files/ks.jks");
        try (OutputStream fos = new FileOutputStream(keystoreFile)) {
            ks.store(fos, "".toCharArray());
        }
        return keystoreFile;
    }
}
