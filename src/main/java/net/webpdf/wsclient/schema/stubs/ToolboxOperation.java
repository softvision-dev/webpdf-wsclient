package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "toolbox"
})
@XmlRootElement(name = "operation")
public class ToolboxOperation extends BaseOperation {

    private List<BaseToolboxType> toolbox;

    @XmlElements({
            @XmlElement(name = "annotation", namespace = "http://schema.webpdf.de/1.0/operation", type = AnnotationType.class, required = true),
            @XmlElement(name = "attachment", namespace = "http://schema.webpdf.de/1.0/operation", type = AttachmentType.class, required = true),
            @XmlElement(name = "delete", namespace = "http://schema.webpdf.de/1.0/operation", type = DeleteType.class, required = true),
            @XmlElement(name = "description", namespace = "http://schema.webpdf.de/1.0/operation", type = DescriptionType.class, required = true),
            @XmlElement(name = "extraction", namespace = "http://schema.webpdf.de/1.0/operation", type = ExtractionType.class, required = true),
            @XmlElement(name = "forms", namespace = "http://schema.webpdf.de/1.0/operation", type = FormsType.class, required = true),
            @XmlElement(name = "image", namespace = "http://schema.webpdf.de/1.0/operation", type = ImageType.class, required = true),
            @XmlElement(name = "merge", namespace = "http://schema.webpdf.de/1.0/operation", type = MergeType.class, required = true),
            @XmlElement(name = "options", namespace = "http://schema.webpdf.de/1.0/operation", type = OptionsType.class, required = true),
            @XmlElement(name = "print", namespace = "http://schema.webpdf.de/1.0/operation", type = PrintType.class, required = true),
            @XmlElement(name = "rotate", namespace = "http://schema.webpdf.de/1.0/operation", type = RotateType.class, required = true),
            @XmlElement(name = "security", namespace = "http://schema.webpdf.de/1.0/operation", type = SecurityType.class, required = true),
            @XmlElement(name = "split", namespace = "http://schema.webpdf.de/1.0/operation", type = SplitType.class, required = true),
            @XmlElement(name = "watermark", namespace = "http://schema.webpdf.de/1.0/operation", type = WatermarkType.class, required = true),
            @XmlElement(name = "xmp", namespace = "http://schema.webpdf.de/1.0/operation", type = XmpType.class, required = true)
    })
    public List<BaseToolboxType> getToolbox() {
        if (this.toolbox == null) {
            this.toolbox = new ArrayList<>();
        }
        return this.toolbox;
    }

    public void setToolbox(List<BaseToolboxType> toolbox) {
        if (toolbox != null) {
            this.toolbox = toolbox;
        }
    }
}
