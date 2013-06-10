package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ProjectDTO;

@Service
@Transactional
public class ProgramsService {

    private final ProgramDAO programDAO;
    private final AdvertDAO advertDAO;
    private final ProjectDAO projectDAO;

    ProgramsService() {
        this(null, null, null);
    }

    @Autowired
    public ProgramsService(ProgramDAO programDAO, AdvertDAO advertDAO, ProjectDAO projectDAO) {
        this.programDAO = programDAO;
        this.advertDAO = advertDAO;
        this.projectDAO = projectDAO;
    }

    public List<Program> getAllPrograms() {
        return programDAO.getAllPrograms();
    }

    public Program getProgramById(Integer programId) {
        return programDAO.getProgramById(programId);
    }

    public void save(Program program) {
        programDAO.save(program);
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }

    public void applyScoringDefinition(String programCode, ScoringStage scoringStage, String scoringContent) {
        Program program = programDAO.getProgramByCode(programCode);
        ScoringDefinition scoringDefinition = new ScoringDefinition();
        scoringDefinition.setContent(scoringContent);
        scoringDefinition.setStage(scoringStage);
        program.getScoringDefinitions().put(scoringStage, scoringDefinition);
    }

    public void removeScoringDefinition(String programCode, ScoringStage scoringStage) {
        Program program = programDAO.getProgramByCode(programCode);
        program.getScoringDefinitions().put(scoringStage, null);
    }

    public void merge(Advert programAdvert) {
        advertDAO.merge(programAdvert);
    }

    public void removeProject(int projectId) {
        Project project = getProject(projectId);
        projectDAO.delete(project);
    }

    public Advert getAdvert(int advertId) {
        return advertDAO.getAdvertById(advertId);
    }

    public Project getProject(int projectId) {
        return projectDAO.getProjectById(projectId);
    }

    public void addProject(ProjectDTO projectAdvertDTO, RegisteredUser author) {

        Advert advert = new Advert();
        advert.setTitle(projectAdvertDTO.getTitle());
        advert.setDescription(projectAdvertDTO.getDescription());
        advert.setStudyDuration(projectAdvertDTO.getStudyDuration());
        advert.setFunding(projectAdvertDTO.getFunding());
        advert.setActive(projectAdvertDTO.getActive());

        Project project = new Project();
        project.setAdvert(advert);
        project.setAuthor(author);
        project.setPrimarySupervisor(author);
        project.setProgram(projectAdvertDTO.getProgram());
        if (projectAdvertDTO.getClosingDateSpecified()) {
            project.setClosingDate(projectAdvertDTO.getClosingDate());
        }

        projectDAO.save(project);
    }

    public List<Project> listProjects(RegisteredUser author) {
        return projectDAO.getProjectsByAuthor(author);
    }

	public void merge(Program program) {
		programDAO.merge(program);
		
	}

}
