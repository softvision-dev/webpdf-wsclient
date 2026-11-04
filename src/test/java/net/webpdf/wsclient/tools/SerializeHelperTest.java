package net.webpdf.wsclient.tools;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SerializeHelperTest {

    private final TestResources testResources = new TestResources(SerializeHelperTest.class);

    @Test
    public void testFromXML() {
        assertDoesNotThrow(() -> {
            String source = FileUtils.readFileToString(testResources.getResource("convert.xml"),
                    Charset.defaultCharset());
            try (StringReader reader = new StringReader(source)) {
                StreamSource streamSource = new StreamSource(reader);
                OperationData operationData = SerializeHelper.fromXML(streamSource, OperationData.class);
                ConverterType converterType = operationData.getConverter();
                assertNotNull(converterType,
                        "Converter parameters should have been initialized.");
                assertEquals("1-5", converterType.getPages(),
                        "Pages attribute should have been 1-5");
                assertTrue(converterType.isSetEmbedFonts(),
                        "EmbedFonts attribute should have been true");
                PdfaType pdfa = converterType.getPdfa();
                assertNotNull(pdfa,
                        "Pdfa element should have been initialized.");
                PdfaType.Convert convert = pdfa.getConvert();
                assertNotNull(convert,
                        "Convert element should have been initialized.");
                assertEquals(PdfaLevelType.LEVEL_3B, convert.getLevel(),
                        "Level attribute should have been LEVEL_3B");
                assertEquals(PdfaErrorReportType.MESSAGE, convert.getErrorReport(),
                        "ErrorReport attribute should have been MESSAGE");
            }
        });
    }

    @Test
    public void testFromJSON() {
        assertDoesNotThrow(() -> {
            String source = FileUtils.readFileToString(testResources.getResource("convert.json"),
                    Charset.defaultCharset());
            try (StringReader reader = new StringReader(source)) {
                StreamSource streamSource = new StreamSource(reader);
                OperationData operationData = SerializeHelper.fromJSON(streamSource, OperationData.class);
                ConverterType converterType = operationData.getConverter();
                assertNotNull(converterType,
                        "Converter parameters should have been initialized.");
                assertEquals("1-5", converterType.getPages(),
                        "Pages attribute should have been 1-5");
                assertTrue(converterType.isSetEmbedFonts(),
                        "EmbedFonts attribute should have been true");
                PdfaType pdfa = converterType.getPdfa();
                assertNotNull(pdfa,
                        "Pdfa element should have been initialized.");
                PdfaType.Convert convert = pdfa.getConvert();
                assertNotNull(convert,
                        "Convert element should have been initialized.");
                assertEquals(PdfaLevelType.LEVEL_3B, convert.getLevel(),
                        "Level attribute should have been LEVEL_3B");
                assertEquals(PdfaErrorReportType.MESSAGE, convert.getErrorReport(),
                        "ErrorReport attribute should have been MESSAGE");
            }
        });
    }

    @Test
    public void testFromXMLHttpEntity() {
        assertDoesNotThrow(() -> {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.xml"), ContentType.APPLICATION_XML);
            OperationData operationData = SerializeHelper.fromXML(entity, OperationData.class);
            ConverterType converterType = operationData.getConverter();
            assertNotNull(converterType,
                    "Converter parameters should have been initialized.");
            assertEquals("1-5", converterType.getPages(),
                    "Pages attribute should have been 1-5");
            assertTrue(converterType.isSetEmbedFonts(),
                    "EmbedFonts attribute should have been true");
            PdfaType pdfa = converterType.getPdfa();
            assertNotNull(pdfa,
                    "Pdfa element should have been initialized.");
            PdfaType.Convert convert = pdfa.getConvert();
            assertNotNull(convert,
                    "Convert element should have been initialized.");
            assertEquals(PdfaLevelType.LEVEL_3B, convert.getLevel(),
                    "Level attribute should have been LEVEL_3B");
            assertEquals(PdfaErrorReportType.MESSAGE, convert.getErrorReport(),
                    "ErrorReport attribute should have been MESSAGE");
        });
    }

    @Test
    public void testFromJSONHttpEntity() {
        assertDoesNotThrow(() -> {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.json"), ContentType.APPLICATION_JSON);
            OperationData operationData = SerializeHelper.fromJSON(entity, OperationData.class);
            ConverterType converterType = operationData.getConverter();
            assertNotNull(converterType,
                    "Converter parameters should have been initialized.");
            assertEquals("1-5", converterType.getPages(),
                    "Pages attribute should have been 1-5");
            assertTrue(converterType.isSetEmbedFonts(),
                    "EmbedFonts attribute should have been true");
            PdfaType pdfa = converterType.getPdfa();
            assertNotNull(pdfa,
                    "Pdfa element should have been initialized.");
            PdfaType.Convert convert = pdfa.getConvert();
            assertNotNull(convert,
                    "Convert element should have been initialized.");
            assertEquals(PdfaLevelType.LEVEL_3B, convert.getLevel(),
                    "Level attribute should have been LEVEL_3B");
            assertEquals(PdfaErrorReportType.MESSAGE, convert.getErrorReport(),
                    "ErrorReport attribute should have been MESSAGE");
        });
    }

    @Test
    public void testInvalidEntities() {
        try {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.json"), ContentType.APPLICATION_JSON);
            SerializeHelper.fromXML(entity, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }

        try {
            HttpEntity entity = new FileEntity(testResources.getResource("convert.xml"), ContentType.APPLICATION_XML);
            SerializeHelper.fromJSON(entity, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }
    }

    @Test
    public void testNullParameters() {
        try {
            SerializeHelper.fromXML((StreamSource) null, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }

        try {
            SerializeHelper.fromXML((HttpEntity) null, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }

        try {
            SerializeHelper.fromJSON((StreamSource) null, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }

        try {
            SerializeHelper.fromJSON((HttpEntity) null, OperationData.class);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, ex.getClientError(),
                    String.format("Error-code %s expected.", Error.INVALID_HTTP_MESSAGE_CONTENT.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }
    }

    @Test
    public void toJSON() {
        assertDoesNotThrow(() -> {
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

            assertEquals("{\"converter\":{\"pdfa\":{\"convert\":" +
                            "{\"level\":\"3b\",\"errorReport\":\"message\"}}," +
                            "\"pages\":\"1-5\",\"embedFonts\":true}}",
                    SerializeHelper.toJSON(operationData),
                    "Serialization content does not match the source object.");
        });
    }

    @Test
    public void toJSONNullContent() {
        assertDoesNotThrow(() -> assertEquals("null", SerializeHelper.toJSON(null),
                "JSON serialization result is not as expected."));
    }

    @SuppressWarnings("HttpUrlsUsage")
    @Test
    public void toXML() {
        assertDoesNotThrow(() -> {
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

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<operation xmlns=\"http://schema.webpdf.de/1.0/operation\">\n" +
                            "    <converter pages=\"1-5\" embedFonts=\"true\">\n" +
                            "        <pdfa>\n" +
                            "            <convert level=\"3b\" errorReport=\"message\"/>\n" +
                            "        </pdfa>\n" +
                            "    </converter>\n" +
                            "</operation>\n",
                    SerializeHelper.toXML(operationData, OperationData.class),
                    "Serialization content does not match the source object.");
        });
    }

    @Test
    public void toXMLNullContent() {
        assertThrows(ResultException.class,
                () -> SerializeHelper.toXML(null, OperationData.class));
    }

    @Test
    public void toXMLInvalidType() {
        assertThrows(ResultException.class,
                () -> SerializeHelper.toXML(new ConverterType(), String.class));
    }

    @Test
    public void testFromJSONInputStreamSource() throws Exception {
        try (InputStream inputStream = new FileInputStream(testResources.getResource("convert.json"))) {
            OperationData operationData = SerializeHelper.fromJSON(new StreamSource(inputStream),
                    OperationData.class);
            assertNotNull(operationData.getConverter(), "Converter parameters should have been initialized.");
            assertEquals("1-5", operationData.getConverter().getPages(), "Pages attribute should have been 1-5");
        }
    }

    @Test
    public void testFromJSONFileSource() throws Exception {
        // A File based StreamSource only carries a system ID.
        OperationData operationData = SerializeHelper.fromJSON(
                new StreamSource(testResources.getResource("convert.json")), OperationData.class);
        assertNotNull(operationData.getConverter(), "Converter parameters should have been initialized.");
        assertEquals("1-5", operationData.getConverter().getPages(), "Pages attribute should have been 1-5");
    }

    @Test
    public void testFromJSONSourceWithoutContent() {
        ClientResultException exception = assertThrows(ClientResultException.class,
                () -> SerializeHelper.fromJSON(new StreamSource(), OperationData.class));
        assertEquals(Error.INVALID_HTTP_MESSAGE_CONTENT, exception.getClientError(), "Unexpected error code.");
    }

    // Note: this test can only detect a regression on a JVM whose default charset is not UTF-8 (e.g. Java 11-17
    // on Windows); with a UTF-8 default (Linux locales, Java 18+) the platform encoding gives the same bytes.
    @Test
    public void testFromJSONHttpEntityWithoutCharsetIsReadAsUtf8() throws Exception {
        // "Ω" and "ł" are not part of windows-1252 or ISO-8859-1.
        String message = "Ärger mit Ω und ł";
        HttpEntity httpEntity = new ByteArrayEntity(
                ("{\"errorMessage\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8),
                ContentType.create("application/json"));
        Map<?, ?> result = SerializeHelper.fromJSON(httpEntity, Map.class);
        assertEquals(message, result.get("errorMessage"), "The content should have been decoded as UTF-8.");
    }

    @Test
    public void testFromJSONHttpEntityUsesDeclaredCharset() throws Exception {
        String message = "Ärger mit Ω und ł";
        HttpEntity httpEntity = new ByteArrayEntity(
                ("{\"errorMessage\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_16),
                ContentType.create("application/json", StandardCharsets.UTF_16));
        Map<?, ?> result = SerializeHelper.fromJSON(httpEntity, Map.class);
        assertEquals(message, result.get("errorMessage"), "The content should have been decoded as UTF-16.");
    }

}
