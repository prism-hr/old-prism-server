package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private UserService userService;

    @Inject
    private UserMapper userMapper;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        return;
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    public void setUserActivityCache(Integer user, DateTime baseline) {
        if (!isShuttingDown()) {
            UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentationFresh(user);
            if (userActivityRepresentation != null) {
                userService.setUserActivityCache(user, userActivityRepresentation, baseline);
            }
        }
    }

}
