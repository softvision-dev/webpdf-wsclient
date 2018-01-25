package net.webpdf.wsclient.tools.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Converts Enum values for JSON parameters
 */
public class EnumAdapterFactory implements TypeAdapterFactory {

    /**
     * Register the adapter for each enum type
     *
     * @param gson gson object
     * @param type current object
     * @param <T>  data type
     * @return enum adapter to register or null
     */
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumTypeAdapter<>();
        }
        return null;
    }

    /**
     * Enum adapter
     *
     * @param <T> type of enum
     */
    class EnumTypeAdapter<T> extends TypeAdapter<T> {

        public void write(JsonWriter out, T value) throws IOException {
            if (out == null) {
                return;
            }

            if (value == null) {
                out.nullValue();
                return;
            }

            try {
                // call the 'value' method for the enum
                Class<?> valueClass = value.getClass();
                Method method = valueClass.getMethod("value");
                String data = (String) method.invoke(value);
                out.value(data);
            } catch (Exception ex) {
                out.nullValue();
            }
        }

        public T read(JsonReader in) {
            // we don't read any JSON value
            return null;
        }
    }
}
