package com.zuehlke.pgadmissions.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;
import com.zuehlke.pgadmissions.rest.representation.CompetenceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("/api/targeting")
@PreAuthorize("permitAll")
public class TargetingController {

    @Inject
    private AdvertService advertService;

    @RequestMapping(value = "/target", method = POST)
    public void createTargets(@RequestBody ResourceRelationInvitationDTO resourceRelation) {
        advertService.createAdvertTargets(resourceRelation);
    }

    @RequestMapping(value = "/target", method = POST)
    public void acceptTarget(@RequestParam Integer advertTargetId, @RequestParam Boolean accept) {
        advertService.acceptAdvertTarget(advertTargetId, accept);
    }

    @RequestMapping(value = "/competences", method = GET)
    public List<CompetenceRepresentation> searchCompetences(@RequestParam String q) {
        return advertService.getCompetences(q);
    }

}
