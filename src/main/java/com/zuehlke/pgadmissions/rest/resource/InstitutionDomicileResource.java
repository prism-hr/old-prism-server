package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.rest.domain.InstitutionDomicileRegionRepresentation;
import com.zuehlke.pgadmissions.rest.domain.InstitutionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/domiciles/{domicileId}")
@RestController
public class InstitutionDomicileResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @RequestMapping(value = "institutions", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<InstitutionRepresentation> getInstitutions(@PathVariable String domicileId) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", domicileId);
        List<Institution> institutions = entityService.listByProperty(Institution.class, "domicile", domicile);
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
