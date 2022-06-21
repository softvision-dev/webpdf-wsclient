package net.webpdf.wsclient.schema.stubs;

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
import jakarta.xml.ws.Action;
import jakarta.xml.ws.FaultAction;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

/**
 * <p>
 * A class implementing {@link Barcode} provides an interface class for the webPDF {@link WebServiceType#BARCODE}
 * webservice.
 * </p>
 * <p>
 * This class was generated by the JAX-WS RI.<br>
 * JAX-WS RI 2.2.4-b01<br>
 * Generated source version: 2.2
 * </p>
 *
 * @see WebServiceType#BARCODE
 */
@SuppressWarnings("NonJaxWsWebServices")
@WebService(name = "Barcode", targetNamespace = "http://schema.webpdf.de/1.0/soap/barcode")
@XmlSeeAlso({
        ObjectFactory.class
})
public interface Barcode {

    /**
     * This is the webservice execution method of the {@link WebServiceType#BARCODE} webservice interface.
     *
     * @param operation   The {@link OperationData}, which contain the configuration and parameterization of
     *                    the webservice call.
     * @param fileURL     The URL of the source document that shall be processed. (might be {@code null} to select a
     *                    file via it´s  {@link DataHandler} instead.)
     * @param fileContent The {@link DataHandler} of the source document, that shall be prcoessed.
     *                    (might be {@code null} to select a resource via URL instead.)
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, when calling the webservice failed.
     */
    @WebMethod
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/barcode",
            className = "net.webpdf.wsclient.webservice.stubs.barcode.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/barcode",
            className = "net.webpdf.wsclient.webservice.stubs.barcode.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/barcode/Barcode/executeRequest",
            output = "http://schema.webpdf.de/1.0/soap/barcode/Barcode/executeResponse", fault = {
            @FaultAction(className = WebServiceException.class,
                    value = "http://schema.webpdf.de/1.0/soap/barcode/Barcode/execute/Fault/WebserviceException")
    })
    @NotNull DataHandler execute(
            @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
            @NotNull OperationData operation,
            @WebParam(name = "fileContent")
            @Nullable DataHandler fileContent,
            @WebParam(name = "fileURL")
            @Nullable String fileURL)
            throws WebServiceException;

}
