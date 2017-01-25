package uk.co.alumeni.prism.services.delegates;

import com.google.common.collect.HashMultimap;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.async.DeferredResult;
import uk.co.alumeni.prism.event.UserActivityUpdateEvent;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.controller.UserController;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.UserActivityCacheService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class UserActivityCacheServiceDelegate {
    
    private final HashMultimap<Integer, DeferredResult<UserActivityRepresentation>> requests = HashMultimap.create();
    
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
        
        Integer currentUser = userActivityUpdateEvent.getCurrentUser();
        if (currentUser != null) {
            userActivityCacheService.updateUserActivityCache(currentUser);
        }
        
        if (isNotEmpty(users)) {
            users.stream().forEach(user -> {
                userActivityCacheService.updateUserActivityCache(user);
            });
        }
    }
    
    public UserActivityRepresentation updateUserActivityCache(Integer userId) {
        UserActivityRepresentation representation = userMapper.getUserActivityRepresentationFresh(userId);
        synchronized (this) {
            requests.removeAll(userId).forEach(result -> {
                result.setResult(representation);
            });
        }
        
        return representation;
    }
    
    public synchronized void addRequest(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        requests.put(userId, result);
    }
    
    public synchronized void processRequestTimeout(Integer userId, DeferredResult<UserActivityRepresentation> result) {
        requests.remove(userId, result);
        result.setErrorResult(new UserController.UserActivityNotModifiedException());
    }
    
}
