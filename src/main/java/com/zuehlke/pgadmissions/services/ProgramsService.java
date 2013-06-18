package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
    
    public List<Program> getProgramsForWhichCanManageProjects(RegisteredUser user) {
        if(user.isInRole(Authority.SUPERADMINISTRATOR)){
            return programDAO.getAllPrograms();
        }
        Set<Program> programs = Sets.newHashSet();
        programs.addAll(user.getProgramsOfWhichAdministrator());
        programs.addAll(user.getProgramsOfWhichApprover());
        programs.addAll(user.getProgramsOfWhichReviewer());
        programs.addAll(user.getProgramsOfWhichInterviewer());
        programs.addAll(user.getProgramsOfWhichSupervisor());
        programs.addAll(programDAO.getProgramsOfWhichPreviousReviewer(user));
        programs.addAll(programDAO.getProgramsOfWhichPreviousInterviewer(user));
        programs.addAll(programDAO.getProgramsOfWhichPreviousSupervisor(user));
        
        return Lists.newArrayList(programs);
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

    public void saveProject(Project project) {
        projectDAO.save(project);
    }

    public List<Project> listProjects(RegisteredUser user, Program program) {
        if (user.isInRole(user, Authority.SUPERADMINISTRATOR) || user.isAdminInProgramme(program)) {
            return projectDAO.getProjectsForProgram(program);
        } else {
            return projectDAO.getProjectsForProgramOfWhichAuthor(program, user);
        }
    }

    public void merge(Program program) {
        programDAO.merge(program);
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
        if (closingDate !=null) {
            formattedDate = new SimpleDateFormat("dd MMM yyyy").format(closingDate);
        }
        return  formattedDate;
    }

	


}
