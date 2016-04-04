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

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

@Service
public class CacheService {

    private Set<Integer> executions = newHashSet();

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    private ExecutorService executorService;

    @PostConstruct
    public void startup() {
        executorService = newFixedThreadPool(100);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resource, Integer currentUser, DateTime baseline) {
        setUserActivityCache(currentUser, baseline);
        for (Integer user : userService.getUsersWithActivitiesToCache(scope, resource, baseline)) {
            setUserActivityCache(user, baseline);
        }
    }

    public synchronized void setUserActivityCache(Integer user, DateTime baseline) {
        if (!(shuttingDown.get() || executions.contains(user))) {
            executions.add(user);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
                    userService.setUserActivityCache(user, userActivityRepresentation, baseline);
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
