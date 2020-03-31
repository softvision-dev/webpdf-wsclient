package net.webpdf.wsclient.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;

public class SerializeHelper {

    private final static String OPERATION_SCHEMA = "/schemas/operation/operation.xsd";

    /**
     * Empty private constructor, for static calls and instantiation of and to instances of this helper class.
     */
    private SerializeHelper() {
    }

    /**
     * Returns the response content from the given http entity as a String for further processing.
     *
     * @param httpEntity The entity, that shall be searched for processable content.
     * @return The response String, that could be extracted.
     * @throws IOException Shall be thrown, when extracting the response content failed.
     */
    @NotNull
    private static String getResponseContent(@NotNull HttpEntity httpEntity) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String tempToken;
            StringBuilder response = new StringBuilder();

            while ((tempToken = bufferedReader.readLine()) != null) {
                response.append(tempToken);
            }
            return response.toString();
        }
    }

    /**
     * Deserialize the object to the given Type T from the xml {@link HttpEntity}.
     *
     * @param <T>        The type T of the target object.
     * @param httpEntity the xml {@link HttpEntity}
     * @param type       the type T of the target object.
     * @return An object of type T representing the xml {@link HttpEntity}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T> T fromXML(@Nullable HttpEntity httpEntity, @Nullable Class<T> type) throws ResultException {
        try {
            if (httpEntity == null) {
                throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
            }
            String response = EntityUtils.toString(httpEntity);

            try (StringReader stringReader = new StringReader(response)) {
                StreamSource streamSource = new StreamSource(stringReader);
                return fromXML(streamSource, type);
            }
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA, ex));
        }
    }

    /**
     * Deserialize the object to the given Type T from the xml {@link StreamSource}.
     *
     * @param <T>          The type T of the target object.
     * @param streamSource the xml {@link StreamSource}
     * @param type         the type T of the target object.
     * @return An object of type T representing the xml {@link StreamSource}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T> T fromXML(@Nullable StreamSource streamSource, @Nullable Class<T> type) throws ResultException {
        if (streamSource == null || type == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }
        XMLValidationEventHandler xmlValidationEventHandler = new XMLValidationEventHandler();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = SerializeHelper.class.getResource(OPERATION_SCHEMA);
            Schema schema = schemaFactory.newSchema(url);

            if (schema != null) {
                unmarshaller.setSchema(schema);
                unmarshaller.setEventHandler(xmlValidationEventHandler);
            }

            JAXBElement<T> jaxbElement = unmarshaller.unmarshal(streamSource, type);
            return type.cast(jaxbElement.getValue());

        } catch (SAXException | JAXBException ex) {
            Result result = Result.build(Error.INVALID_OPERATION_DATA, ex);
            result.addMessage(xmlValidationEventHandler.getMessages());
            throw new ResultException(result);
        }
    }

    /**
     * Deserialize the object to the given Type T from the json {@link HttpEntity}.
     *
     * @param <T>        The type T of the target object.
     * @param httpEntity the json {@link HttpEntity}
     * @param type       the type T of the target object.
     * @return An object of type T representing the json {@link HttpEntity}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T> T fromJSON(@Nullable HttpEntity httpEntity, @Nullable Class<T> type) throws ResultException {
        try {
            if (httpEntity == null) {
                throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
            }
            String response = SerializeHelper.getResponseContent(httpEntity);
            try (StringReader stringReader = new StringReader(response)) {
                StreamSource streamSource = new StreamSource(stringReader);
                return fromJSON(streamSource, type);
            }
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA, ex));
        }
    }

    /**
     * Deserialize the object to the given Type T from the json {@link StreamSource}.
     *
     * @param <T>          The type T of the target object.
     * @param streamSource the json {@link StreamSource}
     * @param type         the type T of the target object.
     * @return An object of type T representing the json {@link StreamSource}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T> T fromJSON(@Nullable StreamSource streamSource, @Nullable Class<T> type) throws ResultException {
        if (streamSource == null || type == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try (Reader reader = streamSource.getReader()) {
            return objectMapper.readValue(reader, type);
        } catch (IOException ex) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA, ex));
        }
    }

    /**
     * Serialize object to json
     *
     * @param object the object
     * @return the json {@link String}
     */
    @NotNull
    public static String toJSON(@Nullable Object object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Serialize object to xml
     *
     * @param object the object
     * @param type   the object type class
     * @return the xml {@link String}
     * @throws ResultException an {@link ResultException}
     */
    @NotNull
    public static String toXML(@Nullable Object object, @Nullable Class<?> type) throws ResultException {
        if (object == null || type == null) {
            throw new ResultException(Result.build(Error.TO_XML_JSON));
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(type);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter stringWriter = new StringWriter();
            jaxbMarshaller.marshal(object, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException ex) {
            throw new ResultException(Result.build(Error.TO_XML_JSON, ex));
        }
    }

}
