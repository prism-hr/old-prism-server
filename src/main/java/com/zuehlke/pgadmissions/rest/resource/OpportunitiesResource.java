package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramLocation;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunitiesResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<AdvertRepresentation> getAdverts(OpportunitiesQueryDTO query) {
        List<Advert> adverts = advertService.getActiveAdverts();
        List<AdvertRepresentation> representations = Lists.newArrayListWithExpectedSize(adverts.size());
        for (Advert advert : adverts) {
            AdvertRepresentation representation = dozerBeanMapper.map(advert, AdvertRepresentation.class);

            Resource resource = advert.getProgram() != null ? advert.getProgram() : advert.getProject();
            representation.setUser(dozerBeanMapper.map(resource.getUser(), UserRepresentation.class));
            representation.setResourceScope(resource.getResourceScope());
            representation.setResourceId(resource.getId());
            representation.setProgramType(resource.getProgram().getProgramType().getPrismProgramType());

            List<String> locations = Lists.newArrayListWithCapacity(resource.getProgram().getLocations().size());
            for (ProgramLocation programLocation : resource.getProgram().getLocations()) {
                locations.add(programLocation.getLocation());
            }
            representation.setLocations(locations);

            Set<PrismStudyOption> studyOptions = Sets.newHashSet();
            for (ProgramStudyOption studyOption : resource.getProgram().getStudyOptions()) {
                studyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
            }
            representation.setStudyOptions(studyOptions);
            representation.setInstitution(dozerBeanMapper.map(resource.getInstitution(), InstitutionRepresentation.class));

            representations.add(representation);
        }
        return representations;
    }

}
