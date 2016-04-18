package uk.co.alumeni.prism.services.delegates;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import uk.co.alumeni.prism.event.UserActivityUpdateEvent;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.UserActivityCacheService;
import uk.co.alumeni.prism.services.UserService;

@Service
public class UserActivityCacheServiceDelegate{

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
    public void updateUserActivityCache(Integer user, DateTime baseline) {
        UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
        userService.setUserActivityCache(user, userActivityRepresentation, baseline);
        userActivityCacheService.updateUserActivityCache(user);
    }

}
