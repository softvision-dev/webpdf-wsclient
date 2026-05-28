# webPDF wsclient
This repository contains a simplified and optimized client library for the webPDF server and serves as an alternative to the more complex direct use of the REST or SOAP APIs.
The library implements the required stubs and session management in ready-to-use interface classes and provides a common object-based parameterization for webservice calls in both protocols.

![webPDF Logo](images/logo.png)

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF wsclient library for webservice calls, a running webPDF installation is required. A demo version as Windows installation, Linux package or container image can be downloaded from the [product page](https://docs.webpdf.de/docs/download/).

> **Note**: Unless otherwise marked, the following is based on webPDF version 10 or newer. If you are using an older version please update to use all parameters for the current release of the library. You will always find the newest version at the product [download page](https://docs.webpdf.de/docs/download/).

## Download

> **Note:** The version 10.0.5 requires at least webPDF server version **[10.0.5](https://docs.webpdf.de/changelog/)** and JDK 11.

> **Note:** Starting with wsclient 9.x the wsclient´s and webPDF server´s version numbers have been synchronized, to simplify finding the proper wsclient for your webPDF server.

You can use this library by adding this dependency to your project:
```
<!-- https://mvnrepository.com/artifact/net.webpdf/webpdf-wsclient -->
<dependency>
    <groupId>net.webpdf</groupId>
    <artifactId>webpdf-wsclient</artifactId>
    <version>10.0.5</version>
</dependency>
```

To download the webPDF server required for this library, 
visit the [webPDF download page](https://docs.webpdf.de/docs/download/).
Further documentation on the webPDF server can be found in the [Developer Hub](https://docs.webpdf.de/).

## Supported languages

The `wsclient` is available for the following programming languages:

- [Java](https://docs.webpdf.de/docs/development/wsclient/java/)
- [TypeScript](https://docs.webpdf.de/docs/development/wsclient/typescript/)
- C# (coming soon)

## Usage
You will find documentation, usage patterns and examples in the [Developer Hub](https://docs.webpdf.de/docs/development/wsclient/).
Further examples can also be found in the project [GitHub wiki](https://github.com/softvision-dev/webpdf-wsclient/wiki/Usage).

## Documentation
For detailed documentation on the webPDF wsclient library, visit the [Developer Hub](https://docs.webpdf.de/docs/development/wsclient/).
For more information about webPDF, please visit the [webPDF Documentation](https://docs.webpdf.de/docs/).

## Development and support
If you have any questions on how to use webPDF, or this library,
or have ideas for future development, please get in touch via our [support team](https://docs.webpdf.de/docs/support).

If you find any issues, please file a [bug](https://github.com/softvision-dev/webpdf-wsclient/issues) after checking for duplicates or create a [pull request](https://github.com/softvision-dev/webpdf-wsclient/pulls).

## License
Please see the [license](LICENSE) file for more information.
