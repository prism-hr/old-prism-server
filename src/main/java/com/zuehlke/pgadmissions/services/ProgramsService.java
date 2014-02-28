package com.zuehlke.pgadmissions.services;

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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

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

    @Autowired
    private RoleService roleService;

    public List<Program> getAllEnabledPrograms() {
        return programDAO.getAllEnabledPrograms();
    }

    public Program getProgramById(Integer programId) {
        return programDAO.getProgramById(programId);
    }

    public void save(Program program) {
        programDAO.save(program);
    }

    public Program merge(Program program) {
        return programDAO.merge(program);
    }

    public Program getProgramByCode(String code) {
        Program program = programDAO.getProgramByCode(code);
        // TODO remove it
        program.setProgramType(new ProgramType(ProgramTypeId.VISITING_RESEARCH, 5));
        return program;
    }

    public List<Program> getProgramsForWhichCanManageProjects(RegisteredUser user) {
        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            return programDAO.getAllEnabledPrograms();
        }

        Set<Program> programs = new TreeSet<Program>(new Comparator<Program>() {
            @Override
            public int compare(Program p1, Program p2) {
                int compareInstitutions = p1.getInstitution().getName().compareTo(p2.getInstitution().getName());
                if (compareInstitutions != 0) {
                    return compareInstitutions;
                }
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
        program.getAdvert().setLastEditedTimestamp(new Date());
        programDAO.updateClosingDate(closingDate);
    }

    public void deleteClosingDateById(Integer programClosingDateId) {
        ProgramClosingDate programClosingDate = programDAO.getClosingDateById(programClosingDateId);
        programDAO.deleteClosingDate(programClosingDate);
    }

    public void addClosingDateToProgram(Program program, ProgramClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
        program.getAdvert().setLastEditedTimestamp(new Date());
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

    protected Program createOrGetProgram(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);

        Program program = opportunityRequest.getSourceProgram();

        if (program != null) {
            if (program.getProgramFeed() != null) {
                // built-in program, cannot modify
                return program;
            }
            program = merge(program);
        } else {
            program = new Program();
            program.setEnabled(true);
        }

        if (program.getInstitution() == null || !Objects.equal(program.getInstitution().getCode(), opportunityRequest.getInstitutionCode())) {
            // assigning new institution to the program (also changing program code)
            QualificationInstitution institution = qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest.getInstitutionCode(),
                    opportunityRequest.getInstitutionCountry(), opportunityRequest.getOtherInstitution());
            program.setInstitution(institution);
            program.setCode(thisBean.generateNextProgramCode(institution));
        }
        program.setTitle(opportunityRequest.getProgramTitle());
        program.setAtasRequired(opportunityRequest.getAtasRequired());
        save(program);
        return program;
    }

    protected void grantAdminPermissionsForProgram(RegisteredUser user, Program program) {
        if (!HibernateUtils.containsEntity(user.getInstitutions(), program.getInstitution())) {
            user.getInstitutions().add(program.getInstitution());
        }
        Role adminRole = roleService.getRoleByAuthority(Authority.ADMINISTRATOR);
        if (!HibernateUtils.containsEntity(user.getRoles(), adminRole)) {
            user.getRoles().add(adminRole);
        }
        if (!HibernateUtils.containsEntity(user.getProgramsOfWhichAdministrator(), program)) {
            user.getProgramsOfWhichAdministrator().add(program);
        }
    }

    public Program saveProgramOpportunity(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);

        Program program = thisBean.createOrGetProgram(opportunityRequest);

        Advert advert = program.getAdvert();
        if (advert == null) {
            advert = new Advert();
            program.setAdvert(advert);
        }
        advert.setDescription(opportunityRequest.getProgramDescription());
        advert.setStudyDuration(opportunityRequest.getStudyDuration());
        advert.setFunding(opportunityRequest.getFunding());
        advert.setActive(opportunityRequest.getAcceptingApplications());

        if (program.getProgramFeed() == null) { // custom program
            programInstanceService.createRemoveProgramInstances(program, opportunityRequest.getStudyOptions(), opportunityRequest.getAdvertisingDeadlineYear());
        }

        thisBean.grantAdminPermissionsForProgram(opportunityRequest.getAuthor(), program);

        return program;
    }

    public boolean canChangeInstitution(RegisteredUser user, OpportunityRequest opportunityRequest) {
        if (user.isInRole(Authority.SUPERVISOR)) {
            return true;
        }
        Program existingProgram = opportunityRequest.getSourceProgram();
        if (existingProgram != null && existingProgram.getInstitution().getCode().equals(opportunityRequest.getInstitutionCode())) {
            // no change
            return true;
        }

        for (QualificationInstitution institution : user.getInstitutions()) {
            if (institution.getCode().equals(opportunityRequest.getInstitutionCode())) {
                // user has rights to this institution
                return true;
            }
        }
        return false;

    }

}
