package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.UserConnectionDTO;
import com.zuehlke.pgadmissions.dto.UserRoleCategoryDTO;
import com.zuehlke.pgadmissions.services.InvitationService;
import com.zuehlke.pgadmissions.services.NotificationService;

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
