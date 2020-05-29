package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class SoapWebServiceSession extends AbstractSoapSession<SoapWebServiceDocument> {

    /**
     * Creates new {@link SoapWebServiceSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    public SoapWebServiceSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, tlsContext);
    }
}
