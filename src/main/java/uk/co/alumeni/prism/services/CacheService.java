package uk.co.alumeni.prism.services;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static uk.co.alumeni.prism.utils.PrismExecutorUtils.shutdownExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

@Service
public class CacheService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    private ExecutorService executorService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @PostConstruct
    public void startup() {
        executorService = newFixedThreadPool(10);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resourceId, Integer currentUser, DateTime baseline) {
        if (!shuttingDown.get()) {
            setUserActivityCache(currentUser, baseline);
            for (Integer user : userService.getUsersWithActivitiesToCache(scope, resourceId, baseline)) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        setUserActivityCache(user, baseline);
                    }
                });
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        shutdownExecutor(executorService);
    }

    private void setUserActivityCache(Integer user, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
        userService.setUserActivityCache(user, userActivityRepresentation, baseline);
    }

}
