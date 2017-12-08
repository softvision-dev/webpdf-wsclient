package net.webpdf.wsclient.testsuite;

import java.io.File;
import java.net.URL;

public class TestResources {
    private final String resourcePath;

    public TestResources(Class testClass) {
        this.resourcePath = testClass.getPackage().getName().replaceAll("\\.", "/");
    }

    public File getResource(String fileName) throws Exception {
        URL resUrl = this.getClass().getClassLoader().getResource(resourcePath + "/" + fileName);
        assert resUrl != null;
        return new File(resUrl.getFile());
    }

    public File getResourceByPath(String fullPath) throws Exception {
        URL resUrl = this.getClass().getClassLoader().getResource(fullPath);
        assert resUrl != null;
        return new File(resUrl.getFile());
    }

    public TestArguments getArguments() {
        return new TestArguments();
    }

    public TestArguments getArguments(boolean useCredentials, boolean useSSLPorts) {
        return new TestArguments().setUseCredentials(useCredentials).setUseSSLPort(useSSLPorts);
    }
}
