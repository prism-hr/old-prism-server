package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRegionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.InstitutionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;

@RequestMapping("api/domiciles/{domicileId}")
@RestController
public class InstitutionDomicileResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(value = "institutions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<InstitutionRepresentation> getInstitutions(@PathVariable String domicileId) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", domicileId);
        List<Institution> institutions = institutionService.listByCountry(domicile);
        List<InstitutionRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            institutionRepresentations.add(dozerBeanMapper.map(institution, InstitutionRepresentation.class));
        }
        return institutionRepresentations;
    }

    @RequestMapping(value = "regions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<InstitutionDomicileRegionRepresentation> getRegions(@PathVariable String domicileId) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", domicileId);
        List<InstitutionDomicileRegion> regions = institutionService.getTopLevelRegions(domicile);

        List<InstitutionDomicileRegionRepresentation> regionRepresentations = Lists.newArrayListWithCapacity(regions.size());
        for (InstitutionDomicileRegion region : regions) {
            regionRepresentations.add(dozerBeanMapper.map(region, InstitutionDomicileRegionRepresentation.class));
        }

        return regionRepresentations;
    }

}