package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.dto.UserConnectionDTO;
import uk.co.alumeni.prism.dto.UserRoleCategoryDTO;
import uk.co.alumeni.prism.services.InvitationService;
import uk.co.alumeni.prism.services.NotificationService;

import com.google.common.collect.Sets;

@Component
public class NotificationServiceHelperInvitation extends PrismServiceHelperAbstract {

    @Inject
    private InvitationService invitationService;

    @Inject
    private NotificationService notificationService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        Set<UserRoleCategoryDTO> userRoleInvitations = Sets.newHashSet();
        invitationService.getInvitationEntities(UserRole.class).forEach(userRole -> {
            sendInvitationRequest(userRole, userRoleInvitations);
        });

        Set<UserConnectionDTO> advertTargetInvitations = Sets.newHashSet();
        invitationService.getInvitationEntities(AdvertTarget.class).forEach(advertTarget -> {
            sendConnectionRequest(advertTarget, advertTargetInvitations);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendInvitationRequest(Integer userRoleId, Set<UserRoleCategoryDTO> sent) {
        if (!isShuttingDown()) {
            notificationService.sendInvitationRequest(userRoleId, sent);
        }
    }

    private void sendConnectionRequest(Integer advertTargetId, Set<UserConnectionDTO> sent) {
        if (!isShuttingDown()) {
            notificationService.sendConnectionRequest(advertTargetId, sent);
        }
    }

}
