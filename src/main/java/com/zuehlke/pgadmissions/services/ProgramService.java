package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Service
@Transactional
public class ProgramService {

    @Inject
    private ProgramDAO programDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private StateService stateService;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
    }

    public Program getProgramByImportedCode(String importedCode) {
        return getProgramByImportedCode(null, importedCode);
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public List<ResourceRepresentationSimple> getApprovedPrograms(Integer institutionId) {
        return programDAO.getApprovedPrograms(institutionId);
    }

    public List<ResourceRepresentationSimple> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return programDAO.getSimilarPrograms(institutionId, searchTerm);
    }

    public List<String> getSuggestedDivisions(Integer programId, String location) {
        Program program = getById(programId);
        return programDAO.getSuggestedDivisions(program, location);
    }

    public List<String> getSuggestedStudyAreas(Integer programId, String location, String division) {
        Program program = getById(programId);
        return programDAO.getSuggestedStudyAreas(program, location, division);
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return programDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getSitemapEntries(activeProgramStates);
    }

    public List<ResourceSearchEngineDTO> getActiveProgramsByInstitution(Integer institutionId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getActiveProgramsByInstitution(institutionId, activeProgramStates);
    }

    public List<Integer> getProjects(Integer program) {
        return programDAO.getProjects(program);
    }

    public List<Integer> getApplications(Integer program) {
        return programDAO.getApplications(program);
    }

    public List<ResourceChildCreationDTO> getProgramsForWhichUserCanCreateProject(Integer institutionId) {
        List<PrismState> states = stateService.getActiveProgramStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return programDAO.getProgramsForWhichUserCanCreateProject(institutionId, states, userLoggedIn);
    }

    public void update(Integer programId, ResourceOpportunityDTO programDTO, Comment comment) throws Exception {
        resourceService.update(PROGRAM, programId, programDTO, comment);
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer programId, List<PrismState> activeProgramStates) {
        return programDAO.getSearchEngineAdvert(programId, activeProgramStates);
    }

}
