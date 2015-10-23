package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Invitation;

@Service
@Transactional
public class InvitationService {

    @Inject
    private EntityService entityService;

    public List<Invitation> getInvitations() {
        return entityService.getAll(Invitation.class);
    }

}
