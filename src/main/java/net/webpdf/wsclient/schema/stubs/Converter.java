package net.webpdf.wsclient.schema.stubs;

import jakarta.activation.DataHandler;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.Action;
import jakarta.xml.ws.FaultAction;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;
import net.webpdf.wsclient.schema.operation.ObjectFactory;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * A class implementing {@link Converter} provides an interface class for the webPDF
 * {@link WebServiceType#CONVERTER} webservice.
 * </p
 *
 * @see WebServiceType#CONVERTER
 */
@WebService(name = "Converter", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter")
@XmlSeeAlso({
        ObjectFactory.class
})
public interface Converter {
    /**
     * This is the webservice execution method of the {@link WebServiceType#CONVERTER} webservice interface.
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
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter",
            className = "net.webpdf.wsclient.webservice.stubs.converter.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter",
            className = "net.webpdf.wsclient.webservice.stubs.converter.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/converter/Converter/executeRequest",
            output = "http://schema.webpdf.de/1.0/soap/converter/Converter/executeResponse", fault = {
            @FaultAction(className = ConverterWebServiceException.class,
                    value = "http://schema.webpdf.de/1.0/soap/converter/Converter/execute/Fault/WebserviceException")
    })
    @NotNull DataHandler execute(
            @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
            @NotNull OperationData operation,
            @WebParam(name = "fileContent")
            @Nullable DataHandler fileContent,
            @WebParam(name = "fileURL")
            @Nullable String fileURL)
            throws ConverterWebServiceException;

}
