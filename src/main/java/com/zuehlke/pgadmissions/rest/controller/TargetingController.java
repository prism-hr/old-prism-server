package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConnectionInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.representation.CompetenceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/targeting")
@PreAuthorize("permitAll")
public class TargetingController {

    @Inject
    private AdvertService advertService;

    @RequestMapping(value = "/targets", method = POST)
    public void createTarget(@RequestBody ResourceConnectionInvitationDTO resourceConnection) {
        ResourceDTO invitingResource = resourceConnection.getInvitingResource();
        advertService.createAdvertTarget(invitingResource.getScope(), invitingResource.getId(), resourceConnection.getReceivingResource());
    }

    @RequestMapping(value = "/targets/{targetId}/{decision:accept|reject}", method = POST)
    public void updateTarget(@PathVariable Integer targetId, @PathVariable String decision) {
        boolean accept = decision.equals("accept");
        advertService.updateAdvertTarget(targetId, accept);
    }

    @RequestMapping(value = "/competences", method = GET)
    public List<CompetenceRepresentation> searchCompetences(@RequestParam String q) {
        return advertService.getCompetences(q);
    }

}
