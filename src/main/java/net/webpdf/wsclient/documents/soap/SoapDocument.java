package net.webpdf.wsclient.documents.soap;

import net.webpdf.wsclient.documents.Document;
import org.jetbrains.annotations.Nullable;

import javax.activation.DataHandler;
import java.io.*;

public interface SoapDocument extends Document, AutoCloseable{

    /**
     * Returns a {@link DataHandler} for the document's source.
     *
     * @return A {@link DataHandler} for the document's source.
     */
    @Nullable
    DataHandler getSourceDataHandler();

    /**
     * Attempts to write the SOAP response document to the given {@link DataHandler}.
     *
     * @param resultDataHandler The {@link DataHandler} the SOAP response document shall be written to.
     * @throws IOException a {@link IOException}
     */
    void save(@Nullable DataHandler resultDataHandler) throws IOException;

}
