package uk.co.alumeni.prism.services.delegates;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.joda.time.DateTime;
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

    private final ConcurrentHashMap<Integer, DeferredResult<UserActivityRepresentation>> requests = new ConcurrentHashMap<>(8, 0.9f, 1);

    @Inject
    private UserActivityCacheService userActivityCacheService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @TransactionalEventListener
    public void updateUserActivityCaches(UserActivityUpdateEvent userActivityUpdateEvent) {
        List<Integer> users = userActivityUpdateEvent.getUsers();
        DateTime baseline = userActivityUpdateEvent.getBaseline();
        ResourceDTO resource = userActivityUpdateEvent.getResource();
        if (isEmpty(users) && resource != null) {
            users = newArrayList(userService.getUsersWithActivitiesToCache(resource.getScope(), singletonList(resource.getId()), baseline));
        }

        Integer currentUser = userActivityUpdateEvent.getCurrentUser();
        if (currentUser != null) {
            userActivityCacheService.updateUserActivityCache(currentUser, baseline);
        }

        if (isNotEmpty(users)) {
            users.stream().forEach(user -> {
                userActivityCacheService.updateUserActivityCache(user, baseline);
            });
        }
    }

    public UserActivityRepresentation updateUserActivityCache(Integer userId, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(userId);
        userService.setUserActivityCache(userId, userActivityRepresentation, baseline);
        DeferredResult<UserActivityRepresentation> result = requests.get(userId);
        if (result != null) {
            result.setResult(userActivityRepresentation);
        }
        return userActivityRepresentation;
    }

    public void addPollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        requests.put(userId, result);
    }

    public void removePollingUser(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        requests.remove(userId, result);
    }

}
