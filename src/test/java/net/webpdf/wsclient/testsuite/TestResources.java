package net.webpdf.wsclient.testsuite;

import java.io.File;
import java.net.URL;

public class TestResources {

    private final String resourcePath;

    public TestResources(Class testClass) {
        this.resourcePath = testClass.getPackage().getName().replaceAll("\\.", "/");
    }

    public File getResource(String fileName) {
        URL resUrl = this.getClass().getClassLoader().getResource(resourcePath + "/" + fileName);
        assert resUrl != null;
        return new File(resUrl.getFile());
    }
}
