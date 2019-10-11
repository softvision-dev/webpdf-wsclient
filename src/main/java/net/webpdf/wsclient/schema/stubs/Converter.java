
package net.webpdf.wsclient.schema.stubs;


import net.webpdf.wsclient.schema.operation.ObjectFactory;
import net.webpdf.wsclient.schema.operation.OperationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 */
@WebService(name = "Converter", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Converter {

    /**
     * This is the webservice execution method of the converter webservice interface.
     *
     * @param operation   The selected operation, configuration and parameterization of the webservice call.
     * @param fileURL     The URL of the source file, that shall be processed. (might be null to select a file directly instead.)
     * @param fileContent The direct DataHandler of the source file. (might be null to select a resource via URL.)
     * @return returns javax.activation.DataHandler The DataHandler of the target file for further handling.
     * @throws WebserviceException Shall be thrown, when calling the webservice failed with an error code.
     */
    @WebMethod
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter", className = "net.webpdf.wsclient.webservice.stubs.converter.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/converter", className = "net.webpdf.wsclient.webservice.stubs.converter.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/converter/Converter/executeRequest", output = "http://schema.webpdf.de/1.0/soap/converter/Converter/executeResponse", fault = {
        @FaultAction(className = WebserviceException.class, value = "http://schema.webpdf.de/1.0/soap/converter/Converter/execute/Fault/WebserviceException")
    })
    @NotNull
    DataHandler execute(
        @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
        @NotNull
            OperationData operation,
        @WebParam(name = "fileContent")
        @Nullable
            DataHandler fileContent,
        @WebParam(name = "fileURL")
        @Nullable
            String fileURL)
        throws WebserviceException
    ;

}
