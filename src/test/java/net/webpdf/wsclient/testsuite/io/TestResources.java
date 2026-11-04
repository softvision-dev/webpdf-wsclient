package net.webpdf.wsclient.testsuite.io;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

public class TestResources {

    /**
     * The temporary folders are created outside of a JUnit extension lifecycle, so their {@code afterEach} cleanup
     * never runs. They are collected here and removed once, when the test JVM ends.
     */
    private static final @NotNull Queue<File> TEMPORARY_FOLDERS = new ConcurrentLinkedQueue<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> TEMPORARY_FOLDERS.forEach(FileUtils::deleteQuietly), "test-resources-cleanup"));
    }

    private final @NotNull String resourcePath;
    private final @NotNull TemporaryFolderExtension temporaryFolderExtension = new TemporaryFolderExtension();

    public TestResources(@NotNull Class<?> testClass) {
        this.resourcePath = testClass.getPackage().getName().replaceAll("\\.", "/");
        temporaryFolderExtension.create();
        TEMPORARY_FOLDERS.add(temporaryFolderExtension.getRoot());
    }

    public @NotNull File getResource(String fileName) {
        URL resUrl = this.getClass().getClassLoader().getResource(resourcePath + "/" + fileName);
        assertNotNull(resUrl, "Test resource not found: " + resourcePath + "/" + fileName);
        // Resolve through the URI: URL#getFile() keeps percent-encoding (spaces, umlauts) in the path.
        return assertDoesNotThrow(() -> Paths.get(resUrl.toURI()).toFile());
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
