package uk.co.alumeni.prism.services.delegates;

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

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

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
        if (isEmpty(users)) {
            ResourceDTO resource = userActivityUpdateEvent.getResource();
            users = newArrayList(userService.getUsersWithActivitiesToCache(resource.getScope(), singletonList(resource.getId()), baseline));
        }

        userActivityCacheService.updateUserActivityCache(userActivityUpdateEvent.getCurrentUser(), baseline);
        users.stream().forEach(user -> {
            userActivityCacheService.updateUserActivityCache(user, baseline);
        });
    }

    @Async
    public void updateUserActivityCache(Integer userId, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(userId);
        userService.setUserActivityCache(userId, userActivityRepresentation, baseline);
        userActivityCacheService.updateUserActivityCache(userId);
        DeferredResult<UserActivityRepresentation> result = pollingUsers.get(userId);
        if (result != null) {
            result.setResult(userActivityRepresentation);
        }
    }

    public void removePollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        pollingUsers.remove(userId, result);
    }

    public void addPollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        pollingUsers.put(userId, result);
    }
}
