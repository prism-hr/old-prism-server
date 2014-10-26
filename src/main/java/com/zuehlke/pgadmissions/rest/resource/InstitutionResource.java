package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetency;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/institutions")
public class InstitutionResource {

    private static final Logger log = LoggerFactory.getLogger(InstitutionResource.class);

    @Autowired
    private AdvertService advertService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    @ResponseBody
    public List<ImportedEntityRepresentation> getInstitutions() {
        List<Institution> institutions = institutionService.list();
        List<ImportedEntityRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (Institution institution : institutions) {
            ImportedEntityRepresentation institutionRepresentation = new ImportedEntityRepresentation();
            institutionRepresentation.setId(institution.getId());
            institutionRepresentation.setName(institution.getTitle() + " - " + institution.getAddress().getLocationString());
            institutionRepresentations.add(institutionRepresentation);
        }
        return institutionRepresentations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "googleId")
    @ResponseBody
    public InstitutionExtendedRepresentation getInstitution(String googleId) {
        Institution institution = institutionService.getByGoogleId(googleId);
        return institution == null ? null : dozerBeanMapper.map(institution, InstitutionExtendedRepresentation.class);
    }

    @RequestMapping(value = "/{institutionId}/categoryTags", method = RequestMethod.GET, params = "locale")
    public Map<String, List<String>> getCategoryTags(@PathVariable Integer institutionId, @RequestParam PrismLocale locale) throws Exception {
        Map<String, List<String>> categoryTags = Maps.newLinkedHashMap();
        Institution institution = institutionService.getById(institutionId);

        String category = "competencies";
        categoryTags.put(category, advertService.getLocalizedTags(institution, locale, AdvertCompetency.class));
        category = "themes";
        categoryTags.put(category, advertService.getLocalizedTags(institution, locale, AdvertTheme.class));

        return categoryTags;
    }

    @RequestMapping(value = "/{institutionId}/programs", method = RequestMethod.GET)
    public List<ProgramRepresentation> getCategoryTags(@PathVariable Integer institutionId) throws Exception {
        Institution institution = institutionService.getById(institutionId);

        List<ProgramRepresentation> programRepresentations = Lists.newLinkedList();
        for (Program program : institution.getPrograms()) {
            if (program.getState().getId() == PrismState.PROGRAM_APPROVED) {
                ProgramRepresentation representation = dozerBeanMapper.map(program, ProgramRepresentation.class);
                representation.setInstitution(null); // saving bandwidth
                programRepresentations.add(representation);
            }
        }
        return programRepresentations;
    }

}
