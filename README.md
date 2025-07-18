# webPDF wsclient
This repository contains a simplified and optimized client library for the webPDF server and serves as an alternative to the more complex direct use of the REST or SOAP APIs.
The library implements the required stubs and session management in ready-to-use interface classes and provides a common object-based parameterization for webservice calls in both protocols.

![webPDF Logo](images/logo.png)

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF wsclient library for webservice calls, a running webPDF installation is required. A demo version as Windows installation, Linux package or container image can be downloaded from the [product page](https://docs.webpdf.de/docs/download/).

> **Note**: Unless otherwise marked, the following is based on webPDF version 10 or newer. If you are using an older version please update to use all parameters for the current release of the library. You will always find the newest version at the product [download page](https://docs.webpdf.de/docs/download/).

## Download

> **Note:** The version 10.0.1 requires at least webPDF server version **[10.0.1](https://docs.webpdf.de/changelog/)** and JDK 11.

> **Note:** Starting with wsclient 9.x the wsclient´s and webPDF server´s version numbers have been synchronized, to simplify finding the proper wsclient for your webPDF server.

You can use this library by adding this dependency to your project:
```
<!-- https://mvnrepository.com/artifact/net.webpdf/webpdf-wsclient -->
<dependency>
    <groupId>net.webpdf</groupId>
    <artifactId>webpdf-wsclient</artifactId>
    <version>10.0.1</version>
</dependency>
```

To download the webPDF server required for this library, 
visit the [webPDF download page](https://docs.webpdf.de/docs/download/).
Further documentation on the webPDF server can be found in the [Developer Hub](https://docs.webpdf.de/).

## Usage
You will find some [usage examples](https://github.com/softvision-dev/webpdf-wsclient/wiki/Usage) in the wiki.

## Migration
If you are migrating from an earlier version, please note the following:

**Migration to version 10.x**
Some enum constants, which are defined as “Camel Case”, have been adjusted so that they are now structured with an “_” (underscore) in the name and are therefore easier to read. Therefore, small code corrections may have to be made. 

**Migration to version 9.x**
With version 9.x this library also has undergone major changes to the logic,
structure and naming of its packages and classes.
If you already used prior versions of wsclient, you should expect to have to adapt your imports and code accordingly.
You will find further information in our [migration guide](https://github.com/softvision-dev/webpdf-wsclient/wiki/Migration).

## Documentation
Have a look at our [wiki](https://github.com/softvision-dev/webpdf-wsclient/wiki) for examples and details.
For more information about webPDF, please visit the [Developer Hub](https://docs.webpdf.de/).

## Development and support
If you have any questions on how to use webPDF, or this library,
or have ideas for future development, please get in touch via our [support team](https://docs.webpdf.de/docs/support).

If you find any issues, please file a [bug](https://github.com/softvision-dev/webpdf-wsclient/issues) after checking for duplicates or create a [pull request](https://github.com/softvision-dev/webpdf-wsclient/pulls).

## More help
Learn even more about our product in our [webPDF Documentation](https://www.webpdf.de/en/documentation).

## License
Please see the [license](LICENSE) file for more information.

## Changes
The current version 10.0.1 is for webPDF version 10. When using webPDF 8 or 9,
you might prefer earlier versions of this library.
You might want to have a look at the [changes](CHANGES.md) file for further information.
