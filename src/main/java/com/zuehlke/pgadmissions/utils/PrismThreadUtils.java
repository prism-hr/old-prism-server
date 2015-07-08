package com.zuehlke.pgadmissions.utils;

import java.util.Collection;
import java.util.List;

public class PrismThreadUtils {

    public static void dispatchThread(Collection<Thread> workers, Runnable runner) {
        Thread parentScopeThread = new Thread(runner);
        parentScopeThread.start();
        workers.add(parentScopeThread);
    }
    
    public static void concludeThreads(List<Thread> workers) throws InterruptedException {
        for (Thread worker : workers) {
            worker.join();
        }
    }
    
}
