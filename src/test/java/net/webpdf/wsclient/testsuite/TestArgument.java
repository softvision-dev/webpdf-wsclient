package net.webpdf.wsclient.testsuite;

public enum TestArgument {
    PROTOCOL("protocol"),
    USERNAME("username"),
    PASSWORD("password"),
    SERVER_IP("serverip"),
    PORT("port"),
    SSL_PORT("sslport"),
    PATH("path");

    private final String key;

    TestArgument(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
