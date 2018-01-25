package net.webpdf.wsclient.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A SSL socket factory class to initiate a trust manger which allows 'self-signed certificates'
 */
public class SelfSignedCertSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory socketFactory;

    /**
     * SSL {@link Socket} factory for self-signed certificates.
     */
    public SelfSignedCertSSLSocketFactory() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new SelfSignedCertTrustManager()}, new SecureRandom());
            socketFactory = ctx.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {
        }
    }

    /**
     * Returns a SSL {@link Socket} factory for self-signed certificates.
     *
     * @return A SSL {@link Socket} factory for self-signed certificates.
     */
    public static SSLSocketFactory getDefault() {
        return new SelfSignedCertSSLSocketFactory();
    }

    /**
     * Creates a SSL {@link Socket} for self-signed certificates.
     *
     * @param socket The existing {@link Socket}.
     * @param string The hostname of the server.
     * @param i      The port of the server.
     * @param bln    Close the underlying socket when this socket is closed.
     * @return An SSL {@link Socket} for self-signed certificates.
     * @throws IOException an {@link IOException}
     */
    @Override
    public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
        return socketFactory.createSocket(socket, string, i, bln);
    }

    /**
     * Creates a SSL {@link Socket} for self-signed certificates.
     *
     * @param string The hostname of the server.
     * @param i      The server port.
     * @param ia     The client network address.
     * @param i1     The client port.
     * @return An SSL {@link Socket} for self-signed certificates.
     * @throws IOException an {@link IOException}
     */
    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException {
        return socketFactory.createSocket(string, i, ia, i1);
    }

    /**
     * Creates a SSL {@link Socket} for self-signed certificates.
     *
     * @param ia The hostname of the server.
     * @param i  The port of the server.
     * @return An SSL {@link Socket} for self-signed certificates.
     * @throws IOException an {@link IOException}
     */
    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException {
        return socketFactory.createSocket(ia, i);
    }

    /**
     * Creates a SSL {@link Socket} for self-signed certificates.
     *
     * @param ia  The hostname of the server.
     * @param i   The port of the server.
     * @param ia1 The client network address.
     * @param i1  The client port.
     * @return An SSL {@link Socket} for self-signed certificates.
     * @throws IOException an {@link IOException}
     */
    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
        return socketFactory.createSocket(ia, i, ia1, i1);
    }

    /**
     * Returns the list of cipher suites which are enabled by default. Unless a different list is enabled, handshaking
     * on an SSL connection will use one of these cipher suites. The minimum quality of service for these defaults
     * requires confidentiality protection and server authentication (that is, no anonymous cipher suites).
     *
     * @return array of the cipher suites enabled by default
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return socketFactory.getDefaultCipherSuites();
    }

    /**
     * Returns the names of the cipher suites which could be enabled for use on an SSL connection. Normally, only a
     * subset of these will actually be enabled by default, since this list may include cipher suites which do not meet
     * quality of service requirements for those defaults. Such cipher suites are useful in specialized applications.
     *
     * @return an array of cipher suite names
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return socketFactory.getSupportedCipherSuites();
    }

    /**
     * Returns a socket layered over an existing socket connected to the named host, at the given port. This constructor
     * can be used when tunneling SSL through a proxy or when negotiating the use of SSL over an existing socket. The
     * host and port refer to the logical peer destination. This socket is configured using the socket options established
     * for this factory.
     *
     * @param string the existing socket
     * @param i      the server host
     * @return An SSL {@link Socket} for self-signed certificates.
     * @throws IOException an {@link IOException}
     */
    @Override
    public Socket createSocket(String string, int i) throws IOException {
        return socketFactory.createSocket(string, i);
    }
}
