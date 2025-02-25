package net.webpdf.wsclient.schema.stubs;

import jakarta.xml.ws.*;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.ObjectFactory;
import net.webpdf.wsclient.schema.operation.OperationData;
import org.jetbrains.annotations.NotNull;

import jakarta.activation.DataHandler;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * <p>
 * A class implementing {@link URLConverter} provides an interface class for the webPDF
 * {@link WebServiceType#URLCONVERTER} webservice.
 * </p>
 *
 * @see WebServiceType#URLCONVERTER
 */
@WebService(name = "URLConverter", targetNamespace = "http://schema.webpdf.de/1.0/soap/urlconverter")
@XmlSeeAlso({
        ObjectFactory.class
})
public interface URLConverter {
    /**
     * This is the webservice execution method of the {@link WebServiceType#URLCONVERTER} webservice interface.
     *
     * @param operation The {@link OperationData}, which contain the configuration and parameterization of the
     *                  webservice call.
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, when calling the webservice failed.
     */
    @WebMethod
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/urlconverter",
            className = "net.webpdf.wsclient.webservice.stubs.urlconverter.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/urlconverter",
            className = "net.webpdf.wsclient.webservice.stubs.urlconverter.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/urlconverter/URLConverter/executeRequest",
            output = "http://schema.webpdf.de/1.0/soap/urlconverter/URLConverter/executeResponse", fault = {
            @FaultAction(className = URLConverterWebServiceException.class,
                    value = "http://schema.webpdf.de/1.0/soap/urlconverter/URLConverter/execute/Fault/WebserviceException")
    })
    @NotNull DataHandler execute(
            @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
            @NotNull OperationData operation)
            throws URLConverterWebServiceException;

}
