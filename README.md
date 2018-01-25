# webPDF wsclient
This repository contains a simplified and optimized client library for the webPDF server, and serves as an alternative to more complex REST or SOAP APIs.
The library implements the required stubs and session management in ready-to-use interface classes and provides a common object based parameterization for webservice calls in both protocols.

![webPDF Logo](images/logo.png)

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF wsclient library for webservice calls, a running webPDF installation is required. A demo version as Windows installation, Linux package or as a virtual machine can be downloaded from the [product page](https://www.webpdf.de/en/download-web-pdf.html).

> **Note**: Unless otherwise marked, the following is based on webPDF version 7 or newer.

## Instructions
 
 [Maven](https://maven.apache.org/): mvn clean install
  

## Usage
Two simple usage samples for wsclient:

##### Usage with REST
 ```java
 import net.webpdf.wsclient.webservice.*;
 import net.webpdf.wsclient.webservice.schemas.operation.*;
 import net.webpdf.wsclient.webservice.session.*;
 
 import javax.xml.bind.JAXBException;
 import java.io.File;
 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.net.URL;
 ...
  try(Session session= SessionFactory.createInstance(WebServiceProtocol.REST,new URL("http://localhost:8080/webPDF/"))){
 
             ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
 
             File file = new File("./files/lorem-ipsum.docx");
             File fileOut = new File("./result/converter_rest.pdf");
 
             webService.setDocument(session.getDocumentManager().uploadDocument(file));
 
             webService.getDocument().setTargetFile(fileOut);
             webService.getOperation().setPages("1-4");
             webService.getOperation().setEmbedFonts(true);
 
             webService.getOperation().setPdfa(new PdfaType());
             webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
             webService.getOperation().getPdfa().getConvert().setLevel("3b");
             webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
 
             session.getDocumentManager().downloadDocument(webService.process());
 
 
         }catch (IOException|URISyntaxException|JAXBException ex){...
          
 ```
 
##### Usage with SOAP
 ```java
 import net.webpdf.wsclient.webservice.*;
 import net.webpdf.wsclient.webservice.documents.SoapDocument;
 import net.webpdf.wsclient.webservice.schemas.operation.*;
 import net.webpdf.wsclient.webservice.session.*;
 
 
 import javax.xml.bind.JAXBException;
 import java.io.File;
 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.net.URL;
 ...
  try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, new URL("http://localhost:8080/webPDF/"))) {
 
             ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
             
             File file = new File("./files/lorem-ipsum.docx");
             File fileOut = new File("./result/converter_soap.pdf");
             webService.setDocument(new SoapDocument(file.toURI(), fileOut));             
 
             webService.getOperation().setPages("1-4");
             webService.getOperation().setEmbedFonts(true);
 
             webService.getOperation().setPdfa(new PdfaType());
             webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
             webService.getOperation().getPdfa().getConvert().setLevel("3b");
             webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
 
             session.getDocumentManager().downloadDocument(webService.process());
 
         } catch (IOException | URISyntaxException | JAXBException ex) {...
  
 ``` 
 
## License
Please, see the [license](LICENSE) file for more information.

## More help
[webPDF Documentation](https://www.webpdf.de/en/documentation)

## External libs
[Apache Commons](https://commons.apache.org/)
 - [codec](https://commons.apache.org/proper/commons-codec/)
 - [IO](https://commons.apache.org/proper/commons-io/)
 - [Lang](https://commons.apache.org/proper/commons-lang/)
 
[Apache HttpComponents](https://hc.apache.org/)
 - [HttpClient](https://hc.apache.org/httpcomponents-client-4.5.x/)
 - [HttpCore](https://hc.apache.org/httpcomponents-core-4.4.x/)
 
[gson](https://github.com/google/gson)