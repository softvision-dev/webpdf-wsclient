package net.webpdf.wsclient;

import net.webpdf.wsclient.testsuite.server.TestServer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class BeforeAllTestsExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);
    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeAllTestsExtension.class);
    private static final TestServer TEST_SERVER = TestServer.getInstance();

    @Override
    public synchronized void beforeAll(final ExtensionContext context) {

        if (STARTED.compareAndSet(false, true)) {
            LOGGER.info("--------------------------------------------------");
            LOGGER.info("Start global resources");

            // global unique key for this class
            context.getRoot().getStore(GLOBAL).put("webpdf-tests", this);

            TEST_SERVER.startContainer();

            LOGGER.info("--------------------------------------------------");
        }
    }

    @Override
    public synchronized void close() {
        if (STARTED.get()) {
            LOGGER.info("--------------------------------------------------");
            LOGGER.info("Stop all global resources");

            TEST_SERVER.stopContainer();

            LOGGER.info("All global resources stopped");
            LOGGER.info("--------------------------------------------------");
        }
    }
}
