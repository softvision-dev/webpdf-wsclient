
package net.webpdf.wsclient.schema.stubs;


import net.webpdf.wsclient.schema.operation.ObjectFactory;
import net.webpdf.wsclient.schema.operation.OperationData;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
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
 * 
 */
@WebService(name = "Pdfa", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Pdfa {


    /**
     * 
     * @param operation
     * @param fileURL
     * @param fileContent
     * @return
     *     returns javax.activation.DataHandler
     * @throws WebserviceException
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "execute", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa", className = "net.webpdf.wsclient.webservice.stubs.pdfa.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://schema.webpdf.de/1.0/soap/pdfa", className = "net.webpdf.wsclient.webservice.stubs.pdfa.ExecuteResponse")
    @Action(input = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/executeRequest", output = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/executeResponse", fault = {
        @FaultAction(className = WebserviceException.class, value = "http://schema.webpdf.de/1.0/soap/pdfa/Pdfa/execute/Fault/WebserviceException")
    })
    public DataHandler execute(
            @WebParam(name = "operation", targetNamespace = "http://schema.webpdf.de/1.0/operation")
                    OperationData operation,
            @WebParam(name = "fileContent", targetNamespace = "")
                    DataHandler fileContent,
            @WebParam(name = "fileURL", targetNamespace = "")
                    String fileURL)
        throws WebserviceException
    ;

}
