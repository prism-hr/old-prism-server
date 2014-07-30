package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentCustomQuestion;
import com.zuehlke.pgadmissions.domain.CommentCustomQuestionVersion;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private InstitutionService qualificationInstitutionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private SystemService systemService;
    
    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }
    
    public List<ResourceConsoleListRowDTO> getProgramListBlock(Integer page, Integer perPage) {
        return resourceService.getConsoleListBlock(Program.class, page, perPage);
    }
    
    public List<Program> getProgramsOpenForApplication() {
        return programDAO.getProgramsOpenForApplication();
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }
    
    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }
     
    public List<Program> getProgramsForWhichCanManageProjects(User user) {
        // TODO implement SQL query for basic list;
        return null;
    }
    
    public Program getOrImportProgram(Programme programme, Institution institution) {
        User proxyCreator = institution.getUser();
        
        PrismProgramType programType = PrismProgramType.findValueFromString(programme.getName());
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withImportedCode(programme.getCode())
                .withTitle(programme.getName()).withProgramType(programType).withUser(proxyCreator);
        
        Action importAction = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);
        Role proxyCreatorRole = roleService.getCreatorRole(transientProgram);

        Comment comment = new Comment().withUser(proxyCreator).withCreatedTimestamp(new DateTime()).withAction(importAction).withDeclinedResponse(false)
                .withAssignedUser(proxyCreator, proxyCreatorRole);

        Program persistentProgram = (Program) actionService.executeSystemAction(transientProgram, importAction, comment).getResource();
        return persistentProgram.withTitle(programme.getName()).withRequireProjectDefinition(programme.isAtasRegistered());
    }
    
    public void saveProgramInstance(ProgramInstance transientProgramInstance) {
        ProgramInstance persistentInstance = (ProgramInstance) entityService.createOrUpdate(transientProgramInstance);
        if (persistentInstance.isEnabled()) {
            Program transientProgram = transientProgramInstance.getProgram();
            Program persistentProgram = (Program) getById(transientProgram.getId());
            LocalDate programDueDate = persistentProgram.getDueDate();
            LocalDate instanceEndDate = persistentInstance.getApplicationDeadline();
            if (programDueDate == null || programDueDate.isBefore(instanceEndDate)) {
                persistentProgram.setDueDate(instanceEndDate);
            }
        }
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
        CommentCustomQuestionVersion version = new CommentCustomQuestionVersion().withCommentCustomQuestion(persistentCustomQuestionDefinition).withContent(definition);
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

    public void addClosingDateToProgram(Program program, AdvertClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
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

    public void deleteInactiveAdverts() {
        programDAO.deleteInactiveAdverts();
    }

    public List<ProgramInstance> getActiveProgramInstances(Program program, StudyOption studyOption) {
        return programDAO.getActiveProgramInstances(program, studyOption);
    }

    public LocalDate getNextClosingDate(Program program) {
        return programDAO.getNextClosingDate(program);
    }

    public Project addProject(ProjectDTO projectDTO) {
        // TODO Auto-generated method stub
        return null;
    }

    public void updateProject(Integer id, ProjectDTO projectDTO) {
        // TODO Auto-generated method stub
    }

    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }
    
    public ProgramInstance getExportProgramInstance(Application application) {
        return programDAO.getExportProgramInstance(application);
    }
    
    public ProgramInstance getLatestProgramInstance(Program program) {
        return programDAO.getLatestProgramInstance(program);
    }

}
