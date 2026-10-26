package seedu.address.testutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/** Runs UI assertions on the JavaFX application thread using one toolkit per test worker. */
public final class FxTestUtil {
    private static boolean initialized;

    private FxTestUtil() {
    }

    /** Starts JavaFX once and keeps it alive while individual test windows are closed. */
    private static synchronized void initialize() throws InterruptedException {
        if (initialized) {
            return;
        }
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        if (!started.await(20, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX toolkit did not start within 20 seconds.");
        }
        initialized = true;
    }

    /** Runs the action on the UI thread and propagates failed assertions to JUnit. */
    public static void runOnFxThread(Runnable action) throws Exception {
        initialize();
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
