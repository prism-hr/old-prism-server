package uk.co.alumeni.prism.services;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;
import static uk.co.alumeni.prism.utils.PrismExecutorUtils.shutdownExecutor;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

@Service
@Transactional
public class CacheService {

    private Set<Integer> executions = newHashSet();

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

    public void updateUserActivityCaches(Resource resource, User currentUser, DateTime baseline) {
        updateUserActivityCaches(resource.getResourceScope(), resource.getId(), currentUser.getId(), baseline);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resource, User currentUser, DateTime baseline) {
        updateUserActivityCaches(scope, resource, currentUser.getId(), baseline);
    }

    public void updateUserActivityCaches(PrismScope scope, Integer resource, Integer currentUser, DateTime baseline) {
        updateUserActivityCache(currentUser, baseline);
        for (Integer user : userService.getUsersWithActivitiesToCache(scope, resource, baseline)) {
            updateUserActivityCache(user, baseline);
        }
    }

    public void updateUserActivityCaches(Collection<Integer> users, DateTime baseline) {
        users.stream().forEach(user -> updateUserActivityCache(user, baseline));
    }

    @Transactional(propagation = SUPPORTS)
    public synchronized void updateUserActivityCache(Integer user, DateTime baseline) {
        try {
            TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
            while (transactionStatus.isCompleted()) {
                scheduleUpdateUserActivityCache(user, baseline);
            }
        } catch (NoTransactionException e) {
            scheduleUpdateUserActivityCache(user, baseline);
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        shutdownExecutor(executorService);
    }

    private synchronized void scheduleUpdateUserActivityCache(Integer user, DateTime baseline) {
        if (!(shuttingDown.get() || executions.contains(user))) {
            executions.add(user);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    executeUpdateUserActivityCache(user, baseline);
                }
            });
        }
    }

    private synchronized void executeUpdateUserActivityCache(Integer user, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
        userService.setUserActivityCache(user, userActivityRepresentation, baseline);
        executions.remove(user);
    }

}
