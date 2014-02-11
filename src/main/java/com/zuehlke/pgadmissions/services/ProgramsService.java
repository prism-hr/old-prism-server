package com.zuehlke.pgadmissions.services;

import static java.util.Objects.requireNonNull;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;
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
    private ProgramInstanceService programInstanceService;

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
        project.setEnabled(false);
        project.setActive(false);
        projectDAO.save(project);
    }

    public List<Project> listProjects(RegisteredUser user, Program program) {
        if (user.isInRole(user, Authority.SUPERADMINISTRATOR) || user.isAdminInProgramme(program)) {
            return projectDAO.getProjectsForProgram(program);
        } else {
            return projectDAO.getProjectsForProgramOfWhichAuthor(program, user);
        }
    }

    public String getDefaultClosingDate(Program program) {
        Date closingDate = programDAO.getNextClosingDate(program);
        String formattedDate = "null";
        if (closingDate != null) {
            formattedDate = new SimpleDateFormat("dd MMM yyyy").format(closingDate);
        }
        return formattedDate;
    }


    public void updateClosingDate(ProgramClosingDate closingDate) {
        Program program = closingDate.getProgram();
        program.setLastEditedTimestamp(new Date());
        programDAO.updateClosingDate(closingDate);
    }

    public void deleteClosingDateById(Integer programClosingDateId) {
        ProgramClosingDate programClosingDate = programDAO.getClosingDateById(programClosingDateId);
        programDAO.deleteClosingDate(programClosingDate);
    }

    public void addClosingDateToProgram(Program program, ProgramClosingDate programClosingDate) {        
        program.getClosingDates().add(programClosingDate);
        program.setLastEditedTimestamp(new Date());
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

    public Program createNewCustomProgram(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);

        QualificationInstitution institution = qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest.getInstitutionCode(),
                opportunityRequest.getInstitutionCountry(), opportunityRequest.getOtherInstitution());

        Program program = new Program();
        program.setDescription(opportunityRequest.getProgramDescription());
        program.setStudyDuration(opportunityRequest.getStudyDuration());
        program.setActive(true);
        program.setInstitution(institution);
        program.setEnabled(true);
        program.setTitle(opportunityRequest.getProgramTitle());
        program.setAtasRequired(opportunityRequest.getAtasRequired());
        program.setCode(thisBean.generateNextProgramCode(institution));

        programDAO.save(program);
        return program;
    }

    
    public Program saveProgramOpportunity(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);
        
        Program program;
        if (opportunityRequest.getType() == OpportunityRequestType.CHANGE) {
            program = requireNonNull(getProgramById(opportunityRequest.getSourceProgram().getId()));
        } else {
            QualificationInstitution institution = qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest.getInstitutionCode(),
                    opportunityRequest.getInstitutionCountry(), opportunityRequest.getOtherInstitution());
            program = new Program();
            program.setTitle(opportunityRequest.getProgramTitle());
            program.setInstitution(institution);
            program.setEnabled(true);
            program.setAtasRequired(opportunityRequest.getAtasRequired());
            program.setCode(thisBean.generateNextProgramCode(institution));
            save(program);
        }

        program.setDescription(opportunityRequest.getProgramDescription());
        program.setStudyDuration(opportunityRequest.getStudyDuration());
        program.setFunding(opportunityRequest.getFunding());
        program.setActive(opportunityRequest.getAcceptingApplications());
        
        if (program.getProgramFeed() == null) { // custom program
            programInstanceService.createRemoveProgramInstances(program, opportunityRequest.getStudyOptions(),
                    opportunityRequest.getAdvertisingDeadlineYear());
        }
        return program;
    }

}
