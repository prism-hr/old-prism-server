package uk.co.alumeni.prism.rest.controller;

import static org.apache.commons.lang3.StringUtils.removeEnd;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationExtended;
import uk.co.alumeni.prism.services.AdvertService;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

    @RequestMapping(method = RequestMethod.GET)
    public AdvertListRepresentation getAdverts(OpportunitiesQueryDTO query) {
        return advertMapper.getAdvertExtendedRepresentations(query);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|departments|institutions}/{resourceId}")
    public AdvertRepresentationExtended getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(PrismScope.valueOf(removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        if (advert == null) {
            throw new ResourceNotFoundException("Advert not found");
        }
        return advertMapper.getAdvertRepresentationExtended(advert);
    }

}
