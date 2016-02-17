package uk.co.alumeni.prism.utils;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrismExecutorUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismExecutorUtils.class);

    public static void shutdownExecutor(ExecutorService executorService) {
        try {
            executorService.shutdownNow();
        } catch (Exception e) {
            logger.error("Error shutting down maintenance task executor", e);
        }
    }

}
