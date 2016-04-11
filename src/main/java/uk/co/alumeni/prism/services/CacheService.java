package uk.co.alumeni.prism.services;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static uk.co.alumeni.prism.utils.PrismExecutorUtils.shutdownExecutor;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

@Service
public class CacheService {

    private ExecutorService executorService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @PostConstruct
    public void startup() {
        executorService = newFixedThreadPool(100);
    }

    public void updateUserActivityCaches(Resource resource, User currentUser, DateTime baseline, TransactionStatus transactionStatus) {
        updateUserActivityCaches(resource.getResourceScope(), resource.getId(), currentUser.getId(), baseline, transactionStatus);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resource, User currentUser, DateTime baseline, TransactionStatus transactionStatus) {
        updateUserActivityCaches(scope, resource, currentUser.getId(), baseline, transactionStatus);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resource, Integer currentUser, DateTime baseline, TransactionStatus transactionStatus) {
        updateUserActivityCaches(userService.getUsersWithActivitiesToCache(scope, resource, baseline), currentUser, baseline, transactionStatus);
    }

    public void updateUserActivityCaches(Collection<Integer> users, Integer currentUser, DateTime baseline, TransactionStatus transactionStatus) {
        if (transactionStatus.isCompleted()) {
            updateUserActivityCache(currentUser, baseline);
            users.stream().forEach(user -> {
                if (!user.equals(currentUser)) {
                    updateUserActivityCache(user, baseline);
                }
            });
        } else {
            updateUserActivityCaches(users, currentUser, baseline, transactionStatus);
        }
    }

    public synchronized void updateUserActivityCache(Integer user, DateTime baseline) {
        if (!shuttingDown.get()) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    executeUpdateUserActivityCache(user, baseline);
                }
            });
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        shutdownExecutor(executorService);
    }

    private synchronized void executeUpdateUserActivityCache(Integer user, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
        userService.setUserActivityCache(user, userActivityRepresentation, baseline);
    }

}
