package net.webpdf.wsclient.tools;

import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class SerializeHelperTest {

    private final TestResources testResources = new TestResources(SerializeHelperTest.class);

    @Test
    public void testFromXML() throws Exception {
        String source = FileUtils.readFileToString(testResources.getResource("convert.xml"), Charset.defaultCharset());
        try (StringReader reader = new StringReader(source)) {
            StreamSource streamSource = new StreamSource(reader);
            OperationData operationData = SerializeHelper.fromXML(streamSource, OperationData.class);
            ConverterType converterType = operationData.getConverter();
            Assert.assertNotNull("Converter parameters should have been initialized.", converterType);
            assertEquals("Pages attribute should have been 1-5", "1-5", converterType.getPages());
            Assert.assertTrue("EmbedFonts attribute should have been true", converterType.isSetEmbedFonts());
            PdfaType pdfa = converterType.getPdfa();
            Assert.assertNotNull("Pdfa element should have been initialized.", pdfa);
            PdfaType.Convert convert = pdfa.getConvert();
            Assert.assertNotNull("Convert element should have been initialized.", convert);
            assertEquals("Level attribute should have been LEVEL_3B", PdfaLevelType.LEVEL_3B, convert.getLevel());
            assertEquals("ErrorReport attribute should have been MESSAGE", PdfaErrorReportType.MESSAGE, convert.getErrorReport());
        }
    }

    @Test
    public void testFromJSON() throws Exception {
        String source = FileUtils.readFileToString(testResources.getResource("convert.json"), Charset.defaultCharset());
        try (StringReader reader = new StringReader(source)) {
            StreamSource streamSource = new StreamSource(reader);
            OperationData operationData = SerializeHelper.fromJSON(streamSource, OperationData.class);
            ConverterType converterType = operationData.getConverter();
            Assert.assertNotNull("Converter parameters should have been initialized.", converterType);
            assertEquals("Pages attribute should have been 1-5", "1-5", converterType.getPages());
            Assert.assertTrue("EmbedFonts attribute should have been true", converterType.isSetEmbedFonts());
            PdfaType pdfa = converterType.getPdfa();
            Assert.assertNotNull("Pdfa element should have been initialized.", pdfa);
            PdfaType.Convert convert = pdfa.getConvert();
            Assert.assertNotNull("Convert element should have been initialized.", convert);
            assertEquals("Level attribute should have been LEVEL_3B", PdfaLevelType.LEVEL_3B, convert.getLevel());
            assertEquals("ErrorReport attribute should have been MESSAGE", PdfaErrorReportType.MESSAGE, convert.getErrorReport());
        }
    }

    @Test
    public void testFromXMLHttpEntity() throws Exception {
        HttpEntity entity = new FileEntity(testResources.getResource("convert.xml"));
        OperationData operationData = SerializeHelper.fromXML(entity, OperationData.class);
        ConverterType converterType = operationData.getConverter();
        Assert.assertNotNull("Converter parameters should have been initialized.", converterType);
        assertEquals("Pages attribute should have been 1-5", "1-5", converterType.getPages());
        Assert.assertTrue("EmbedFonts attribute should have been true", converterType.isSetEmbedFonts());
        PdfaType pdfa = converterType.getPdfa();
        Assert.assertNotNull("Pdfa element should have been initialized.", pdfa);
        PdfaType.Convert convert = pdfa.getConvert();
        Assert.assertNotNull("Convert element should have been initialized.", convert);
        assertEquals("Level attribute should have been LEVEL_3B", PdfaLevelType.LEVEL_3B, convert.getLevel());
        assertEquals("ErrorReport attribute should have been MESSAGE", PdfaErrorReportType.MESSAGE, convert.getErrorReport());
    }

    @Test
    public void testFromJSONHttpEntity() throws Exception {
        HttpEntity entity = new FileEntity(testResources.getResource("convert.json"));
        OperationData operationData = SerializeHelper.fromJSON(entity, OperationData.class);
        ConverterType converterType = operationData.getConverter();
        Assert.assertNotNull("Converter parameters should have been initialized.", converterType);
        assertEquals("Pages attribute should have been 1-5", "1-5", converterType.getPages());
        Assert.assertTrue("EmbedFonts attribute should have been true", converterType.isSetEmbedFonts());
        PdfaType pdfa = converterType.getPdfa();
        Assert.assertNotNull("Pdfa element should have been initialized.", pdfa);
        PdfaType.Convert convert = pdfa.getConvert();
        Assert.assertNotNull("Convert element should have been initialized.", convert);
        assertEquals("Level attribute should have been LEVEL_3B", PdfaLevelType.LEVEL_3B, convert.getLevel());
        assertEquals("ErrorReport attribute should have been MESSAGE", PdfaErrorReportType.MESSAGE, convert.getErrorReport());
    }

    @Test
    public void testInvalidEntities() {
        try {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.json"));
            SerializeHelper.fromXML(entity, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }

        try {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.xml"));
            SerializeHelper.fromJSON(entity, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
    }

    @Test
    public void testNullParameters() {
        try {
            SerializeHelper.fromXML((StreamSource) null, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
        try {
            SerializeHelper.fromXML(new StreamSource(), null);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }

        try {
            SerializeHelper.fromXML((HttpEntity) null, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
        try {
            SerializeHelper.fromXML(new FileEntity(new File("")), null);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }

        try {
            SerializeHelper.fromJSON((StreamSource) null, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
        try {
            SerializeHelper.fromJSON(new StreamSource(), null);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }

        try {
            SerializeHelper.fromJSON((HttpEntity)null, OperationData.class);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
        try {
            SerializeHelper.fromJSON(new FileEntity(new File("")), null);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
    }

    @Test
    public void toJSON() throws Exception {
        ConverterType converterType = new ConverterType();
        converterType.setEmbedFonts(true);
        converterType.setPages("1-5");

        PdfaType pdfaType = new PdfaType();
        PdfaType.Convert convert = new PdfaType.Convert();
        convert.setLevel(PdfaLevelType.LEVEL_3B);
        convert.setErrorReport(PdfaErrorReportType.MESSAGE);
        pdfaType.setConvert(convert);
        converterType.setPdfa(pdfaType);

        OperationData operationData = new OperationData();
        operationData.setConverter(converterType);

        assertEquals("Serialization content does not match the source object.",
            "{\"converter\":{\"pdfa\":{\"convert\":{\"level\":\"3b\",\"errorReport\":\"message\"}},\"pages\":\"1-5\",\"embedFonts\":true}}",
            SerializeHelper.toJSON(operationData));
    }

    @Test
    public void toJSONNullContent() throws Exception {
        assertEquals("JSON serialization result is not as expected.", "null", SerializeHelper.toJSON(null));
    }

    @Test
    public void toXML() throws Exception {
        ConverterType converterType = new ConverterType();
        converterType.setEmbedFonts(true);
        converterType.setPages("1-5");

        PdfaType pdfaType = new PdfaType();
        PdfaType.Convert convert = new PdfaType.Convert();
        convert.setLevel(PdfaLevelType.LEVEL_3B);
        convert.setErrorReport(PdfaErrorReportType.MESSAGE);
        pdfaType.setConvert(convert);
        converterType.setPdfa(pdfaType);

        OperationData operationData = new OperationData();
        operationData.setConverter(converterType);

        assertEquals("Serialization content does not match the source object.",
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<operation xmlns=\"http://schema.webpdf.de/1.0/operation\">\n" +
                "    <converter pages=\"1-5\" embedFonts=\"true\">\n" +
                "        <pdfa>\n" +
                "            <convert level=\"3b\" errorReport=\"message\"/>\n" +
                "        </pdfa>\n" +
                "    </converter>\n" +
                "</operation>\n",
            SerializeHelper.toXML(operationData, OperationData.class));
    }

    @Test(expected = ResultException.class)
    public void toXMLNullContent() throws Exception {
        SerializeHelper.toXML(null, OperationData.class);
    }

    @Test(expected = ResultException.class)
    public void toXMLNullType() throws Exception {
        SerializeHelper.toXML(new ConverterType(), null);
    }

    @Test(expected = ResultException.class)
    public void toXMLInvalidType() throws Exception {
        SerializeHelper.toXML(new ConverterType(), String.class);
    }
}
