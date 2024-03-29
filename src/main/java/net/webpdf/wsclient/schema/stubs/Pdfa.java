package net.webpdf.wsclient.schema.stubs;

import jakarta.xml.ws.*;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.ObjectFactory;
import net.webpdf.wsclient.schema.operation.OperationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * <p>
 * A class implementing {@link Pdfa} provides an interface class for the webPDF {@link WebServiceType#PDFA} webservice.
 * </p>
 *
 * @see WebServiceType#PDFA
 */
@SuppressWarnings("NonJaxWsWebServices")
@WebService(name = "Pdfa", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa")
@XmlSeeAlso({
        ObjectFactory.class
})
public interface Pdfa {
    /**
     * This is the webservice execution method of the {@link WebServiceType#PDFA} webservice interface.
     *
     * @param operation   The {@link OperationData}, which contain the configuration and parameterization of
     *                    the webservice call.
     * @param fileURL     The URL of the source document that shall be processed. (might be {@code null} to select a
     *                    file via it´s  {@link DataHandler} instead.)
     * @param fileContent The {@link DataHandler} of the source document, that shall be processed.
     *                    (might be {@code null} to select a resource via URL instead.)
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, when calling the webservice failed.
     */
    @WebMethod
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa",
            className = "net.webpdf.wsclient.webservice.stubs.pdfa.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa",
            className = "net.webpdf.wsclient.webservice.stubs.pdfa.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/executeRequest",
            output = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/executeResponse", fault = {
            @FaultAction(className = PdfaWebServiceException.class,
                    value = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/execute/Fault/WebserviceException")
    })
    @NotNull DataHandler execute(
            @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
            @NotNull OperationData operation,
            @WebParam(name = "fileContent")
            @Nullable DataHandler fileContent,
            @WebParam(name = "fileURL")
            @Nullable String fileURL)
            throws PdfaWebServiceException;

}
