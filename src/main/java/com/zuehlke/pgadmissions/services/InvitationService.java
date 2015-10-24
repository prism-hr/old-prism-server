package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InvitationDAO;
import com.zuehlke.pgadmissions.domain.InvitationEntity;

@Service
@Transactional
public class InvitationService {

    @Inject
    private InvitationDAO invitationDAO;

    public <T extends InvitationEntity> List<Integer> getInvitationEntities(Class<T> invitationClass) {
        return invitationDAO.getInvitationEntities(invitationClass);
    }

}
