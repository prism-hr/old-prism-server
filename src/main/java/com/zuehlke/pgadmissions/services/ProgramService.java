package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.CommentCustomQuestion;
import com.zuehlke.pgadmissions.domain.CustomQuestionVersion;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private InstitutionService qualificationInstitutionService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ActionService actionService;
    
    // TODO: Rewrite below
    
    public List<Program> getAllEnabledPrograms() {
        return programDAO.getAllEnabledPrograms();
    }

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }
    
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void save(Advert advert) {
        programDAO.save(advert);
    }

    public Advert merge(Advert advert) {
        programDAO.merge(advert);
        return advert;
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }

    public List<Program> getProgramsForWhichCanManageProjects(User user) {
        return programDAO.getProgramsForWhichUserCanManageProjects(user);
    }
    
    public CommentCustomQuestion getCustomQuestionsForProgram(Integer programId, PrismAction actionId) {
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("program.id", programId);
        properties.put("action.id", actionId);
        properties.put("enabled", true);
        return entityService.getByProperties(CommentCustomQuestion.class, properties);
    }

    public void createCustomQuestionsForProgram(Integer programId, PrismAction actionId, String definition) {
        Program program = getById(programId).getProgram();
        Action action = actionService.getById(actionId);
        CommentCustomQuestion persistentCustomQuestionDefinition = entityService.getOrCreate(new CommentCustomQuestion().withProgram(program).withAction(action));
        CustomQuestionVersion version = new CustomQuestionVersion().withCustomQuestion(persistentCustomQuestionDefinition).withContent(definition);
        entityService.save(version);
        persistentCustomQuestionDefinition.setVersion(version);
        persistentCustomQuestionDefinition.setEnabled(true);
    }

    public void disableCustomQuestionsForProgram(Integer programId, PrismAction actionId) {
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("program.id", programId);
        properties.put("action.id", actionId);
        CommentCustomQuestion customQuestions = entityService.getByProperties(CommentCustomQuestion.class, properties);
        customQuestions.setEnabled(false);
    }

    public void removeProject(Integer projectId) {
        Project project = (Project) getById(projectId);
        if (project != null) {
// TODO - state transition project.setState(ProjectState.PROJECT_DISABLED);
        }
    }

    public List<Project> listProjects(User user, Program program) {
        // TODO implement
        return null;
    }

    public String getDefaultClosingDate(Program program) {
        LocalDate closingDate = programDAO.getNextClosingDate(program);
        return closingDate != null ? closingDate.toString("dd MMM yyyy") : "null";
    }

    public void updateClosingDate(AdvertClosingDate closingDate) {
        programDAO.updateClosingDate(closingDate);
    }

    public void deleteClosingDateById(Integer programClosingDateId) {
        AdvertClosingDate programClosingDate = programDAO.getClosingDateById(programClosingDateId);
        programDAO.deleteClosingDate(programClosingDate);
    }

    public void addClosingDateToProgram(Program program, AdvertClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
        programDAO.save(program);
    }

    protected String generateNextProgramCode(Institution institution) {
        Program lastCustomProgram = programDAO.getLastCustomProgram(institution);
        Integer codeNumber;
        if (lastCustomProgram != null) {
            codeNumber = Integer.valueOf(lastCustomProgram.getCode());
            codeNumber++;
        } else {
            codeNumber = 0;
        }
        return String.format("%05d", codeNumber);
    }

// TODO: rewrite for new workflow paradigm    
    
