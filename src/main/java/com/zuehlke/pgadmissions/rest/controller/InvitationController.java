package com.zuehlke.pgadmissions.rest.controller;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

@RestController
@RequestMapping("api/invitations")
public class InvitationController {

    @Inject
    private ResourceService resourceService;

    @PreAuthorize("permitAll")
    @RequestMapping(method = RequestMethod.POST)
    public void inviteResource(@RequestBody ResourceRelationInvitationDTO invitationDTO) {
        resourceService.inviteResourceRelation(invitationDTO);
    }

}
