package net.webpdf.wsclient.documents;

import org.apache.commons.io.input.CloseShieldInputStream;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class InputStreamDataSource implements DataSource {
    private InputStream inputStream;

    InputStreamDataSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new CloseShieldInputStream(inputStream);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return "";
    }
}