package uk.co.alumeni.prism.services;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static uk.co.alumeni.prism.utils.PrismExecutorUtils.shutdownExecutor;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private ExecutorService executorService;

    private Set<Integer> executions = newHashSet();

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Inject
    private UserService userService;

    @PostConstruct
    public void startup() {
        executorService = newFixedThreadPool(100);
    }

    public synchronized void updateUserActivityCache(Integer user, DateTime baseline) {
        if (!(shuttingDown.get() || executions.contains(user))) {
            executions.add(user);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    userService.updateUserActivityCache(user, baseline);
                    executions.remove(user);
                }
            });
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        shutdownExecutor(executorService);
    }

}
