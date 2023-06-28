package net.webpdf.wsclient.testsuite.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class TestResources {

    private final @NotNull String resourcePath;
    private final @NotNull TemporaryFolderExtension temporaryFolderExtension = new TemporaryFolderExtension();

    public TestResources(@NotNull Class<?> testClass) {
        this.resourcePath = testClass.getPackage().getName().replaceAll("\\.", "/");
        temporaryFolderExtension.create();
    }

    public @NotNull File getResource(String fileName) {
        URL resUrl = this.getClass().getClassLoader().getResource(resourcePath + "/" + fileName);
        assert resUrl != null;
        File resource = new File(resUrl.getFile());
        assertNotNull(resource);
        return resource;
    }

    public @NotNull TemporaryFolderExtension getTempFolder() {
        return temporaryFolderExtension;
    }

    /**
     * <p>
     * This method makes sure to terminate a possibly created {@link AutoCloseable} resource and fails the test.
     * </p>
     * <p>
     * This is useful, should you just want to test for occurring exceptions upon resource creation, without the
     * intention to actually use the resource itself.<br>
     * This is a fallback behaviour should an expected Exception not occur.
     * </p>
     *
     * @param resource The {@link AutoCloseable} to terminate.
     */
    public static void fallbackFailAndClose(@Nullable AutoCloseable resource) {
        if (resource != null) {
            assertDoesNotThrow(resource::close);
        }
        fail("Resource creation should have failed with an Exception.");
    }
}
