package com.zuehlke.pgadmissions.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetency;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SimpleResourceRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.helpers.DozerMapperHelper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/institutions")
@PreAuthorize("permitAll")
public class InstitutionController {

    private static Logger LOGGER = LoggerFactory.getLogger(InstitutionController.class);

    @Inject
    private AdvertService advertService;

    @Inject
    private ProgramService programService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private DozerMapperHelper dozerMapperHelper;

    @Inject
    private Mapper dozerBeanMapper;

    @Inject
    private ObjectMapper objectMapper;

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

    @RequestMapping(method = RequestMethod.GET, params = "query")
    public List<InstitutionRepresentation> getInstitutions(@RequestParam String query, @RequestParam(required = false) String[] googleIds) {
        List<Institution> institutions = institutionService.getInstitutions(query, googleIds);
        return Lists.transform(institutions, dozerMapperHelper.createFunction(InstitutionRepresentation.class));
    }

    @RequestMapping(method = RequestMethod.GET, params = "accepting")
    public List<AcceptingResourceRepresentation> getAcceptingInstitutions(@RequestParam String accepting) {
        List<ResourceForWhichUserCanCreateChildDTO> institutions;
        if (accepting.equals("programs")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProgram();
        } else if (accepting.equals("projects")) {
            institutions = institutionService.getInstitutionsForWhichUserCanCreateProject();
        } else {
            throw new Error();
        }
        return Lists.transform(institutions, new AcceptingResourceToRepresentationFunction());
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET, params = "accepting=projects")
    public List<AcceptingResourceRepresentation> getAcceptingPrograms(@PathVariable Integer institutionId) {
        List<ResourceForWhichUserCanCreateChildDTO> programs = programService.getProgramsForWhichUserCanCreateProject(institutionId);
        return Lists.transform(programs, new AcceptingResourceToRepresentationFunction());
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

        return institution.getPrograms().stream()
                .filter(program -> program.getState().getId() == PrismState.PROGRAM_APPROVED)
                .map(program -> {
                    ProgramRepresentation representation = dozerBeanMapper.map(program, ProgramRepresentation.class);
                    representation.setInstitution(null);
                    return representation;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{institutionId}/similarPrograms", method = RequestMethod.GET)
    public List<ProgramRepresentation> getSimilarPrograms(@PathVariable Integer institutionId, @RequestParam String searchTerm) {
        return programService.getSimilarPrograms(institutionId, searchTerm);
    }

    @RequestMapping(value = "/{institutionId}/importedData/{type}", method = RequestMethod.POST)
    public void importData(@PathVariable PrismImportedEntity type, HttpServletRequest request) throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type.getEntityClass());
        List<Object> entities = objectMapper.readValue(request.getInputStream(), collectionType);
        LOGGER.info("Loaded entities: " + entities.size() + ", type: " + type);
    }

    private static class AcceptingResourceRepresentation extends SimpleResourceRepresentation {

        private Boolean partnerMode;

        private PrismOpportunityType opportunityType;

        public AcceptingResourceRepresentation(Integer id, String title, Boolean partnerMode, PrismOpportunityType opportunityType) {
            super(id, title);
            this.partnerMode = partnerMode;
            this.opportunityType = opportunityType;
        }

        @SuppressWarnings("unused")
        public Boolean getPartnerMode() {
            return partnerMode;
        }

        @SuppressWarnings("unused")
        public PrismOpportunityType getOpportunityType() {
            return opportunityType;
        }
    }

    private static class AcceptingResourceToRepresentationFunction implements Function<ResourceForWhichUserCanCreateChildDTO, AcceptingResourceRepresentation> {
        @Override
        public AcceptingResourceRepresentation apply(ResourceForWhichUserCanCreateChildDTO input) {
            ResourceParent resource = input.getResource();
            PrismOpportunityType opportunityType = resource.getOpportunityType() != null ? resource.getOpportunityType().getPrismOpportunityType() : null;
            return new AcceptingResourceRepresentation(resource.getId(), resource.getTitle(), input.getPartnerMode(), opportunityType);
        }
    }

}
