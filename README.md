# webPDF wsclient
This repository contains a simplified and optimized client library for the webPDF server, and serves as an alternative to more complex REST or SOAP APIs.
The library implements the required stubs and session management in ready-to-use interface classes and provides a common object based parameterization for webservice calls in both protocols.

![webPDF Logo](images/logo.png)

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF wsclient library for webservice calls, a running webPDF installation is required. A demo version as Windows installation, Linux package or as a virtual machine can be downloaded from the [product page](https://www.webpdf.de/en/download-web-pdf.html).

> **Note**: Unless otherwise marked, the following is based on webPDF version 7 or newer.

## Installation
You can clone this repository with 'Git' to your local system. Another way to use this library without using 'Git' is to download the zip file containing the current version (using the following link or by clicking the "Download ZIP" button on the repo page).

[Download the library as ZIP](https://github.com/softvision-dev/webpdf-wsclient/archive/master.zip)

This library is build with the IntelliJ IDEA IDE. You can directly open and run the project from inside the IDE. For other IDEs you have to adapt the project accordingly.

### Building the project
This project uses Maven as it's Build-Management-Tool. To produce all necessary artifacts, you can simply run:
 
 [Maven](https://maven.apache.org/): mvn clean install
  
### Testing the project
The Maven Build lifecycle, will also cause the execution of some basic unit tests, which will check the proper functionality of all classes contained in this library.
To test the client-server communication with the webPDF server itself, you will have to activate the maven "integration" profile, which will cause the execution of extended integration tests during the "test" phase.
The integration tests require a running instance of the webPDF server with an activated SSL "connector" (https). 
For a proper execution of the integration tests, the insecure http "connector" must be activated as well.
You can find help concerning the SSL configuration here: [SSL Configuration](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/server_security_ssl.htm)

## Usage
The webPDF server provides a broad palette of webservices - too many to describe the interface for each of them here.
But you can find a description of all [webservices](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/webservice_general.htm) in our user manual.
You might find our [parameter documentation](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/webservice_parameter.htm) helpful as well. 

Further we want to provide two simple usage examples for the wsclient library:

##### Usage with REST
 ```java
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
##### SSL/TLS Configuration
The SessionFactory offers a method to create a secure HTTPS Session via the "TLSContext" class. TLS is the official successor of SSL and the webPDF wsclient library does only support the protocol versions:
- TLSv1, TLSv1.1, TLSv1.2 
 
 ```java
 TLSContext context = TLSContext.createDefault()
    .setAllowSelfSigned(true)
    .setProtocol(TLSProtocol.TLSV1_2)
    .setTrustStore(testResources.getResource("keystore.jks"), "keystorePassword");
 try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
    new URL("https://localhost:8443/webPDF"), context)) {
        session.login();
        executeConverter(session);
 }
 ``` 
 
##### Error codes
The webPDF [error codes](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/error_codes.htm) shall be wrapped in the server's responses and are representing errors on the server side. The webPDF wsclient implements it's own error codes and exceptions to indicate errors on the client side.

Following is a list of the various possible error codes.

**UNKNOWN_EXCEPTION = -1**

Unknown problem. Please contact Support.

**UNKNOWN_WEBSERVICE_PROTOCOL = -2**

The Selected webservice protocol is not known. 

**UNKNOWN_WEBSERVICE_TYPE = -3**

The selected webservice type is not known.

**INVALID_WEBSERVICE_URL = -4**

The given URL does not point to a webPDF webservice.

**INVALID_FILE_SOURCE = -5**

The referenced file source is invalid.

**INVALID_OPERATION_DATA = -6**

The given operation parameters are invalid.

**INVALID_DOCUMENT = -7**

Resolving the document reference failed.

**NO_OPERATION_DATA = -8**

No operation data have been given.

**NO_DOCUMENT = -9**

No document reference has been given.

**INVALID_URL = -30**

The given server URL is invalid.

**HTTP_IO_ERROR = -31**

The HTTP request to the webPDF server failed.

**HTTPS_IO_ERROR = -32**

The HTTPS request to the webPDF server failed.

**HTTP_EMPTY_ENTITY = -33**

The server's response is invalid.

**HTTP_CUSTOM_ERROR = -34**

The server's response could not be read.

**UNKNOWN_HTTP_METHOD = -35**

The server does not offer access via the selected HTTP method.

**SESSION_CREATE = -36**

The session creation failed.

**TO_XML = -37**

The given data container could not be translated to XML.

**WSDL_INVALID_FILE = -50**

The given file does not contain a valid WSDL structure.

**WSDL_INVALID_URL = -51**

The given URL does not contain a valid WSDL structure.

**SOAP_EXECUTION = -52**

The execution of the SOAP request failed.

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