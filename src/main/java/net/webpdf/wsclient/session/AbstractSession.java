package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.documents.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Session class which contains connection and authorization objects and a {@link DocumentManager} instance
 */
abstract class AbstractSession implements Session {

    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    DataFormat dataFormat = DataFormat.JSON;
    private String basePath;
    private URI baseUrl;
    private WebServiceProtocol webServiceProtocol;
    private AuthScope authScope;
    private Credentials credentials;

    /**
     * Creates new {@link AbstractSession} instance
     *
     * @param url                base url for webPDF server
     * @param webServiceProtocol protocol for Web service operation
     * @throws URISyntaxException a {@link URISyntaxException}
     */
    AbstractSession(URL url, WebServiceProtocol webServiceProtocol) throws ResultException {
        this.webServiceProtocol = webServiceProtocol;
        this.basePath = webServiceProtocol.equals(WebServiceProtocol.SOAP) ? "soap/" : "rest/";
        buildBaseURL(url);
    }

    public WebServiceProtocol getWebServiceProtocol() {
        return this.webServiceProtocol;
    }

    private void extractUserInfo(String userInfo) {

        String name = "";
        String password = "";

        if (userInfo != null) {
            String[] credentials = userInfo.split(":");

            if (credentials.length >= 1) {
                name = credentials[0];
            }

            if (credentials.length >= 2) {
                password = credentials[1];
            }
        }

        this.credentials = new UsernamePasswordCredentials(name, password);
    }

    private void buildBaseURL(URL url) throws ResultException {
        if (url == null) {
            throw new ResultException(Result.build(Error.INVALID_URL));
        }
        String toUrl = url.toString();
        if (!toUrl.endsWith("/")) {
            toUrl = toUrl + "/";
        }

        try {
            // get the URL
            URIBuilder uriBuilder = new URIBuilder(toUrl);
            String userInfo = uriBuilder.getUserInfo();
            this.baseUrl = uriBuilder.setUserInfo(null).build();
            this.authScope = new AuthScope(this.baseUrl.getHost(), this.baseUrl.getPort());

            // extract user info from the URL
            if (userInfo != null) {
                extractUserInfo(userInfo);
            }

            // set credentials based on the URL settings
            if (this.credentials != null) {
                this.credentialsProvider.setCredentials(this.authScope, this.credentials);
            }
        } catch (URISyntaxException ex) {
            throw new ResultException(Result.build(Error.INVALID_URL, ex));
        }
    }

    public URI getURI(String subPath) throws ResultException {
        try {
            return new URIBuilder(this.baseUrl)
                    .setPath(this.baseUrl.getPath() + this.basePath + subPath)
                    .build();
        } catch (URISyntaxException ex) {
            throw new ResultException(Result.build(Error.INVALID_URL, ex));
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        // set new credentials
        if (credentials != null) {
            this.credentials = credentials;
            this.credentialsProvider.setCredentials(this.authScope, this.credentials);
        }
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

}
