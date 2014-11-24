package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ApplicationContext applicationContext;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
        for (ProgramStudyOption studyOption : program.getStudyOptions()) {
            entityService.save(studyOption);
        }
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }

    public ProgramStudyOptionInstance getFirstEnabledProgramStudyOptionInstance(Program program, StudyOption studyOption) {
        return programDAO.getFirstEnabledProgramStudyOptionInstance(program, studyOption);
    }

    public Program create(User user, ProgramDTO programDTO) {
        Institution institution = entityService.getById(Institution.class, programDTO.getInstitutionId());
        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withInstitution(institution).withImported(false)
                .withRequireProjectDefinition(false);
        copyProgramDetails(program, programDTO);
        copyStudyOptions(program, programDTO);
        return program;
    }

    public void postProcessProgram(Program program, Comment comment) {
        if (comment.isProgramApproveOrDeactivateComment()) {
            projectService.sychronizeProject(program);
        }
        Advert advert = program.getAdvert();
        advert.setSequenceIdentifier(program.getSequenceIdentifier().substring(0, 13) + String.format("%010d", advert.getId()));
    }

    public List<ProgramStudyOption> getEnabledProgramStudyOptions(Program program) {
        return programDAO.getEnabledProgramStudyOptions(program);
    }

    public ProgramStudyOption getEnabledProgramStudyOption(Program program, StudyOption studyOption) {
        return programDAO.getEnabledProgramStudyOption(program, studyOption);
    }

    public LocalDate getProgramClosureDate(Program program) {
        return programDAO.getProgramClosureDate(program);
    }

    public List<Program> getProgramsWithElapsedStudyOptions(LocalDate baseline) {
        return programDAO.getProgramsWithElapsedStudyOptions(baseline);
    }

    public void updateProgramStudyOptions(Program transientProgram, LocalDate baseline) {
        Program persistentProgram = getById(transientProgram.getId());
        List<ProgramStudyOption> elapsedOptions = programDAO.getElapsedStudyOptions(persistentProgram, baseline);

        for (ProgramStudyOption elapsedOption : elapsedOptions) {
            elapsedOption.setEnabled(false);
        }

        if (persistentProgram.getStudyOptions().size() == elapsedOptions.size()) {
            persistentProgram.setDueDate(baseline);
        }
    }

    public List<ProgramRepresentation> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return programDAO.getSimilarPrograms(institutionId, searchTerm);
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws DeduplicationException {
        User user = userService.getById(commentDTO.getUser());
        Program program = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PrismAction.PROGRAM_VIEW_EDIT;

        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(program, user)
                .load(PrismDisplayPropertyDefinition.PROGRAM_COMMENT_UPDATED) : commentDTO.getContent();

        ProgramDTO programDTO = (ProgramDTO) commentDTO.fetchResouceDTO();
        LocalDate dueDate = programDTO.getEndDate();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        if (viewEditAction && !program.getImported() && transitionState == null && dueDate.isAfter(new LocalDate())) {
            transitionState = programDAO.getPreviousState(program);
        }
        
        Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        if (programDTO != null) {
            update(programId, programDTO);
        }

        return actionService.executeUserAction(program, action, comment);
    }

    public List<String> getPossibleLocations(Program program) {
        return programDAO.getPossibleLocations(program);
    }

    public List<String> getSuggestedDivisions(Program program, String location) {
        return programDAO.getSuggestedDivisions(program, location);
    }

    public List<String> getSuggestedStudyAreas(Program program, String location, String division) {
        return programDAO.getSuggestedStudyAreas(program, location, division);
    }

    private void update(Integer programId, ProgramDTO programDTO) {
        Program program = entityService.getById(Program.class, programId);
        copyProgramDetails(program, programDTO);

        if (!program.getImported()) {
            programDAO.deleteProgramStudyOptionInstances(program);
            programDAO.deleteProgramStudyOptions(program);
            program.getStudyOptions().clear();
            copyStudyOptions(program, programDTO);
            for (ProgramStudyOption studyOption : program.getStudyOptions()) {
                entityService.save(studyOption);
            }
        }
    }

    private void copyProgramDetails(Program program, ProgramDTO programDTO) {
        if (program.getAdvert() == null) {
            program.setAdvert(new Advert());
        }

        Advert advert = program.getAdvert();

        if (!program.getImported()) {
            ProgramType programType = importedEntityService.getImportedEntityByCode(ProgramType.class, program.getInstitution(), programDTO.getProgramType()
                    .name());
            String title = programDTO.getTitle();

            program.setProgramType(programType);
            program.setTitle(title);
            advert.setTitle(title);

            program.setEndDate(programDTO.getEndDate());
        }

        program.getLocations().clear();
        entityService.flush();
        for (String location : programDTO.getLocations()) {
            program.addLocation(location);
        }

        advert.setSummary(programDTO.getSummary());
        advert.setApplyHomepage(programDTO.getApplyHomepage());
        advert.setStudyDurationMinimum(programDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(programDTO.getStudyDurationMaximum());
        advert.setAddress(advertService.createAddressCopy(program.getInstitution().getAddress()));
    }

    private void copyStudyOptions(Program program, ProgramDTO programDTO) {
        for (PrismStudyOption prismStudyOption : programDTO.getStudyOptions()) {
            StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, program.getInstitution(), prismStudyOption.name());
            ProgramStudyOption programStudyOption = new ProgramStudyOption().withStudyOption(studyOption).withApplicationStartDate(new LocalDate())
                    .withApplicationCloseDate(program.getEndDate()).withEnabled(true).withProgram(program);
            program.getStudyOptions().add(programStudyOption);
        }
    }

}