//    protected Program getOrCreateProgram(Program program) {
//        ProgramService thisBean = applicationContext.getBean(ProgramService.class);
//
//        if (program != null) {
//            program = (Program) merge(program);
//            program.setUser(thisBean.getContactUserForProgram(program, program.getUser()));
//        } else {
//            program = new Program();
////            program.setState(ProgramState.PROGRAM_APPROVED);
//            program.setUser(program.getUser());
//        }
//
//        if (program.isImported()) {
//            if (program.getInstitution() == null || !HibernateUtils.sameEntities(program.getInstitution(), opportunityRequest.getInstitutionCode())) {
//                Institution institution = qualificationInstitutionService.getOrCreate(opportunityRequest.getInstitutionCode(),
//                        opportunityRequest.getInstitutionCountry(), opportunityRequest.getOtherInstitution());
//                program.setInstitution(institution);
//                program.setCode(thisBean.generateNextProgramCode(institution));
//            }
//            program.setTitle(opportunityRequest.getProgramTitle());
//            program.setRequireProjectDefinition(opportunityRequest.getAtasRequired());
//            program.setProgramType(opportunityRequest.getProgramType());
//        }
//
//        program.setDescription(opportunityRequest.getProgramDescription());
//        program.setStudyDuration(opportunityRequest.getStudyDuration());
//        program.setFunding(opportunityRequest.getFunding());
//        // FIXME set the right state, not that it can be overridden by programInstanceService#createRemoveProgramInstances() (when there are no active instanes)
//        // program.setActive(opportunityRequest.getAcceptingApplications());
//
//        save(program);
//        return program;
//    }

    protected void grantAdminPermissionsForProgram(User user, Program program) {
        // TODO try to reuse any method from RoleService
        throw new UnsupportedOperationException();
        // if (!HibernateUtils.containsEntity(user.getInstitutions(), program.getInstitution())) {
        // user.getInstitutions().add(program.getInstitution());
        // }
        // Role adminRole = roleService.getById(Authority.ADMINISTRATOR);
        // Role approverRole = roleService.getById(Authority.APPROVER);
        // if (!HibernateUtils.containsEntity(user.getRoles(), adminRole)) {
        // user.getRoles().add(adminRole);
        // }
        // if (!HibernateUtils.containsEntity(user.getRoles(), approverRole)) {
        // user.getRoles().add(approverRole);
        // }
        // if (!HibernateUtils.containsEntity(user.getProgramsOfWhichAdministrator(), program)) {
        // user.getProgramsOfWhichAdministrator().add(program);
        // }
        // if (!HibernateUtils.containsEntity(user.getProgramsOfWhichApprover(), program)) {
        // user.getProgramsOfWhichApprover().add(program);
        // }
    }

    public Advert getValidProgramProjectAdvert(Integer advertId) {
        Advert advert = null;
        if (advertId != null) {
            advert = programDAO.getAcceptingApplicationsById(advertId);
        }

        if (advert == null) {
            throw new CannotApplyException();
        }

        return advert;
    }

    public List<ProgramType> getProgramTypes() {
        return programDAO.getProgamTypes();
    }

    public ProgramType getProgramTypeById(ProgramType programTypeId) {
        return programDAO.getProgramTypeById(programTypeId);
    }

    public void deleteInactiveAdverts() {
        programDAO.deleteInactiveAdverts();
    }

    public Date getDefaultStartDate(Program program, StudyOption studyOption) {
        return programDAO.getDefaultStartDate(program, studyOption);
    }

    public List<ProgramInstance> getActiveProgramInstances(Program program) {
        return programDAO.getActiveProgramInstances(program);
    }

    public List<ProgramInstance> getActiveProgramInstancesForStudyOption(Program program, StudyOption studyOption) {
        return programDAO.getActiveProgramInstancesForStudyOption(program, studyOption);
    }

    public List<StudyOption> getAvailableStudyOptions(Program program) {
        return programDAO.getAvailableStudyOptions(program);
    }

    public LocalDate getNextClosingDate(Program program) {
        return programDAO.getNextClosingDate(program);
    }

    protected User getContactUserForProgram(Program program, User candidateUser) {
        List<User> administrators = roleService.getProgramAdministrators(program);
        if (!administrators.isEmpty()) {
            if (administrators.contains(candidateUser)) {
                return candidateUser;
            } else {
                return administrators.get(0);
            }
        }
        return program.getUser();
    }

    public Project addProject(ProjectDTO projectDTO) {
        // TODO Auto-generated method stub
        return null;
    }

    public void updateProject(Integer id, ProjectDTO projectDTO) {
        // TODO Auto-generated method stub

    }

    public List<Program> getAllPrograms() {
        return programDAO.getAllPrograms();
    }

}
