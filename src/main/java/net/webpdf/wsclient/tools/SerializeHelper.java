package net.webpdf.wsclient.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.tools.adapter.BaseToolboxTypeAdapter;
import net.webpdf.wsclient.tools.adapter.EnumAdapterFactory;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
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

    private SerializeHelper() {
    }

    private static String getResponseContent(HttpEntity httpEntity) throws IOException {
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

    public static <T> T fromXML(HttpEntity httpEntity, Class<T> type) throws ResultException {
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

    public static <T> T fromXML(StreamSource streamSource, Class<T> type) throws ResultException {
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

    public static <T> T fromJSON(HttpEntity httpEntity, Class<T> type) throws ResultException {
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

    public static <T> T fromJSON(StreamSource streamSource, Class<T> type) throws ResultException {
        if (streamSource == null || type == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder
                    .registerTypeAdapter(BaseToolboxType.class, new BaseToolboxTypeAdapter())
                    .create();

            return gson.fromJson(streamSource.getReader(), type);
        } catch (JsonParseException ex) {
            Result result = Result.build(Error.INVALID_OPERATION_DATA, ex);
            result.addMessage(ex.getMessage());
            throw new ResultException(result);
        }
    }

    /**
     * Serialize object to json
     *
     * @param object the object
     * @return the json {@link String}
     */
    public static String toJSON(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .registerTypeAdapterFactory(new EnumAdapterFactory())
                .registerTypeAdapter(BaseToolboxType.class, new BaseToolboxTypeAdapter())
                .create();
        return gson.toJson(object);
    }

    /**
     * Serialize object to xml
     *
     * @param object the object
     * @param type   the object type class
     * @return the xml {@link String}
     */
    public static String toXML(Object object, Class type) throws ResultException {
        if (object == null || type == null) {
            throw new ResultException(Result.build(Error.TO_XML));
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
            throw new ResultException(Result.build(Error.TO_XML, ex));
        }
    }
}
