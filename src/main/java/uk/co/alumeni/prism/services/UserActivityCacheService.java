package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.event.UserActivityUpdateEvent;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.services.delegates.UserActivityCacheServiceDelegate;

@Service
public class UserActivityCacheService {

    private Set<Integer> executions = newHashSet();

    @Inject
    private UserActivityCacheServiceDelegate userActivityCacheServiceDelegate;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    public void updateUserActivityCaches(Object source, Resource resource, User currentUser, DateTime baseline) {
        applicationEventPublisher.publishEvent(new UserActivityUpdateEvent(resource)
                .withResource(new ResourceDTO().withScope(resource.getResourceScope()).withId(
                        resource.getId())).withCurrentUser(currentUser.getId()).withBaseline(baseline));
    }

    public void updateUserActivityCaches(Object source, Collection<Integer> users, User currentUser, DateTime baseline) {
        applicationEventPublisher.publishEvent(new UserActivityUpdateEvent(source).withUsers(newArrayList(users)).withCurrentUser(currentUser.getId())
                .withBaseline(baseline));
    }

    public synchronized void updateUserActivityCache(Integer user, DateTime baseline) {
        if (!executions.contains(user)) {
            executions.add(user);
            userActivityCacheServiceDelegate.updateUserActivityCache(user, baseline);
        }
    }

    public synchronized void updateUserActivityCache(Integer user) {
        executions.remove(user);
    }

}
