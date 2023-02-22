package net.webpdf.wsclient.schema;

/**
 * <p>
 * A class implementing {@link AccessPermissionType} shall indicate which access permissions an access policy user
 * (recipient) shall have, concerning the document. In a PDF document such permissions are represented by a number of
 * bit flags, where certain bits are setting specific permissions.
 * </p>
 * <p>
 * <b>Conforming readers shall ignore all flags other than those at bit positions 3, 4, 5, 6, 9, 10, 11, and 12.</b>
 * </p>
 */
public interface AccessPermissionType {

    /**
     * <b>Bit position 3: can print</b>
     * <p>
     * (Security handlers of revision 2) Print the document.
     * (Security handlers of revision 3 or greater) Print the document (possibly not at the highest quality level,
     * depending on whether bit 12 is also set).
     * </p>
     *
     * @return {@code true}, if the user may print the document.
     */
    boolean isCanPrint();

    /**
     * <b>Bit position 4: can modify</b>
     * <p>
     * Modify the contents of the document by operations other than those controlled by bits 6, 9, and 11.
     * </p>
     *
     * @return {@code true}, if a user may edit document contents.
     */
    boolean isCanModify();

    /**
     * <b>Bit position 5: can extract</b>
     * <p>
     * (Security handlers of revision 2) Copy or otherwise extract text and graphics from the document, including
     * extracting text and graphics (in support of accessibility to users with disabilities or for other purposes).
     * (Security handlers of revision 3 or greater) Copy or otherwise extract text and graphics from the document by
     * operations other than that controlled by bit 10.
     * </p>
     *
     * @return {@code true}, if a user may extract contents.
     */
    boolean isCanExtractContent();

    /**
     * <b>Bit position 6: can modify annotations</b>
     * <p>
     * Add or modify text annotations, fill in interactive form fields, and, if bit 4 is also set, create or modify
     * interactive form fields (including signature fields).
     * </p>
     *
     * @return {@code true}, if a user may edit a document's annotations.
     */
    boolean isCanModifyAnnotations();

    /**
     * <b>Bit position 9: can fill in forms</b>
     * <p>
     * (Security handlers of revision 3 or greater) Fill in existing interactive form fields (including signature
     * fields), even if bit 6 is clear.
     * </p>
     *
     * @return {@code true}, if a user may fill in existing forms.
     */
    boolean isCanFillInForm();

    /**
     * <b>Bit position 10: can extract for accessibility</b>
     * <p>
     * (Security handlers of revision 3 or greater) Extract text and graphics (in support of accessibility to
     * users with disabilities or for other purposes).
     * </p>
     *
     * @return {@code true}, if a user may extract contents for accessibility purposes.
     */
    boolean isCanExtractForAccessibility();

    /**
     * <b>Bit position 11: can assemble</b>
     * <p>
     * (Security handlers of revision 3 or greater) Assemble the document (insert, rotate, or delete pages and create
     * bookmarks or thumbnail images), even if bit 4 is clear.
     * </p>
     *
     * @return {@code true}, if a user may assemble the document.
     */
    boolean isCanAssemble();

    /**
     * <b>Bit position 12: can print high resolution</b>
     * <p>
     * (Security handlers of revision 3 or greater) Print the document to a representation from which a faithful
     * digital copy of the PDF content could be generated. When this bit is clear (and bit 3 is set), printing is
     * limited to a low-level representation of the appearance, possibly of degraded quality.
     * </p>
     *
     * @return {@code true}, if the user may print the document with high resolution.
     */
    boolean isCanPrintHighRes();

}
