package net.webpdf.wsclient.testsuite;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class TemporaryFolderExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback,
        AfterAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemporaryFolderExtension.class);
    private final boolean useParentFolderForAllTests;
    private File parentFolder;
    private File folder;
    private String displayName;

    public TemporaryFolderExtension() {
        this(false);
    }

    public TemporaryFolderExtension(boolean useParentFolderForAllTests) {
        this.useParentFolderForAllTests = useParentFolderForAllTests;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (this.parentFolder != null) {
            recursiveDelete(this.parentFolder);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        this.displayName = context.getDisplayName();
        if (this.useParentFolderForAllTests) {
            this.parentFolder = createTemporaryFolderIn(null);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        delete();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        this.displayName = extensionContext.getDisplayName();
        create();
    }

    // testing purposes only

    /**
     * for testing purposes only. Do not use.
     */
    public void create() {
        assertDoesNotThrow(() -> folder = createTemporaryFolderIn(parentFolder));
    }

    /**
     * Returns a new fresh file with the given name under the temporary temporaryFolderExtension.
     */
    public File newFile(String fileName) {
        File file = new File(getRoot(), fileName);
        if (assertDoesNotThrow(() -> !file.createNewFile())) {
            fail(
                    "a file with the name '" + fileName + "' already exists in the test temporaryFolderExtension");
        }
        return file;
    }

    /**
     * Returns a new fresh file with a random name under the temporary temporaryFolderExtension.
     */
    public File newFile() {
        return assertDoesNotThrow(() -> File.createTempFile("junit", null, getRoot()));
    }

    /**
     * Returns a new fresh temporaryFolderExtension with the given name under the temporary
     * temporaryFolderExtension.
     */
    public File newFolder(String folder) {
        return newFolder(new String[]{folder});
    }

    /**
     * Returns a new fresh temporaryFolderExtension with the given name(s) under the temporary
     * temporaryFolderExtension.
     */
    public File newFolder(String... folderNames) {
        File file = getRoot();
        for (int i = 0; i < folderNames.length; i++) {
            String folderName = folderNames[i];
            validateFolderName(folderName);
            file = new File(file, folderName);
            if (!file.mkdir() && isLastElementInArray(i, folderNames)) {
                fail("a temporaryFolderExtension with the name '" + folderName + "' already exists");
            }
        }
        return file;
    }

    /**
     * Validates if multiple path components were used while creating a temporaryFolderExtension.
     *
     * @param folderName Name of the temporaryFolderExtension being created
     */
    private void validateFolderName(String folderName) {
        File tempFile = new File(folderName);
        if (tempFile.getParent() != null) {
            fail("Folder name cannot consist of multiple path components separated by a file separator."
                    + " Please use newFolder('MyParentFolder','MyFolder') to create hierarchies of folders");
        }
    }

    private boolean isLastElementInArray(int index, String[] array) {
        return index == array.length - 1;
    }

    /**
     * Returns a new fresh temporaryFolderExtension with a random name under the temporary temporaryFolderExtension.
     */
    public File newFolder() {
        return createTemporaryFolderIn(getRoot());
    }

    private File createTemporaryFolderIn(@Nullable File parentFolder) {
        File createdFolder = assertDoesNotThrow(() ->
                File.createTempFile("junit", "", parentFolder));
        assertTrue(createdFolder.delete());
        assertTrue(createdFolder.mkdir());
        return createdFolder;
    }

    /**
     * @return the location of this temporary temporaryFolderExtension.
     */
    public File getRoot() {
        if (folder == null) {
            fail("the temporary temporaryFolderExtension has not yet been created");
        }
        return folder;
    }

    /**
     * @return the location of this temporary temporaryFolderExtension.
     */
    public File getParentRoot() {
        if (this.parentFolder == null) {
            fail("the temporary temporaryFolderExtension has not yet been created");
        }
        return this.parentFolder;
    }

    /**
     * Delete all files and folders under the temporary temporaryFolderExtension.
     */
    public void delete() {
        if (folder != null) {
            recursiveDelete(folder);
        }
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        if (!FileUtils.deleteQuietly(file)) {
            LOGGER.error("{}: Unable to delete {}", this.displayName, file.getAbsolutePath());
        }
    }

}