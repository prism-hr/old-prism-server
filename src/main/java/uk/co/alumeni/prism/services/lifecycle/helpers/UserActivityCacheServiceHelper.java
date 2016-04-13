package uk.co.alumeni.prism.services.lifecycle.helpers;

import static org.joda.time.DateTime.now;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.UserActivityCacheService;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserActivityCacheServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private UserActivityCacheService userActivityCacheService;

    @Inject
    private UserService userService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        userService.getUsersWithActivitiesToCache(now()).stream().forEach(user -> updateUserActivityCache(user, now()));
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void updateUserActivityCache(Integer user, DateTime baseline) {
        if (!isShuttingDown()) {
            userActivityCacheService.updateUserActivityCache(user, baseline);
        }
    }

}
