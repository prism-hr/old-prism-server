package uk.co.alumeni.prism.services;

import static java.util.Collections.emptyList;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.InvitationDAO;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.delegates.NotificationServiceDelegate;

@Service
@Transactional
public class InvitationService {

    @Inject
    private InvitationDAO invitationDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationServiceDelegate notificationServiceDelegate;

    public Invitation createInvitation(User user) {
        return createInvitation(user, null);
    }

    public Invitation createInvitation(User user, String message) {
        Invitation invitation = new Invitation().withUser(user).withMessage(message);
        entityService.save(invitation);
        return invitation;
    }

    public <T extends InvitationEntity> List<Integer> getInvitationEntities(Class<T> invitationClass, PrismNotificationDefinition notificationDefinition) {
        if (!notificationServiceDelegate.getExecutionBatches().contains(notificationDefinition)) {
            return invitationDAO.getInvitationEntities(invitationClass);
        }
        return emptyList();
    }

}
