package net.webpdf.wsclient.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A SSL socket factory class to initiate a trust manger which allows 'self-signed certificates'
 */
public class SelfSignedCertSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory socketFactory;

    /**
     * SSL socket factory for self-signed certificates
     */
    public SelfSignedCertSSLSocketFactory() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new SelfSignedCertTrustManager()}, new SecureRandom());
            socketFactory = ctx.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {
        }
    }

    public static SSLSocketFactory getDefault() {
        return new SelfSignedCertSSLSocketFactory();
    }

    @Override
    public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
        return socketFactory.createSocket(socket, string, i, bln);
    }

    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException {
        return socketFactory.createSocket(string, i, ia, i1);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException {
        return socketFactory.createSocket(ia, i);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
        return socketFactory.createSocket(ia, i, ia1, i1);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return socketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return socketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(String string, int i) throws IOException {
        return socketFactory.createSocket(string, i);
    }
}
