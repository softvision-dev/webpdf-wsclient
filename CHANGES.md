# 1.0.0 (10.01.2019)

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

# 1.0.1 (15.08.2019)

- Fixed bug: Missing UTF-8 encoding in REST parameter structure.
- Fixed bug: Proper handling of webPDF 7 error codes and error messages in Webservice Exceptions (FaultInfo).

# 2.0.0 (04.09.2019)

- Support for JDK 8 and higher (JDK 7 is no longer supported).
- Library and implementation adaptations and updates for JDK 8 and webPDF 8.
    - The webservice interfaces have been updated to support new webPDF 8 features.
    - JSON support of enumeration values has been improved. Enumeration values can/must now be set, using proper
      enumeration types instead of using simple string values.
- Adding full support for webPDF xjb files.

# 2.0.1 (19.03.2020)

- Improved handling of FaultInfo for REST services.
- Client update for new webPDF8 parameters (OCR, toolbox/merge, converter, toolbox/print, options)
- Enhanced REST document management and access (delete/rename/history)
- Enhanced REST user management (roles/credentials/limits)
- Adding client side proxy configuration for REST and SOAP services
- Added UTF-8 support for REST requests.
- Added "redact" annotation and "blacken" function for annotation/redact toolbox operation

# 9.0.0 (13.06.2022)

- Support for JDK 11 and higher.
- Library and implementation adaptations and updates for JDK 11 and webPDF 9.
    - The webservice interfaces have been updated to support new webPDF 9 features.
- Raised version number to 9.0.0 to synchronize the wsclient and webPDF server version numbers.
- Refactoring and generalization of Session, DocumentManager, Document. (not backwards compatible with wsclient 1 and 2)
- Improved package structure for better conciseness.
- Clearer differentiation of SOAP and REST webservice features.
    - DocumentManager is only available for REST sessions.
    - InputStreamDataSource and WSClientProxySelector are only available for SOAP sessions.
    - Removed support for selectable transfer DataFormat. SOAP shall always use XML, REST shall always use JSON.
- Adding REST and SOAP OAuth Token support.