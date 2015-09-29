package com.zuehlke.pgadmissions.rest.controller;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("api/target")
@PreAuthorize("isAuthenticated()")
public class AdvertTargetController {

    @Inject
    private AdvertService advertService;

    @RequestMapping(method = RequestMethod.PUT)
    public void target(@RequestBody ResourceRelationInvitationDTO resourceRelation) {
        advertService.getOrCreateAdvertTargets(resourceRelation);
    }

    @RequestMapping(value = "/accept/{advertTargetId}", method = RequestMethod.PUT)
    public void target(@PathVariable Integer advertTargetId, @RequestParam Boolean accept) {
        advertService.acceptAdvertTarget(advertTargetId, accept);
    }

}
