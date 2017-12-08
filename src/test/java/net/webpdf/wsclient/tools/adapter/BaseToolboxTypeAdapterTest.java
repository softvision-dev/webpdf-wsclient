package net.webpdf.wsclient.tools.adapter;

import com.google.gson.*;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.schema.operation.DeleteType;
import org.junit.Test;

import java.lang.reflect.Type;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class BaseToolboxTypeAdapterTest {

    @Test(expected = JsonParseException.class)
    public void TestDeserializeUnspecifiedType() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();
        JsonObject jObject = new JsonObject();
        jObject.addProperty("attribute", "value");
        assertNull("Unspecified type can not lead to valid deserialization.", baseToolboxTypeAdapter.deserialize(jObject, String.class, null));
    }

    @Test
    public void TestDeserializeSpecifiedType() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();

        JsonObject toolbox = new JsonObject();
        toolbox.add("unknown", new JsonObject());
        JsonObject delete = new JsonObject();
        delete.addProperty("pages", "1");
        toolbox.add("delete", delete);

        BaseToolboxType baseToolboxType = baseToolboxTypeAdapter.deserialize(toolbox, DeleteType.class, null);
        assertNotNull("Valid BaseToolboxType should have been deserialized.", baseToolboxType);
        assertTrue("BaseToolboxType should have been instance of DeleteType.", baseToolboxType instanceof DeleteType);
        DeleteType deleteType = (DeleteType) baseToolboxType;
        assertEquals("Pages attribute should have been 1.", "1", deleteType.getPages());
    }

    @Test
    public void TestSerializeSpecifiedObject() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();
        DeleteType delete = new DeleteType();
        delete.setPages("1");

        JsonElement element = baseToolboxTypeAdapter.serialize(delete, null, new SerializationContext());
        assertNotNull("Valid json element should have been serialized.", element);
        assertEquals("Serialization should have been equal to defined expectations.", "{\"delete\":{\"pages\":\"1\"}}", element.toString());
    }

    @Test
    public void TestSerializeNull() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();

        JsonElement element = baseToolboxTypeAdapter.serialize(null, null, new SerializationContext());
        assertNotNull("Valid json element should have been serialized.", element);
        assertEquals("Serialization should have been equal to defined expectations.", "{}", element.toString());
    }

    @Test
    public void TestSerializeNullContext() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();

        JsonElement element = baseToolboxTypeAdapter.serialize(new DeleteType(), null, null);
        assertNotNull("Valid json element should have been serialized.", element);
        assertEquals("Serialization should have been equal to defined expectations.", "{}", element.toString());
    }

    @Test(expected = JsonParseException.class)
    public void TestDeserializeNull() throws Exception {
        BaseToolboxTypeAdapter baseToolboxTypeAdapter = new BaseToolboxTypeAdapter();

        baseToolboxTypeAdapter.deserialize(null, null, null);
        fail("Exception should have been thrown.");
    }

    private class SerializationContext implements JsonSerializationContext {

        private final Gson gson = new GsonBuilder().create();

        @Override
        public JsonElement serialize(Object src) {
            return gson.toJsonTree(src);
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc) {
            return gson.toJsonTree(src, typeOfSrc);
        }
    }
}
