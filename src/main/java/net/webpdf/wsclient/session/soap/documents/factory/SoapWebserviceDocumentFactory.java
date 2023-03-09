package net.webpdf.wsclient.session.soap.documents.factory;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.soap.documents.datasource.BinaryDataSource;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 * An instance of {@link SoapWebserviceDocumentFactory} shall generalize the instantiation of
 * {@link SoapWebServiceDocument}s.
 */
public class SoapWebserviceDocumentFactory implements SoapDocumentFactory<SoapWebServiceDocument> {

    /**
     * <p>
     * Creates a handle for a {@link SoapWebService} output without providing a source document.<br>
     * This is recommended for webservices, that don't require a source document. (Such as the URL-Converter.)
     * </p>
     * <p>
     * <b>Be aware:</b> Most webservices require a source document, with few exceptions. Before using this,
     * make sure that this is valid for the {@link WebService} call you intend to execute.
     * </p>
     */
    @Override
    public @NotNull SoapWebServiceDocument createInstance() {
        return new SoapWebServiceDocument();
    }

    /**
     * <p>
     * Creates a {@link SoapDocument} originating from the given {@link InputStream}.<br>
     * <b>Be aware:</b> This copies all remaining bytes from the given {@link InputStream} to an array, to create a
     * reusable {@link BinaryDataSource}.<br>
     * Especially for large files using this is ill advised, using the alternative constructors
     * {@link #createInstance(URI)} or {@link #createInstance(File)} is always recommended.
     * </p>
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createInstance(@NotNull InputStream source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

    /**
     * Creates a {@link SoapDocument} originating from the given {@link URI}.
     *
     * @param source The {@link URI} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createInstance(@NotNull URI source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

    /**
     * Creates a {@link SoapDocument} originating from the given {@link File}.
     *
     * @param source The {@link File} the {@link SoapDocument} is originating from.
     * @throws ResultException Shall be thrown in case loading the source document failed.
     */
    @Override
    public @NotNull SoapWebServiceDocument createInstance(@NotNull File source) throws ResultException {
        return new SoapWebServiceDocument(source);
    }

}
