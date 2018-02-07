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

## Usage
The webPDF server provides a broad palette of webservices - too many to describe the interface for each of them here.
But you can find a description of all [webservices](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/webservice_general.htm) in our user manual.
You might find our [parameter documentation](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/webservice_parameter.htm) helpful as well. 

Further we want to provide two simple usage examples for the wsclient library:

### Usage with SOAP
```java
// Prepare the session by selecting the webPDF SOAP interface (remember to close sessions after you are done)
try (Session session = SessionFactory.createInstance(
     WebServiceProtocol.SOAP,
     new URL("http://localhost:8080/webPDF/")
   )) {
   // Select source and target files
   File source = new File("./files/lorem-ipsum.docx");
   File target = new File("./result/converter_soap.pdf");
   
   // Prepare a webservice call for a webPDF webservice (CONVERTER in this example)
   ConverterWebService webService = WebServiceFactory.createInstance(
      session, WebServiceType.CONVERTER
   );
   
   // Make the source and target document known to the prepared webservice call
   webService.setDocument(new SoapDocument(source.toURI(), target));
   
   // Parameterize the webservice call
   webService.getOperation().setPages("1-4");
   webService.getOperation().setEmbedFonts(true);
   
   webService.getOperation().setPdfa(new PdfaType());
   webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
   webService.getOperation().getPdfa().getConvert().setLevel("3b");
   webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
   
   // Execute the webservice call and download the result to the target file
   webService.process();
} catch (ResultException e){
    // When encountering a ResultException you can retrieve the error code like this:
    String resultMessage = e.getResult().getMessage(); 
    int errorCode = e.getResult().getCode());
    ...
 } catch (IOException e) {
    ...
 }
``` 

### Usage with REST
```java
// Prepare the session by selecting the webPDF REST interface (remember to close sessions after you are done)
try ( RestSession session= SessionFactory.createInstance(
     WebServiceProtocol.REST,
     new URL("http://localhost:8080/webPDF/")
    )){
   // Select source and target files
   File source = new File("./files/lorem-ipsum.docx");
   File target = new File("./result/converter_soap.pdf");
   
   // Connect to the server
   session.login();
   // Upload the source file to the Server and receive a reference to the uploaded document
   RestDocument sourceDocument = session.getDocumentManager().uploadDocument(source);
   
   // Prepare a webservice call for a webPDF webservice (CONVERTER in this example)
   ConverterRestWebService webService = WebServiceFactory.createInstance(
      session, WebServiceType.CONVERTER
   );
   
   // Make the source document known to the prepared webservice call
   webService.setDocument(sourceDocument);
   
   // Parameterize the webservice call
   webService.getOperation().setPages("1-4");
   webService.getOperation().setEmbedFonts(true);
   
   webService.getOperation().setPdfa(new PdfaType());
   webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
   webService.getOperation().getPdfa().getConvert().setLevel("3b");
   webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
   
   // Execute the webservice call and receive a reference to the result document
   RestDocument resultDocument = webService.process();
   
   // Download the result document to your local file system
   try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
      session.getDocumentManager().downloadDocument(resultDocument, fileOutputStream);
   }
} catch (ResultException e){
   // When encountering a ResultException you can retrieve the error code like this:
   String resultMessage = e.getResult().getMessage(); 
   int errorCode = e.getResult().getCode());
   ...
} catch (IOException e) {
   ...
}
```
 
## License
Please, see the [license](LICENSE) file for more information.

## Documentation
Have a look at our [wiki](https://github.com/softvision-dev/webpdf-wsclient/wiki) for more examples and details.
- [Installation](https://github.com/softvision-dev/webpdf-wsclient/wiki/Installation)
- [Usage](https://github.com/softvision-dev/webpdf-wsclient/wiki/Usage)
- [SSL/TLS Configuration](https://github.com/softvision-dev/webpdf-wsclient/wiki/SSL-TLS-Configuration)
- [Error codes](https://github.com/softvision-dev/webpdf-wsclient/wiki/Error-Codes)
- [License](https://github.com/softvision-dev/webpdf-wsclient/wiki/License)

## More help
Learn even more about our product in our [webPDF Documentation](https://www.webpdf.de/en/documentation).

## External libs
[Apache Commons](https://commons.apache.org/)
 - [codec](https://commons.apache.org/proper/commons-codec/)
 - [IO](https://commons.apache.org/proper/commons-io/)
 - [Lang](https://commons.apache.org/proper/commons-lang/)
 
[Apache HttpComponents](https://hc.apache.org/)
 - [HttpClient](https://hc.apache.org/httpcomponents-client-4.5.x/)
 - [HttpCore](https://hc.apache.org/httpcomponents-core-4.4.x/)
 
[gson](https://github.com/google/gson)