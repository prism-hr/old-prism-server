package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetency;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SimpleResourceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import org.dozer.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/institutions")
@PreAuthorize("permitAll")
public class InstitutionController {

    @Inject
    private AdvertService advertService;

    @Inject
    private ProgramService programService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private UserService userService;

    @Inject
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<SimpleResourceRepresentation> getInstitutions() {
        List<Institution> institutions;
        institutions = institutionService.list();
        List<SimpleResourceRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            InstitutionAddress address = institution.getAdvert().getAddress();
            String name = Joiner.on(" - ").skipNulls().join(institution.getTitle(), address.getAddressTown(), address.getAddressCode());
            SimpleResourceRepresentation institutionRepresentation = new SimpleResourceRepresentation(institution.getId(), name);
            institutionRepresentations.add(institutionRepresentation);
        }
        return institutionRepresentations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "accepting")
    public List<AcceptingResourceRepresentation> getAcceptingInstitutions(@RequestParam String accepting) {
        User user = userService.getCurrentUser();
        List<ResourceForWhichUserCanCreateChildDTO> institutions;
        if (accepting.equals("programs")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProgram(user);
        } else if (accepting.equals("projects")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProject(user);
        } else {
            throw new Error();
        }

        return Lists.transform(institutions, new AcceptingResourceToRepresentationFunction());
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public InstitutionExtendedRepresentation getInstitution(String googleId) {
        Institution institution = institutionService.getActivatedInstitutionByGoogleId(googleId);
        return institution == null ? null : dozerBeanMapper.map(institution, InstitutionExtendedRepresentation.class);
    }


    @RequestMapping(value = "/{institutionId}/categoryTags", method = RequestMethod.GET)
    public Map<String, List<String>> getCategoryTags(@PathVariable Integer institutionId) throws Exception {
        Map<String, List<String>> categoryTags = Maps.newLinkedHashMap();
        Institution institution = institutionService.getById(institutionId);

        String category = "competencies";
        categoryTags.put(category, advertService.getAdvertTags(institution, AdvertCompetency.class));
        category = "themes";
        categoryTags.put(category, advertService.getAdvertTags(institution, AdvertTheme.class));

        return categoryTags;
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET)
    public List<ProgramRepresentation> getPrograms(@PathVariable Integer institutionId) throws Exception {
        Institution institution = institutionService.getById(institutionId);

        List<ProgramRepresentation> programRepresentations = Lists.newLinkedList();
        for (Program program : institution.getPrograms()) {
            if (program.getState().getId() == PrismState.PROGRAM_APPROVED) {
                ProgramRepresentation representation = dozerBeanMapper.map(program, ProgramRepresentation.class);
                representation.setInstitution(null);
                programRepresentations.add(representation);
            }
        }
        return programRepresentations;
    }

    @RequestMapping(value = "/{institutionId}/similarPrograms", method = RequestMethod.GET)
    public List<ProgramRepresentation> getSimilarPrograms(@PathVariable Integer institutionId, @RequestParam String searchTerm) {
        return programService.getSimilarPrograms(institutionId, searchTerm);
    }

    private static class AcceptingResourceRepresentation extends SimpleResourceRepresentation {

        private Boolean partnerMode;

        public AcceptingResourceRepresentation(Integer id, String title, Boolean partnerMode) {
            super(id, title);
            this.partnerMode = partnerMode;
        }

        public Boolean getPartnerMode() {
            return partnerMode;
        }

    }

    private static class AcceptingResourceToRepresentationFunction implements Function<ResourceForWhichUserCanCreateChildDTO, AcceptingResourceRepresentation> {
        @Override
        public AcceptingResourceRepresentation apply(ResourceForWhichUserCanCreateChildDTO input) {
            return new AcceptingResourceRepresentation(input.getResource().getId(), input.getResource().getTitle(), input.getPartnerMode());
        }
    }

}
