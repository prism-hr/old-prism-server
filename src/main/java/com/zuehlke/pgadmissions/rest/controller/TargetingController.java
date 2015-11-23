package com.zuehlke.pgadmissions.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConnectionInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConnectionInvitationsDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.representation.CompetenceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("/api/targeting")
@PreAuthorize("permitAll")
public class TargetingController {

    @Inject
    private AdvertService advertService;

    @RequestMapping(value = "/targets", method = POST)
    public void createTarget(@RequestBody ResourceConnectionInvitationDTO resourceConnection) {
        ResourceCreationDTO invitingResource = resourceConnection.getInvitingResource();
        advertService.createAdvertTarget(invitingResource, resourceConnection.getReceivingResource());
    }

    @RequestMapping(value = "/targets/batch", method = POST)
    public void createTarget(@RequestBody ResourceConnectionInvitationsDTO resourceConnections) {
        advertService.createAdvertTargetPending(resourceConnections);
    }

    @RequestMapping(value = "/targets/{targetId}/{decision:accept|reject}", method = POST)
    public void updateTarget(@PathVariable Integer targetId, @PathVariable String decision, @RequestBody Map<?, ?> undertow) {
        boolean severed = decision.equals("reject");
        advertService.updateAdvertTarget(targetId, severed);
    }

    @RequestMapping(value = "/competences", method = GET)
    public List<CompetenceRepresentation> searchCompetences(@RequestParam String q) {
        return advertService.getCompetences(q);
    }

}
