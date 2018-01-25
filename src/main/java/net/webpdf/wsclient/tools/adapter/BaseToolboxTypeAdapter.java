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

    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the specified type.
     * In the implementation of this call-back method, you should consider invoking
     * JsonSerializationContext.serialize(Object, Type) method to create JsonElements for any non-trivial field of the
     * src object. However, you should never invoke it on the src object itself since that will cause an infinite loop
     * (Gson will call your call-back method again).
     *
     * @param src       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context   The {@link JsonSerializationContext}
     * @return a JsonElement corresponding to the specified object.
     */
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

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the specified type.
     * In the implementation of this call-back method, you should consider invoking
     * JsonDeserializationContext.deserialize(JsonElement, Type) method to create objects for any non-trivial field of
     * the returned object. However, you should never invoke it on the the same type passing json since that will cause
     * an infinite loop (Gson will call your call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context The {@link JsonDeserializationContext}
     * @return a deserialized object of the specified type typeOfT which is a subclass of T
     * @throws JsonParseException if json is not in the expected format of typeofT
     */
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

    /**
     * Maps element descriptors to element types.
     *
     * @param type       The type, subtypes shall be extracted from.
     * @param methodName The methods, that shall be searched for element types.
     * @return A mapping of type descriptors to element types.
     */
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
