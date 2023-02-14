# webPDF wsclient
This repository contains a simplified and optimized client library for the webPDF server, and serves as an alternative to more complex REST or SOAP APIs.
The library implements the required stubs and session management in ready-to-use interface classes and provides a common object based parameterization for webservice calls in both protocols.

![webPDF Logo](images/logo.png)

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF wsclient library for webservice calls, a running webPDF installation is required. A demo version as Windows installation, Linux package or as a virtual machine can be downloaded from the [product page](https://www.webpdf.de/en/download-web-pdf.html).

> **Note**: Unless otherwise marked, the following is based on webPDF version 9 or newer. If you are using an older version please update to use all parameters for the current release of the library. You will always find the newest version at the product [download page](https://download.softvision.de/?product=webpdf).

## Download

> **Note:** The version 9.0.0 requires at least webPDF server version **9.0.0** and JDK 11.
> **Note:** Starting with wsclient 9.0.0 the wsclient´s and webPDF server´s version numbers have been synchronized, to simplify finding the proper wsclient for your webPDF server.

You can use this library, by adding this dependency to your project:
```
<!-- https://mvnrepository.com/artifact/net.webpdf/webpdf-wsclient -->
<dependency>
    <groupId>net.webpdf</groupId>
    <artifactId>webpdf-wsclient</artifactId>
    <version>9.0.0</version>
</dependency>
```

## Usage
You will find some [usage examples](https://github.com/softvision-dev/webpdf-wsclient/wiki/Usage) in the wiki.
 
## Documentation
Have a look at our [wiki](https://github.com/softvision-dev/webpdf-wsclient/wiki) for examples and details.

## Development and support
If you have any questions on how to use webPDF, or this library, or have ideas for future development, please get in touch via our [product homepage](https://www.webpdf.de).

If you find any issues, please file a [bug](https://github.com/softvision-dev/webpdf-wsclient/issues) after checking for duplicates or create a [pull request](https://github.com/softvision-dev/webpdf-wsclient/pulls).

## More help
Learn even more about our product in our [webPDF Documentation](https://www.webpdf.de/en/documentation).

## License
Please, see the [license](LICENSE) file for more information.

## Changes
The current version 9.0.0 is optimized for webPDF version 9. When using webPDF 7 or 8 you might prefer earlier versions of this library.
You might want to have a look at the [changes](CHANGES.md) file for further information.