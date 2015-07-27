package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
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
    private StateService stateService;

    @Inject
    private UserService userService;

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

    public List<Integer> getProjects(Integer program) {
        return programDAO.getProjects(program);
    }

    public List<Integer> getApplications(Integer program) {
        return programDAO.getApplications(program);
    }

    public List<ResourceChildCreationDTO> getProgramsForWhichUserCanCreateProject(Integer institutionId) {
        List<PrismState> states = stateService.getActiveResourceStates(PROGRAM);
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return programDAO.getProgramsForWhichUserCanCreateProject(institutionId, states, userLoggedIn);
    }

}
