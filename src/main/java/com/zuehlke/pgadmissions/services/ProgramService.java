package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
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

    @Autowired
    private AdvertService advertService;

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
        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withInstitution(institution).withImported(false);
        copyProgramDetails(program, programDTO);
        copyStudyOptions(program, programDTO);

        // TODO: add global defaults
        return program;
    }

    public void update(Integer programId, ProgramDTO programDTO) {
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
            ProgramType programType = importedEntityService.getImportedEntityByCode(ProgramType.class, program.getInstitution(), programDTO.getProgramType().name());
            String title = programDTO.getTitle();

            program.setProgramType(programType);
            program.setTitle(title);
            program.setLocale(programDTO.getLocale());
            advert.setTitle(title);
        }

        program.setDueDate(programDTO.getDueDate());
        program.setRequireProjectDefinition(programDTO.getRequireProjectDefinition());
        advert.setSummary(programDTO.getSummary());
        advert.setStudyDurationMinimum(programDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(programDTO.getStudyDurationMaximum());
        advert.setAddress(advertService.createAddressCopy(program.getInstitution().getAddress()));
    }

    private void copyStudyOptions(Program program, ProgramDTO programDTO) {
        for (PrismStudyOption prismStudyOption : programDTO.getStudyOptions()) {
            StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, program.getInstitution(), prismStudyOption.name());
            ProgramStudyOption programStudyOption = new ProgramStudyOption().withStudyOption(studyOption).withApplicationStartDate(new LocalDate())
                    .withApplicationCloseDate(program.getDueDate()).withEnabled(true).withProgram(program);
            program.getStudyOptions().add(programStudyOption);
        }
    }

    public void postProcessProgram(Program program, Comment comment) {
        if (comment.isProgramCreateOrUpdateComment()) {
            program.getAdvert().setSequenceIdentifier(program.getSequenceIdentifier() + "-" + program.getResourceScope().getShortCode());
            projectService.updateProjectsLinkedToProgramDueDate(program);
        }
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

    public LocalDate resolveDueDateBaseline(Program program, Comment comment) {
        if (comment.isProgramCreateOrUpdateComment()) {
            return getProgramClosureDate(program);
        }
        return null;
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

}
