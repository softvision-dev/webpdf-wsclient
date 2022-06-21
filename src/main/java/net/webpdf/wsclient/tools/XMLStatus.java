package net.webpdf.wsclient.tools;

/**
 * {@link XMLStatus} enumerates all known validation results produced by a {@link XMLValidationEventHandler}.
 */
enum XMLStatus {

    /**
     * The XML is well-formed and contains a valid data transfer object.
     */
    OK,
    /**
     * The XML validation did not fail, some contents however are not well-formed.
     */
    WARNING,
    /**
     * The XML validation most likely failed, the validated contents contents are invalid.
     */
    ERROR,
    /**
     * The XML validation failed, the validated contents are invalid and unusable.
     */
    FATAL

}
