package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public List<Program> getPrograms(User user) {
        // TODO implement SQL query for basic list;
        return null;
    }

    public Program create(User user, ProgramDTO programDTO) {
        Institution institution = entityService.getById(Institution.class, programDTO.getInstitutionId());
        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withTitle(programDTO.getTitle()).withInstitution(institution)
                .withProgramType(programDTO.getProgramType()).withRequireProjectDefinition(programDTO.getRequireProjectDefinition())
                .withPublishDate(programDTO.getStartDate().toLocalDate()).withDueDate(programDTO.getEndDate().toLocalDate())
                .withImmediateStart(programDTO.getImmediateStart());
        return program;
    }

    public Program getOrImportProgram(Programme programme, Institution institution) throws WorkflowEngineException {
        User proxyCreator = institution.getUser();

        PrismProgramType programType = PrismProgramType.findValueFromString(programme.getName());
        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withImportedCode(programme.getCode())
                .withTitle(programme.getName()).withRequireProjectDefinition(programme.isAtasRegistered()).withImmediateStart(false)
                .withProgramType(programType).withUser(proxyCreator);

        Action importAction = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);
        Role proxyCreatorRole = roleService.getCreatorRole(transientProgram);

        Comment comment = new Comment().withUser(proxyCreator).withCreatedTimestamp(new DateTime()).withAction(importAction).withDeclinedResponse(false)
                .withAssignedUser(proxyCreator, proxyCreatorRole);

        Program persistentProgram = (Program) actionService.executeSystemAction(transientProgram, importAction, comment).getResource();
        return persistentProgram.withTitle(programme.getName()).withRequireProjectDefinition(programme.isAtasRegistered());
    }

    public void saveProgramInstance(ProgramInstance transientProgramInstance) {
        ProgramInstance persistentInstance = entityService.createOrUpdate(transientProgramInstance);
        if (persistentInstance.isEnabled()) {
            Program transientProgram = transientProgramInstance.getProgram();
            Program persistentProgram = (Program) getById(transientProgram.getId());
            
            LocalDate programDueDate = persistentProgram.getDueDate();
            LocalDate instanceEndDate = persistentInstance.getApplicationDeadline();
            
            if (programDueDate == null || programDueDate.isBefore(instanceEndDate)) {
                persistentProgram.setDueDate(instanceEndDate);
            }
            
            LocalDate programPublishDate = persistentProgram.getPublishDate();
            LocalDate instanceStartDate = persistentInstance.getApplicationStartDate();
            
            if (programPublishDate == null || programPublishDate.isAfter(instanceStartDate)) {
                persistentProgram.setPublishDate(instanceStartDate);
            }
        }
    }

    public void updateProgramClosingDates() {
        List<Program> programs = programDAO.getProgramsWithElapsedClosingDates();
        
        for (Program program : programs) {
            AdvertClosingDate nextClosingDate = programDAO.getNextClosingDate(program);
            program.setClosingDate(nextClosingDate);
        }
        
    }
    
    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }

    public ProgramInstance getExportProgramInstance(Application application) {
        return programDAO.getExportProgramInstance(application);
    }

    public ProgramInstance getEarliestProgramInstance(Application application) {
        return programDAO.getEarliestProgramInstance(application);
    }

    public ProgramInstance getLatestProgramInstance(Application application) {
        return programDAO.getLatestProgramInstance(application);
    }

    public ProgramInstance getLatestProgramInstance(Program program) {
        return programDAO.getLatestProgramInstance(program);
    }

    public ActionOutcome performAction(Integer programId, CommentDTO commentDTO) {
        Program program = entityService.getById(Program.class, programId);
        PrismAction actionId = commentDTO.getAction();

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withAction(action)
                .withTransitionState(transitionState).withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        ProgramDTO programDTO = commentDTO.getProgram();
        if (programDTO != null) {
            // modify program
            update(programId, programDTO);
        }

        return actionService.executeUserAction(program, action, comment);
    }

    public void update(Integer programId, ProgramDTO programDTO) {
        Program program = entityService.getById(Program.class, programId);

        program.setProgramType(programDTO.getProgramType());
        program.setTitle(programDTO.getTitle());
        program.setDescription(programDTO.getDescription());

        // TODO set study options, start date and end date

        program.setRequireProjectDefinition(programDTO.getRequireProjectDefinition());
        program.setImmediateStart(programDTO.getImmediateStart());

    }
}
