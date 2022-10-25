package net.webpdf.wsclient.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.*;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * An instance of this class wraps some sort of {@link WebServiceProtocol#REST} operation data and may be used as a
 * base to parameterize a webPDF webservice call, to any {@link WebServiceType}.
 * </p>
 * <p>
 * A {@link RestOperationData} object is an artificial supporting structure, that would not normally occur in the
 * openapi definition. It exists to create a similar container to the {@link WebServiceProtocol#SOAP} specific
 * {@link OperationData} and allows a similar handling of both protocols via the {@link WebServiceFactory}.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "settings",
        "barcode",
        "converter",
        "ocr",
        "pdfa",
        "signature",
        "toolbox",
        "urlconverter"
})
@XmlRootElement(name = "operation")
public class RestOperationData {

    private @Nullable OperationBilling billing;
    private @Nullable OperationPdfPassword password;
    private @Nullable OperationSettings settings;
    private @Nullable OperationBarcode barcode;
    private @Nullable OperationConverter converter;
    private @Nullable OperationOcr ocr;
    private @Nullable OperationPdfa pdfa;
    private @Nullable OperationSignature signature;
    private @Nullable List<OperationBaseToolbox> toolbox;
    private @Nullable OperationUrlConverter urlconverter;

    /**
     * <p>
     * Prepares a base operation data instance, that may be used to parameterize a {@link WebServiceType} webPDF
     * webservice call.
     * </p>
     */
    @SuppressWarnings("unused")
    public RestOperationData() {
    }

    /**
     * <p>
     * Wraps the given {@link OperationConverterOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#CONVERTER} webPDF webservice call.
     * </p>
     *
     * @param operation The {@link OperationConverterOperation} to wrap.
     */
    public RestOperationData(@NotNull OperationConverterOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setConverter((operation).getConverter());
    }

    /**
     * <p>
     * Wraps the given {@link OperationBarcodeOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#BARCODE} webPDF webservice call.
     * </p>
     *
     * @param operation The {@link OperationBarcodeOperation} to wrap.
     */
    public RestOperationData(@NotNull OperationBarcodeOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setBarcode((operation).getBarcode());
    }

    /**
     * <p>
     * Wraps the given {@link OperationOcrOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#OCR} webPDF webservice call.
     * </p>
     *
     * @param operation The {@link OperationOcrOperation} to wrap.
     */
    public RestOperationData(@NotNull OperationOcrOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setOcr((operation).getOcr());
    }

    /**
     * Wraps the given {@link OperationPdfaOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#PDFA} webPDF webservice call.
     */
    public RestOperationData(@NotNull OperationPdfaOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setPdfa((operation).getPdfa());
    }

    /**
     * <p>
     * Wraps the given {@link OperationSignatureOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#SIGNATURE} webPDF webservice call.
     * </p>
     *
     * @param operation The {@link OperationSignatureOperation} to wrap.
     */
    public RestOperationData(@NotNull OperationSignatureOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setSignature((operation).getSignature());
    }

    /**
     * <p>
     * Wraps the given {@link OperationToolboxOperation} as operation data and may be used as a base to parameterize a
     * {@link WebServiceType#TOOLBOX} webPDF webservice call.
     * </p>
     * <p>
     * This may contain the following operations:
     * <ul>
     *  <li>{@link OperationToolboxAnnotationAnnotation}</li>
     *  <li>{@link OperationToolboxAttachmentAttachment}</li>
     *  <li>{@link OperationToolboxDeleteDelete}</li>
     *  <li>{@link OperationToolboxDescriptionDescription}</li>
     *  <li>{@link OperationToolboxExtractionExtraction}</li>
     *  <li>{@link OperationToolboxFormsForms}</li>
     *  <li>{@link OperationToolboxImageImage}</li>
     *  <li>{@link OperationToolboxMergeMerge}</li>
     *  <li>{@link OperationToolboxOptionsOptions}</li>
     *  <li>{@link OperationToolboxPrintPrint}</li>
     *  <li>{@link OperationToolboxRotateRotate}</li>
     *  <li>{@link OperationToolboxSecuritySecurity}</li>
     *  <li>{@link OperationToolboxSplitSplit}</li>
     *  <li>{@link OperationToolboxWatermarkWatermark}</li>
     *  <li>{@link OperationToolboxXmpXmp}</li>
     *  <li>{@link OperationToolboxMoveMove}</li>
     *  <li>{@link OperationToolboxOutlineOutline}</li>
     *  <li>{@link OperationToolboxRedactRedact}</li>
     *  <li>{@link OperationToolboxPortfolioPortfolio}</li>
     *  <li>{@link OperationToolboxScaleScale}</li>
     *  <li>{@link OperationToolboxCompressCompress}</li>
     *  <li>{@link OperationToolboxTranscribeTranscribe}</li>
     * </ul>
     * </p>
     *
     * @param operation The {@link OperationToolboxOperation} to wrap.
     */
    public RestOperationData(OperationToolboxOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setToolbox((operation).getToolbox());
    }

