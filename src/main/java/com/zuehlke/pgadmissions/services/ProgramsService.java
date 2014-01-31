package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

@Service
@Transactional
public class ProgramsService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private AdvertDAO advertDAO;

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private QualificationInstitutionService qualificationInstitutionService;

    @Autowired
    private ApplicationContext applicationContext;

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

    public List<Program> getProgramsForWhichCanManageProjects(RegisteredUser user) {
        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            return programDAO.getAllPrograms();
        }

        Set<Program> programs = new TreeSet<Program>(new Comparator<Program>() {
            @Override
            public int compare(Program p1, Program p2) {
                return p1.getTitle().compareTo(p2.getTitle());
            }
        });

        programs.addAll(user.getProgramsOfWhichAdministrator());
        programs.addAll(user.getProgramsOfWhichApprover());
        programs.addAll(programDAO.getProgramsOfWhichPreviousReviewer(user));
        programs.addAll(programDAO.getProgramsOfWhichPreviousInterviewer(user));
        programs.addAll(programDAO.getProgramsOfWhichPreviousSupervisor(user));

        return Lists.newArrayList(programs);
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
        if (project == null) {
            return;
        }
        project.setDisabled(true);
        project.getAdvert().setActive(false);
        projectDAO.save(project);
    }

    public List<Project> listProjects(RegisteredUser user, Program program) {
        if (user.isInRole(user, Authority.SUPERADMINISTRATOR) || user.isAdminInProgramme(program)) {
            return projectDAO.getProjectsForProgram(program);
        } else {
            return projectDAO.getProjectsForProgramOfWhichAuthor(program, user);
        }
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

    public Program createNewCustomProgram(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);
        
        Advert advert = new Advert();
        advert.setActive(true);
        advert.setDescription(opportunityRequest.getProgramDescription());
        advert.setStudyDuration(opportunityRequest.getStudyDuration());

        QualificationInstitution institution = qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest);

        Program program = new Program();
        program.setAdvert(advert);
        program.setInstitution(institution);
        program.setEnabled(true);
        program.setTitle(opportunityRequest.getProgramTitle());
        program.setAtasRequired(opportunityRequest.getAtasRequired());
        program.setCode(thisBean.generateNextProgramCode(institution));
        
        programDAO.save(program);
        return program;
    }
    
    public void addClosingDateToProgram(Program program, ProgramClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
        programDAO.save(program);
    }

    protected String generateNextProgramCode(QualificationInstitution institution) {
        Program lastCustomProgram = programDAO.getLastCustomProgram(institution);
        Integer codeNumber;
        if (lastCustomProgram != null) {
            codeNumber = Integer.valueOf(lastCustomProgram.getCode().split("_")[1]);
            codeNumber++;
        } else {
            codeNumber = 0;
        }
        return String.format("%s_%05d", institution.getCode(), codeNumber);
    }

}