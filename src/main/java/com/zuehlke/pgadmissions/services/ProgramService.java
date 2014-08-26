package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
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
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
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

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
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
        String title = programDTO.getTitle();

        Advert advert = new Advert().withTitle(title).withPublishDate(programDTO.getStartDate().toLocalDate())
                .withImmediateStart(programDTO.getImmediateStart());

        Institution institution = entityService.getById(Institution.class, programDTO.getInstitutionId());

        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withTitle(title).withInstitution(institution)
                .withProgramType(programDTO.getProgramType()).withRequireProjectDefinition(programDTO.getRequireProjectDefinition()).withAdvert(advert)
                .withDueDate(programDTO.getEndDate().toLocalDate());
        return program;
    }

    public Program getOrImportProgram(Programme programme, Institution institution) throws WorkflowEngineException {
        User proxyCreator = institution.getUser();

        PrismProgramType programType = PrismProgramType.findValueFromString(programme.getName());

        String title = programme.getName();
        Advert transientAdvert = new Advert().withTitle(title).withImmediateStart(false);

        Program transientProgram = new Program().withSystem(systemService.getSystem()).withInstitution(institution).withImportedCode(programme.getCode())
                .withTitle(title).withRequireProjectDefinition(programme.isAtasRegistered()).withAdvert(transientAdvert).withProgramType(programType)
                .withUser(proxyCreator);

        Action importAction = actionService.getById(PrismAction.INSTITUTION_IMPORT_PROGRAM);
        Role proxyCreatorRole = roleService.getCreatorRole(transientProgram);

        Comment comment = new Comment().withUser(proxyCreator).withCreatedTimestamp(new DateTime()).withAction(importAction).withDeclinedResponse(false)
                .withAssignedUser(proxyCreator, proxyCreatorRole);

        Program persistentProgram = (Program) actionService.executeSystemAction(transientProgram, importAction, comment).getResource();
        return persistentProgram.withTitle(programme.getName()).withRequireProjectDefinition(programme.isAtasRegistered());
    }
    
    public StudyOption getOrImportProgramStudyOption(Institution institution, ModeOfAttendance modeOfAttendance) {
        String externalcode = modeOfAttendance.getCode();
        PrismStudyOption internalCode = PrismStudyOption.findValueFromString(externalcode);
        StudyOption transientStudyOption = new StudyOption().withInstitution(institution).withCode(internalCode).withName(externalcode).withEnabled(true);
        return entityService.createOrUpdate(transientStudyOption);
    }

    public void getOrImportProgramInstance(ProgramInstance transientProgramInstance) {
        ProgramInstance persistentInstance = entityService.createOrUpdate(transientProgramInstance);
        if (persistentInstance.isEnabled()) {
            Program transientProgram = transientProgramInstance.getProgram();
            Program persistentProgram = getById(transientProgram.getId());
            Advert persistentAdvert = persistentProgram.getAdvert();

            LocalDate programDueDate = persistentProgram.getDueDate();
            LocalDate instanceEndDate = persistentInstance.getApplicationDeadline();

            if (programDueDate == null || programDueDate.isBefore(instanceEndDate)) {
                persistentProgram.setDueDate(instanceEndDate);
            }

            LocalDate programPublishDate = persistentAdvert.getPublishDate();
            LocalDate instanceStartDate = persistentInstance.getApplicationStartDate();

            if (programPublishDate == null || programPublishDate.isAfter(instanceStartDate)) {
                persistentAdvert.setPublishDate(instanceStartDate);
            }
        }
    }

    public void updateProgramClosingDates() {
        List<Program> programs = programDAO.getProgramsWithElapsedClosingDates();

        for (Program program : programs) {
            AdvertClosingDate nextClosingDate = programDAO.getNextClosingDate(program);
            Advert advert = program.getAdvert();
            advert.setClosingDate(nextClosingDate);
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

    public ActionOutcomeDTO performAction(Integer programId, CommentDTO commentDTO) {
        Program program = entityService.getById(Program.class, programId);
        PrismAction actionId = commentDTO.getAction();

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        ProgramDTO programDTO = commentDTO.getProgram();
        if (programDTO != null) {
            // modify program
            update(programId, programDTO);
        }

        return actionService.executeUserAction(program, action, comment);
    }

    public void update(Integer programId, ProgramDTO programDTO) {
        String title = programDTO.getTitle();
        Program program = entityService.getById(Program.class, programId);
        Advert advert = program.getAdvert();

        program.setProgramType(programDTO.getProgramType());
        program.setTitle(title);
        advert.setDescription(programDTO.getDescription());

        // TODO set study options, start date and end date

        program.setRequireProjectDefinition(programDTO.getRequireProjectDefinition());
        advert.setImmediateStart(programDTO.getImmediateStart());
        advert.setTitle(title);

    }

    public void postProcessProgram(Program program, Comment comment) {
        PrismActionCategory actionCategory = comment.getAction().getActionCategory();
        if (Arrays.asList(PrismActionCategory.CREATE_RESOURCE, PrismActionCategory.VIEW_EDIT_RESOURCE).contains(actionCategory)) {
            program.getAdvert().setSequenceIdentifier(program.getSequenceIdentifier() + "-" + program.getResourceScope().getShortCode());
        } 
    }

}
