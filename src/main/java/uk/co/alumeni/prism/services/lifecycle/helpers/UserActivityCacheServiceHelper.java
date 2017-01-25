package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.UserActivityCacheService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class UserActivityCacheServiceHelper extends PrismServiceHelperAbstract {
    
    @Inject
    private UserActivityCacheService userActivityCacheService;
    
    @Inject
    private UserService userService;
    
    private AtomicBoolean shuttingDown = new AtomicBoolean(false);
    
    @Override
    public void execute() throws Exception {
        userService.getUsersWithActivitiesToCache(DateTime.now()).stream().forEach(user -> updateUserActivityCache(user));
    }
    
    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }
    
    private void updateUserActivityCache(Integer user) {
        if (!isShuttingDown()) {
            userActivityCacheService.updateUserActivityCache(user);
        }
    }
    
}
