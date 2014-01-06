package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Advert getAdvert(int advertId) {
        return advertDAO.getAdvertById(advertId);
    }

    public Project getProject(int projectId) {
        return projectDAO.getProjectById(projectId);
    }

    public void saveProject(Project project) {
        projectDAO.save(project);
    }
    
    public void removeProject(int projectId) {
        Project project = getProject(projectId);
        if(project==null){
        	return;
        }
        project.setDisabled(true);
        project.getAdvert().setActive(false);
        projectDAO.save(project);
    }

    public void addProgramAdvert(String programCode, Advert advert) {
        Program program = getProgramByCode(programCode);
        advertDAO.delete(program.getAdvert());
        program.setAdvert(advert);
        programDAO.save(program);
    }

    public Map<String, String> getDefaultClosingDates() {
        Map<String, String> result = new HashMap<String, String>();
        List<Program> programs = getAllPrograms();
        for (Program program : programs) {
            result.put(program.getCode(), getDefaultClosingDate(program));
        }
        return result;
    }

    public String getDefaultClosingDate(Program program) {
        Date closingDate = programDAO.getNextClosingDateForProgram(program, new Date());
        String formattedDate = "null";
        if (closingDate != null) {
            formattedDate = new SimpleDateFormat("dd MMM yyyy").format(closingDate);
        }
        return formattedDate;
    }
    
	public List<Program> getProgramsOfWhichAdministrator(RegisteredUser user) {
		return programDAO.getProgramsOfWhichAdministrator(user);
	}
    
	public List<Program> getProgramsOfWhichAuthor(RegisteredUser user) {
		return programDAO.getProgramsOfWhichAuthor(user);
	}
	
	public List<Program> getProgramsOfWhichProjectAuthor(RegisteredUser user) {
		return programDAO.getProgramsOfWhichProjectAuthor(user);
	}
	
	public List<Program> getProgramsOfWhichProjectEditor(RegisteredUser user) {
		return programDAO.getProgramsOfWhichProjectEditor(user);
	}

}