#1.0.0 (10.01.2019)
- Support for JDK 7 and higher.
- Initial release, supporting webPDF 7 xsd based parameterization for SOAP and REST Webservice calls.
- Supports the following webPDF 7 webservices:
    - Barcode webservice
    - Converter webservice
    - OCR webservice
    - PDFA webservice
    - Signature webservice
    - Toolbox webservice
    - URL converter webservice

#1.0.1 (15.08.2019)
- Fixed bug: Missing UTF-8 encoding in REST parameter structure.
- Fixed bug: Proper handling of webPDF 7 error codes and error messages in Webservice Exceptions (FaultInfo).

#2.0.0 (04.09.2019)
- Support for JDK 8 and higher (JDK 7 is no longer supported).
- Library and implementation adaptations and updates for JDK 8 and webPDF 8.
    - The webservice interfaces have been updated to support new webPDF 8 features.
    - JSON support of enumeration values has been improved. Enumeration values can/must now be set, using proper enumeration types instead of using simple string values.
- Adding full support for webPDF xjb files.        