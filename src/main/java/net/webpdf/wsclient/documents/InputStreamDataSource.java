package net.webpdf.wsclient.documents;

import org.apache.commons.io.input.CloseShieldInputStream;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.OutputStream;

class InputStreamDataSource implements DataSource {
    private InputStream inputStream;

    /**
     * Prepares the given resource for further processing.
     *
     * @param inputStream an {@link InputStream} containing the resource.
     */
    InputStreamDataSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Returns the {@link InputStream} containing the resource data represented by this data source.
     *
     * @return The {@link InputStream} containing the resource data.
     */
    @Override
    public InputStream getInputStream() {
        return new CloseShieldInputStream(inputStream);
    }

    /**
     * Writing to the managed resource is unsupported.
     *
     * @return an {@link OutputStream}
     */
    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the MIME-type of the contained resource.
     *
     * @return The MIME-type of the contained resource.
     */
    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    /**
     * Returns the name of the contained resource.
     *
     * @return The name of the contained resource.
     */
    @Override
    public String getName() {
        return "";
    }
}