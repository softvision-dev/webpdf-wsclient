package net.webpdf.wsclient.tools.adapter;

import com.google.gson.*;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.ToolboxOperation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class BaseToolboxTypeAdapter implements JsonDeserializer<BaseToolboxType>, JsonSerializer<BaseToolboxType> {

    private static final Class TOOLBOX_TYPE_DEFINITION_CLASS = ToolboxOperation.class;
    private static final String TOOLBOX_TYPE_DEFINITION_METHOD = "getToolbox";

    @Override
    public JsonElement serialize(BaseToolboxType src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        if (src == null || context == null) {
            return jsonObject;
        }

        //noinspection finally
        try {
            Field nameField = OperationData.class.getDeclaredField("toolbox");
            XmlElements xmlElements = nameField.getAnnotation(XmlElements.class);
            XmlElement[] allXmlElement = xmlElements.value();

            for (XmlElement xmlElement : allXmlElement) {
                if (src.getClass().getName().equals(xmlElement.type().getName())) {
                    jsonObject.add(xmlElement.name(), context.serialize(src));
                    break;
                }
            }

        } catch (NoSuchFieldException ignore) {
        } finally {
            //noinspection ReturnInsideFinallyBlock
            return jsonObject;
        }
    }

    @Override
    public BaseToolboxType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            throw new JsonParseException("Json element is null and can not be deserialized.");
        }

        Map<String, Class> toolboxTypes = getXMLElementTypes(TOOLBOX_TYPE_DEFINITION_CLASS, TOOLBOX_TYPE_DEFINITION_METHOD);
        JsonObject jObject = json.getAsJsonObject();
        for (String key : jObject.keySet()) {
            Class type = toolboxTypes.get(key);
            if (type == null) {
                continue;
            }
            jObject = jObject.getAsJsonObject(key);
            Gson gson = new Gson();
            Object instance = gson.fromJson(jObject, type);
            if (instance instanceof BaseToolboxType) {
                return (BaseToolboxType) instance;
            }
        }
        throw new JsonParseException("BaseToolboxType is unspecified.");
    }

    private Map<String, Class> getXMLElementTypes(Class<?> type, String methodName) {
        Map<String, Class> toolboxTypes = new HashMap<>();

        try {
            Method getToolboxMethod = type.getMethod(methodName);
            for (Annotation annotation : getToolboxMethod.getAnnotations()) {
                if (annotation instanceof XmlElements) {
                    XmlElements elements = (XmlElements) annotation;
                    for (XmlElement element : elements.value()) {
                        toolboxTypes.put(element.name(), element.type());
                    }
                    break;
                }
            }
        } catch (NoSuchMethodException ignore) {
        }
        return toolboxTypes;
    }
}
