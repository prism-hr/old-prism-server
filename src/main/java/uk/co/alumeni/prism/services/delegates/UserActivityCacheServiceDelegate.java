package uk.co.alumeni.prism.services.delegates;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.async.DeferredResult;

import uk.co.alumeni.prism.event.UserActivityUpdateEvent;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.UserActivityCacheService;
import uk.co.alumeni.prism.services.UserService;

@Service
public class UserActivityCacheServiceDelegate {

    private ConcurrentHashMap<Integer, DeferredResult<UserActivityRepresentation>> pollingUsers = new ConcurrentHashMap<>();

    @Inject
    private UserActivityCacheService userActivityCacheService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @Async
    @TransactionalEventListener
    public void updateUserActivityCaches(UserActivityUpdateEvent userActivityUpdateEvent) {
        List<Integer> users = userActivityUpdateEvent.getUsers();
        DateTime baseline = userActivityUpdateEvent.getBaseline();
        ResourceDTO resource = userActivityUpdateEvent.getResource();
        if (isEmpty(users) && resource != null) {
            users = newArrayList(userService.getUsersWithActivitiesToCache(resource.getScope(), singletonList(resource.getId()), baseline));
        }

        userActivityCacheService.updateUserActivityCache(userActivityUpdateEvent.getCurrentUser(), baseline);
        if (isNotEmpty(users)) {
            users.stream().forEach(user -> {
                userActivityCacheService.updateUserActivityCache(user, baseline);
            });
        }
    }

    public UserActivityRepresentation updateUserActivityCache(Integer userId, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(userId);
        userService.setUserActivityCache(userId, userActivityRepresentation, baseline);
        return userActivityRepresentation;
    }

    @Async
    public void updateUserActivityCacheAsynchronous(Integer userId, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = updateUserActivityCache(userId, baseline);
        userActivityCacheService.updateUserActivityCache(userId);
        DeferredResult<UserActivityRepresentation> result = pollingUsers.get(userId);
        if (result != null) {
            result.setResult(userActivityRepresentation);
        }
    }

    public void addPollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        pollingUsers.put(userId, result);
    }

    public void removePollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        pollingUsers.remove(userId, result);
    }

}
