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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;

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

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public Program create(User user, ProgramDTO programDTO) {
        String title = programDTO.getTitle();

        Advert advert = new Advert().withTitle(title);
        // TODO: add global defaults
        Institution institution = entityService.getById(Institution.class, programDTO.getInstitution());
        ProgramType programType = importedEntityService.getImportedEntityByCode(ProgramType.class, institution, programDTO.getProgramType().name());

        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withTitle(title).withInstitution(institution)
                .withProgramType(programType).withRequireProjectDefinition(programDTO.getRequireProjectDefinition()).withImported(false).withAdvert(advert)
                .withDueDate(programDTO.getDueDate());

        // TODO: study options
        return program;
    }

    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }

    public ProgramStudyOptionInstance getFirstEnabledProgramStudyOptionInstance(Program program, StudyOption studyOption) {
        return programDAO.getFirstEnabledProgramStudyOptionInstance(program, studyOption);
    }

    public ActionOutcomeDTO performAction(Integer programId, CommentDTO commentDTO) throws Exception {
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
        AdvertDTO advertDTO = programDTO.getAdvert();
        String title = programDTO.getTitle();
        Program program = entityService.getById(Program.class, programId);
        Advert advert = program.getAdvert();

        ProgramType programType = importedEntityService.getImportedEntityByCode(ProgramType.class, program.getInstitution(), programDTO.getProgramType().name());

        program.setDueDate(programDTO.getDueDate());
        program.setRequireProjectDefinition(programDTO.getRequireProjectDefinition());

        if (!program.getImported()) {
            program.setProgramType(programType);
            program.setTitle(title);

            programDAO.deleteProgramStudyOptionInstances(program);
            programDAO.deleteProgramStudyOptions(program);
            program.getStudyOptions().clear();
            for (PrismStudyOption prismStudyOption : programDTO.getStudyOptions()) {
                StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, program.getInstitution(), prismStudyOption.name());
                ProgramStudyOption programStudyOption = new ProgramStudyOption().withStudyOption(studyOption).withApplicationStartDate(new LocalDate()).withApplicationCloseDate(program.getDueDate()).withEnabled(true).withProgram(program);
                entityService.save(programStudyOption);
                program.getStudyOptions().add(programStudyOption);
            }
            advert.setTitle(title);
        }

        advert.setDescription(advertDTO.getDescription());
        advert.setSummary(advertDTO.getSummary());
    }

    public void postProcessProgram(Program program, Comment comment) {
        if (comment.isProgramCreateOrUpdateComment()) {
            program.getAdvert().setSequenceIdentifier(program.getSequenceIdentifier() + "-" + program.getResourceScope().getShortCode());
            projectService.updateProjectsLinkedToProgramDueDate(program);
        }
    }

    // TODO handle case where there are no enabled study options
    public List<ProgramStudyOption> getEnabledProgramStudyOptions(Program program) {
        return programDAO.getEnabledProgramStudyOptions(program);
    }

    public ProgramStudyOption getEnabledProgramStudyOption(Program program, StudyOption studyOption) {
        return programDAO.getEnabledProgramStudyOption(program, studyOption);
    }

    public LocalDate getProgramClosureDate(Program program) {
        return programDAO.getProgramClosureDate(program);
    }

    public LocalDate resolveDueDateBaseline(Program program, Comment comment) {
        if (comment.isProgramCreateOrUpdateComment()) {
            return getProgramClosureDate(program);
        }
        return null;
    }

    public List<Program> getProgramsWithElapsedStudyOptions(LocalDate baseline) {
        return programDAO.getProgramsWithElapsedStudyOptions(baseline);
    }

    public void updateProgramStudyOptions(Program program, LocalDate baseline) {
        List<ProgramStudyOption> elapsedOptions = programDAO.getElapsedStudyOptions(program, baseline);

        for (ProgramStudyOption elapsedOption : elapsedOptions) {
            elapsedOption.setEnabled(false);
        }

        if (program.getStudyOptions().size() == elapsedOptions.size()) {
            program.setDueDate(baseline);
        }
    }
}