    /**
     * <p>
     * Wraps the given {@link OperationUrlConverterOperation} as operation data and may be used as a base to
     * parameterize a {@link WebServiceType#URLCONVERTER} webPDF webservice call.
     * </p>
     *
     * @param operation The {@link OperationUrlConverterOperation} to wrap.
     */
    public RestOperationData(OperationUrlConverterOperation operation) {
        setBilling(operation.getBilling());
        setPassword(operation.getPassword());
        setSettings(operation.getSettings());
        setUrlconverter((operation).getUrlconverter());
    }

    /**
     * Returns the {@link OperationBilling} settings set for this {@link RestOperationData}.
     *
     * @return The {@link OperationBilling} settings set for this {@link RestOperationData}.
     */
    public @Nullable OperationBilling getBilling() {
        return billing;
    }

    /**
     * Sets the {@link OperationBilling} settings for this {@link RestOperationData}.
     *
     * @param value The {@link OperationBilling} settings for this {@link RestOperationData}.
     */
    public void setBilling(@Nullable OperationBilling value) {
        this.billing = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationBilling} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationBilling} not be {@code null}.
     * @see #setBilling(OperationBilling)
     * @see #getBilling()
     */
    @JsonIgnore
    public boolean isSetBilling() {
        return (this.billing != null);
    }

    /**
     * Returns the {@link OperationPdfPassword} settings set for this {@link RestOperationData}.
     *
     * @return The {@link OperationPdfPassword} settings set for this {@link RestOperationData}.
     */
    public @Nullable OperationPdfPassword getPassword() {
        return password;
    }

    /**
     * Sets the {@link OperationPdfPassword} settings for this {@link RestOperationData}.
     *
     * @param value The {@link OperationPdfPassword} settings for this {@link RestOperationData}.
     */
    public void setPassword(@Nullable OperationPdfPassword value) {
        this.password = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationPdfPassword} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationPdfPassword} not be {@code null}.
     * @see #setPassword(OperationPdfPassword)
     * @see #getPassword() ()
     */
    @JsonIgnore
    public boolean isSetPassword() {
        return (this.password != null);
    }

    /**
     * Returns the {@link OperationSettings} settings set for this {@link RestOperationData}.
     *
     * @return The {@link OperationSettings} settings set for this {@link RestOperationData}.
     */
    public @Nullable OperationSettings getSettings() {
        return settings;
    }

    /**
     * Sets the {@link OperationSettings} settings for this {@link RestOperationData}.
     *
     * @param value The {@link OperationSettings} settings for this {@link RestOperationData}.
     */
    public void setSettings(@Nullable OperationSettings value) {
        this.settings = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationSettings} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationSettings} not be {@code null}.
     * @see #setSettings(OperationSettings)
     * @see #getSettings()
     */
    @JsonIgnore
    public boolean isSetSettings() {
        return (this.settings != null);
    }

    /**
     * Returns the {@link OperationBarcode} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationBarcode} set for this {@link RestOperationData}.
     */
    public @Nullable OperationBarcode getBarcode() {
        return barcode;
    }

    /**
     * Sets the {@link OperationBarcode} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationBarcode} for this {@link RestOperationData}.
     */
    public void setBarcode(@Nullable OperationBarcode value) {
        this.barcode = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationBarcode} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationBarcode} not be {@code null}.
     * @see #setBarcode(OperationBarcode)
     * @see #getBarcode()
     */
    @JsonIgnore
    public boolean isSetBarcode() {
        return (this.barcode != null);
    }

    /**
     * Returns the {@link OperationConverter} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationConverter} set for this {@link RestOperationData}.
     */
    public @Nullable OperationConverter getConverter() {
        return converter;
    }

    /**
     * Sets the {@link OperationConverter} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationConverter} for this {@link RestOperationData}.
     */
    public void setConverter(@Nullable OperationConverter value) {
        this.converter = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationConverter} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationConverter} not be {@code null}.
     * @see #setConverter(OperationConverter)
     * @see #getConverter()
     */
    @JsonIgnore
    public boolean isSetConverter() {
        return (this.converter != null);
    }

    /**
     * Returns the {@link OperationOcr} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationOcr} set for this {@link RestOperationData}.
     */
    public @Nullable OperationOcr getOcr() {
        return ocr;
    }

    /**
     * Sets the {@link OperationOcr} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationOcr} for this {@link RestOperationData}.
     */
    public void setOcr(@Nullable OperationOcr value) {
        this.ocr = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationOcr} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationOcr} not be {@code null}.
     * @see #setOcr(OperationOcr)
     * @see #getOcr()
     */
    @JsonIgnore
    public boolean isSetOcr() {
        return (this.ocr != null);
    }

    /**
     * Returns the {@link OperationPdfa} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationPdfa} set for this {@link RestOperationData}.
     */
    public @Nullable OperationPdfa getPdfa() {
        return pdfa;
    }

    /**
     * Sets the {@link OperationPdfa} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationPdfa} for this {@link RestOperationData}.
     */
    public void setPdfa(@Nullable OperationPdfa value) {
        this.pdfa = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationPdfa} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationPdfa} not be {@code null}.
     * @see #setPdfa(OperationPdfa)
     * @see #getPdfa()
     */
    @JsonIgnore
    public boolean isSetPdfa() {
        return (this.pdfa != null);
    }

    /**
     * Returns the {@link OperationSignature} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationSignature} set for this {@link RestOperationData}.
     */
    public @Nullable OperationSignature getSignature() {
        return signature;
    }

    /**
     * Sets the {@link OperationSignature} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationSignature} for this {@link RestOperationData}.
     */
    public void setSignature(@Nullable OperationSignature value) {
        this.signature = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationSignature} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationSignature} not be {@code null}.
     * @see #setSignature(OperationSignature)
     * @see #getSignature()
     */
    @JsonIgnore
    public boolean isSetSignature() {
        return (this.signature != null);
    }

    /**
     * <p>
     * Returns the {@link OperationBaseToolbox} list set for this {@link RestOperationData}.
     * </p>
     * <p>
     * This may contain the following operations:
     * <ul>
     *  <li>{@link OperationToolboxAnnotationAnnotation}</li>
     *  <li>{@link OperationToolboxAttachmentAttachment}</li>
     *  <li>{@link OperationToolboxDeleteDelete}</li>
     *  <li>{@link OperationToolboxDescriptionDescription}</li>
     *  <li>{@link OperationToolboxExtractionExtraction}</li>
     *  <li>{@link OperationToolboxFormsForms}</li>
     *  <li>{@link OperationToolboxImageImage}</li>
     *  <li>{@link OperationToolboxMergeMerge}</li>
     *  <li>{@link OperationToolboxOptionsOptions}</li>
     *  <li>{@link OperationToolboxPrintPrint}</li>
     *  <li>{@link OperationToolboxRotateRotate}</li>
     *  <li>{@link OperationToolboxSecuritySecurity}</li>
     *  <li>{@link OperationToolboxSplitSplit}</li>
     *  <li>{@link OperationToolboxWatermarkWatermark}</li>
     *  <li>{@link OperationToolboxXmpXmp}</li>
     *  <li>{@link OperationToolboxMoveMove}</li>
     *  <li>{@link OperationToolboxOutlineOutline}</li>
     *  <li>{@link OperationToolboxRedactRedact}</li>
     *  <li>{@link OperationToolboxPortfolioPortfolio}</li>
     *  <li>{@link OperationToolboxScaleScale}</li>
     *  <li>{@link OperationToolboxCompressCompress}</li>
     *  <li>{@link OperationToolboxTranscribeTranscribe}</li>
     * </ul>
     * </p>
     *
     * @return The {@link OperationSignature} list set for this {@link RestOperationData}.
     */
    public @NotNull List<OperationBaseToolbox> getToolbox() {
        if (toolbox == null) {
            toolbox = new ArrayList<>();
        }
        return this.toolbox;
    }

    /**
     * <p>
     * Sets the {@link OperationBaseToolbox} list for this {@link RestOperationData}.
     * </p>
     * <p>
     * This may contain the following operations:
     * <ul>
     *  <li>{@link OperationToolboxAnnotationAnnotation}</li>
     *  <li>{@link OperationToolboxAttachmentAttachment}</li>
     *  <li>{@link OperationToolboxDeleteDelete}</li>
     *  <li>{@link OperationToolboxDescriptionDescription}</li>
     *  <li>{@link OperationToolboxExtractionExtraction}</li>
     *  <li>{@link OperationToolboxFormsForms}</li>
     *  <li>{@link OperationToolboxImageImage}</li>
     *  <li>{@link OperationToolboxMergeMerge}</li>
     *  <li>{@link OperationToolboxOptionsOptions}</li>
     *  <li>{@link OperationToolboxPrintPrint}</li>
     *  <li>{@link OperationToolboxRotateRotate}</li>
     *  <li>{@link OperationToolboxSecuritySecurity}</li>
     *  <li>{@link OperationToolboxSplitSplit}</li>
     *  <li>{@link OperationToolboxWatermarkWatermark}</li>
     *  <li>{@link OperationToolboxXmpXmp}</li>
     *  <li>{@link OperationToolboxMoveMove}</li>
     *  <li>{@link OperationToolboxOutlineOutline}</li>
     *  <li>{@link OperationToolboxRedactRedact}</li>
     *  <li>{@link OperationToolboxPortfolioPortfolio}</li>
     *  <li>{@link OperationToolboxScaleScale}</li>
     *  <li>{@link OperationToolboxCompressCompress}</li>
     *  <li>{@link OperationToolboxTranscribeTranscribe}</li>
     * </ul>
     * </p>
     *
     * @param value The {@link OperationBaseToolbox} list for this {@link RestOperationData}.
     */
    public void setToolbox(@Nullable List<OperationBaseToolbox> value) {
        this.toolbox = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationBaseToolbox} list not be {@code null} or {@code empty}.
     *
     * @return {@code true} should the set {@link OperationBaseToolbox} not be {@code null} or {@code empty}.
     * @see #setToolbox(List)
     * @see #getToolbox()
     */
    @JsonIgnore
    public boolean isSetToolbox() {
        return ((this.toolbox != null) && (!this.toolbox.isEmpty()));
    }

    /**
     * Returns the {@link OperationUrlConverter} set for this {@link RestOperationData}.
     *
     * @return The {@link OperationUrlConverter} set for this {@link RestOperationData}.
     */
    public @Nullable OperationUrlConverter getUrlconverter() {
        return urlconverter;
    }

    /**
     * Sets the {@link OperationUrlConverter} for this {@link RestOperationData}.
     *
     * @param value The {@link OperationUrlConverter} for this {@link RestOperationData}.
     */
    public void setUrlconverter(@Nullable OperationUrlConverter value) {
        this.urlconverter = value;
    }

    /**
     * Returns {@code true} should the set {@link OperationUrlConverter} not be {@code null}.
     *
     * @return {@code true} should the set {@link OperationUrlConverter} not be {@code null}.
     * @see #setUrlconverter(OperationUrlConverter)
     * @see #getUrlconverter()
     */
    @JsonIgnore
    public boolean isSetUrlconverter() {
        return (this.urlconverter != null);
    }

}

