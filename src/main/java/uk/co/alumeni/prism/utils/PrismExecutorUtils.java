package uk.co.alumeni.prism.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

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
