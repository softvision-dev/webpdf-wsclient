package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.http.auth.Credentials;

import java.io.IOException;
import java.net.URI;

public interface Session extends AutoCloseable {

    @Override
    void close() throws IOException;

    WebServiceProtocol getWebServiceProtocol();

    URI getURI(String subPath) throws ResultException;

    DataFormat getDataFormat();

    Credentials getCredentials();

    void setCredentials(Credentials userCredentials);

}
