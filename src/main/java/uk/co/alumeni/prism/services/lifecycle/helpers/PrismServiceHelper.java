package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

public interface PrismServiceHelper {

    void execute() throws Exception;

    void shutdown();

    boolean isShuttingDown();

    AtomicBoolean getShuttingDown();

}
