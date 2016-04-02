package uk.co.alumeni.prism.services.lifecycle.helpers;

import static org.joda.time.DateTime.now;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.services.CacheService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.UserService;

import com.google.common.collect.HashMultimap;

@Component
public class CacheServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private CacheService cacheService;
    
    @Inject
    private ResourceService resourceService;
    
    @Inject
    private UserService userService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        DateTime baseline = now();
        HashMultimap<PrismScope, Integer> resources = resourceService.getResourcesWithActivitiesToCache(baseline);
        for (PrismScope scope : resources.keySet()) {
            resources.get(scope).stream().forEach(resource -> {
                userService.getUsersWithActivitiesToCache(scope, resource, baseline).stream().forEach(user -> {
                    setUserActivityCache(user, baseline);
                });
            });
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void setUserActivityCache(Integer user, DateTime baseline) {
        if (!isShuttingDown()) {
            cacheService.updateUserActivityCache(user, baseline);
        }
    }

}
