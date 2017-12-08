package net.webpdf.wsclient.tools.adapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertNull;

public class EnumAdapterFactoryTest {
    @Test
    public void createEnumTypeAdapter() throws Exception {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<EnumAdapterTestEnum> typeToken = TypeToken.get(EnumAdapterTestEnum.class);

        try (StringWriter stringWriter = new StringWriter();
             JsonWriter jsonWriter = new JsonWriter(stringWriter)) {
            enumAdapterFactory.create(new Gson(), typeToken).write(jsonWriter, EnumAdapterTestEnum.TYPE);
            Assert.assertEquals("EnumAdapterFactory should have processed the expected enum content.", "\"1test\"", stringWriter.toString());
        }
    }

    @Test
    public void createNonEnumType() throws Exception {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<String> typeToken = TypeToken.get(String.class);

        assertNull("Non enum types shall not be processed.", enumAdapterFactory.create(new Gson(), typeToken));
    }

    @Test
    public void createNullValue() throws Exception {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<EnumAdapterTestEnum> typeToken = TypeToken.get(EnumAdapterTestEnum.class);

        try (StringWriter stringWriter = new StringWriter();
             JsonWriter jsonWriter = new JsonWriter(stringWriter)) {
            enumAdapterFactory.create(new Gson(), typeToken).write(jsonWriter, null);
            Assert.assertEquals("Empty values shall always lead to a content of \"null\"", "null", stringWriter.toString());
        }
    }

    @Test
    public void createNullWriter() throws Exception {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<EnumAdapterTestEnum> typeToken = TypeToken.get(EnumAdapterTestEnum.class);

        enumAdapterFactory.create(new Gson(), typeToken).write(null, null);
    }

    @Test
    public void createIncompleteEnumType() throws Exception {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<EnumAdapterTestIncompleteEnum> typeToken = TypeToken.get(EnumAdapterTestIncompleteEnum.class);

        try (StringWriter stringWriter = new StringWriter();
             JsonWriter jsonWriter = new JsonWriter(stringWriter)) {
            enumAdapterFactory.create(new Gson(), typeToken).write(jsonWriter, EnumAdapterTestIncompleteEnum.INCOMPLETE);
            Assert.assertEquals("Incomplete enumeration definitions shall always lead to a content of \"null\"", "null", stringWriter.toString());
        }
    }

    @Test
    public void testUnsupportedWrite() throws IOException {
        EnumAdapterFactory enumAdapterFactory = new EnumAdapterFactory();
        TypeToken<EnumAdapterTestIncompleteEnum> typeToken = TypeToken.get(EnumAdapterTestIncompleteEnum.class);
        assertNull("Write is not supported and should return NULL.", enumAdapterFactory.create(new Gson(), typeToken).read(null));
    }

    private enum EnumAdapterTestEnum {
        TYPE("test", 1);

        private String stringValue;
        private int intValue;

        EnumAdapterTestEnum(String stringValue, int intValue) {
            this.stringValue = stringValue;
            this.intValue = intValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public String value() {
            return intValue + stringValue;
        }
    }

    private enum EnumAdapterTestIncompleteEnum {
        INCOMPLETE
    }
}
