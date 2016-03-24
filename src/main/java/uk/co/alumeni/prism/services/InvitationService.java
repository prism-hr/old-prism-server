package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.InvitationDAO;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.user.User;

@Service
@Transactional
public class InvitationService {

    @Inject
    private InvitationDAO invitationDAO;

    @Inject
    private EntityService entityService;

    public Invitation createInvitation(User user) {
        return createInvitation(user, null);
    }

    public Invitation createInvitation(User user, String message) {
        Invitation invitation = new Invitation().withUser(user).withMessage(message);
        entityService.save(invitation);
        return invitation;
    }

    public <T extends InvitationEntity> List<Integer> getInvitationEntities(Class<T> invitationClass) {
        return invitationDAO.getInvitationEntities(invitationClass);
    }

}
