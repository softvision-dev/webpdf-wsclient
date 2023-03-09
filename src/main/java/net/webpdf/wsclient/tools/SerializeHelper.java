package net.webpdf.wsclient.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.DataFormat;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;

import jakarta.xml.bind.*;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;

/**
 * The {@link SerializeHelper} provides a set of tools, to translate the server´s responses and data transfer objects,
 * which may be provided using one of the defined {@link DataFormat}s.
 */
public class SerializeHelper {

    private final static String OPERATION_SCHEMA = "/schemas/operation/operation.xsd";

    /**
     * This class is not intended to be instantiated, use the static methods instead.
     */
    private SerializeHelper() {
    }

    /**
     * Returns the response content from the given {@link HttpEntity} as a String for further processing.
     *
     * @param httpEntity The {@link HttpEntity}, that shall be searched for processable content.
     * @return The extracted response String.
     * @throws ResultException Shall be thrown, when extracting the response content failed.
     */
    private static @NotNull String getResponseContent(@NotNull HttpEntity httpEntity) throws ResultException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String tempToken;
            StringBuilder response = new StringBuilder();

            while ((tempToken = bufferedReader.readLine()) != null) {
                response.append(tempToken);
            }
            return response.toString();
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA, ex);
        }
    }

    /**
     * Deserialize the data transfer object to the given type from the given {@link DataFormat#XML} {@link HttpEntity}.
     *
     * @param <T>        The expected type of the deserialized data transfer object.
     * @param httpEntity The deserializable {@link DataFormat#XML} {@link HttpEntity}
     * @param type       The expected type of the deserialized data transfer object.
     * @return The deserialized {@link DataFormat#XML} data transfer object.
     * @throws ResultException Shall be thrown upon a deserialization failure.
     */
    public static <T> @NotNull T fromXML(@Nullable HttpEntity httpEntity, @NotNull Class<T> type)
            throws ResultException {
        try {
            if (httpEntity == null) {
                throw new ClientResultException(Error.INVALID_OPERATION_DATA);
            }
            String response = EntityUtils.toString(httpEntity);

            try (StringReader stringReader = new StringReader(response)) {
                StreamSource streamSource = new StreamSource(stringReader);
                return fromXML(streamSource, type);
            }
        } catch (IOException | ParseException ex) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA, ex);
        }
    }

    /**
     * Deserialize the data transfer object to the given type from the given {@link DataFormat#XML}
     * {@link StreamSource}.
     *
     * @param <T>          The expected type of the deserialized data transfer object.
     * @param streamSource The deserializable {@link DataFormat#XML} {@link StreamSource}
     * @param type         The expected type of the deserialized data transfer object.
     * @return The deserialized {@link DataFormat#XML} data transfer object.
     * @throws ResultException Shall be thrown upon a deserialization failure.
     */
    public static <T> @NotNull T fromXML(@Nullable StreamSource streamSource, @NotNull Class<T> type)
            throws ResultException {
        if (streamSource == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }
        XMLValidationEventHandler xmlValidationEventHandler = new XMLValidationEventHandler();

        try {
            // create a new instance with a list of allowed classes for this context
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = SerializeHelper.class.getResource(OPERATION_SCHEMA);
            Schema schema = schemaFactory.newSchema(url);

            if (schema != null) {
                unmarshaller.setSchema(schema);
                unmarshaller.setEventHandler(xmlValidationEventHandler);
            }

            // unmarshall the given stream with a specific class (ignores @XmlRootElement annotated class name)
            JAXBElement<T> jaxbElement = unmarshaller.unmarshal(streamSource, type);
            return type.cast(jaxbElement.getValue());

        } catch (SAXException | JAXBException ex) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA, ex)
                    .appendMessage(xmlValidationEventHandler.getMessages());
        }
    }

    /**
     * Deserialize the data transfer object to the given type from the given {@link DataFormat#JSON} {@link HttpEntity}.
     *
     * @param <T>        The expected type of the deserialized data transfer object.
     * @param httpEntity The deserializable {@link DataFormat#JSON} {@link HttpEntity}
     * @param type       The expected type of the deserialized data transfer object.
     * @return The deserialized {@link DataFormat#JSON} data transfer object.
     * @throws ResultException Shall be thrown upon a deserialization failure.
     */
    public static <T> @NotNull T fromJSON(@Nullable HttpEntity httpEntity, @NotNull Class<T> type)
            throws ResultException {
        if (httpEntity == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }
        String response = SerializeHelper.getResponseContent(httpEntity);
        try (StringReader stringReader = new StringReader(response)) {
            StreamSource streamSource = new StreamSource(stringReader);
            return fromJSON(streamSource, type);
        }
    }

    /**
     * Deserialize the data transfer object to the given type from the given {@link DataFormat#JSON}
     * {@link StreamSource}.
     *
     * @param <T>          The expected type of the deserialized data transfer object.
     * @param streamSource The deserializable {@link DataFormat#JSON} {@link StreamSource}
     * @param type         The expected type of the deserialized data transfer object.
     * @return The deserialized {@link DataFormat#JSON} data transfer object.
     * @throws ResultException Shall be thrown upon a deserialization failure.
     */
    public static <T> @NotNull T fromJSON(@Nullable StreamSource streamSource, @NotNull Class<T> type)
            throws ResultException {
        if (streamSource == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JakartaXmlBindAnnotationModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (Reader reader = streamSource.getReader()) {
            return objectMapper.readValue(reader, type);
        } catch (IOException ex) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA, ex);
        }
    }

    /**
     * Serialize a data transfer object to {@link DataFormat#JSON}
     *
     * @param object The object to serialize.
     * @return The resulting {@link DataFormat#JSON} {@link String}.
     * @throws ResultException Shall be thrown upon a serialization failure.
     */
    @NotNull
    public static String toJSON(@Nullable Object object) throws ResultException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JakartaXmlBindAnnotationModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new ClientResultException(Error.TO_XML_JSON, ex);
        }
    }

    /**
     * Serialize a data transfer object to {@link DataFormat#XML}.
     *
     * @param object The data transfer object to serialize.
     * @param type   The annotated type of the data transfer object.
     * @return The resulting {@link DataFormat#XML} {@link String}.
     * @throws ResultException Shall be thrown upon a serialization failure.
     */
    public static @NotNull String toXML(@Nullable Object object, @NotNull Class<?> type) throws ResultException {
        if (object == null) {
            throw new ClientResultException(Error.TO_XML_JSON);
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
            throw new ClientResultException(Error.TO_XML_JSON, ex);
        }
    }

}
