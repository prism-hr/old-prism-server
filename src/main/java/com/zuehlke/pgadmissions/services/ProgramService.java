package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ProgramService {

    @Inject
    private ProgramDAO programDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private AdvertService advertService;

    @Inject
    private StateService stateService;

    @Inject
    private ApplicationContext applicationContext;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
        for (ResourceStudyOption studyOption : program.getStudyOptions()) {
            entityService.save(studyOption);
        }
    }

    public Program getProgramByImportedCode(String importedCode) {
        return getProgramByImportedCode(null, importedCode);
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }

    public ResourceStudyOptionInstance getFirstEnabledProgramStudyOptionInstance(Program program, StudyOption studyOption) {
        return programDAO.getFirstEnabledProgramStudyOptionInstance(program, studyOption);
    }

    public Program create(User user, ProgramDTO programDTO) throws Exception {
        Institution institution = institutionService.getById(programDTO.getInstitutionId());

        AdvertDTO advertDTO = programDTO.getAdvert();
        Advert advert = advertService.createAdvert(user, advertDTO);

        DepartmentDTO departmentDTO = programDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, institution, programDTO.getOpportunityType().name());

        Program program = new Program().withUser(user).withInstitution(institution).withDepartment(department).withAdvert(advert)
                .withOpportunityType(opportunityType).withTitle(advert.getTitle()).withDurationMinimum(programDTO.getDurationMinimum())
                .withDurationMaximum(programDTO.getDurationMaximum()).withRequireProjectDefinition(false)
                .withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD));

        resourceService.setStudyOptions(institution, programDTO.getStudyOptions(), new LocalDate());
        resourceService.setStudyLocations(institution, programDTO.getStudyLocations());
        return program;
    }

    public void postProcessProgram(Program program, Comment comment) {
        DateTime updatedTimestamp = program.getUpdatedTimestamp();
        program.setUpdatedTimestampSitemap(updatedTimestamp);
        program.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);

        if (comment.isProgramApproveOrDeactivateComment()) {
            projectService.synchronizeProjects(program);
            if (comment.isProgramRestoreComment()) {
                projectService.restoreProjects(program, comment.getCreatedTimestamp().toLocalDate());
            }
        }

        advertService.setSequenceIdentifier(program.getAdvert(), program.getSequenceIdentifier().substring(0, 13));
    }

    public List<ResourceStudyOption> getEnabledProgramStudyOptions(Program program) {
        return programDAO.getEnabledProgramStudyOptions(program);
    }

    public ResourceStudyOption getEnabledProgramStudyOption(Program program, StudyOption studyOption) {
        return programDAO.getEnabledProgramStudyOption(program, studyOption);
    }

    public void disableElapsedProgramStudyOptions() {
        LocalDate baseline = new LocalDate();
        programDAO.disableElapsedProgramStudyOptions(baseline);
        programDAO.disableElapsedProgramStudyOptionInstances(baseline);
    }

    public List<ProgramRepresentation> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return programDAO.getSimilarPrograms(institutionId, searchTerm);
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Program program = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PrismAction.PROGRAM_VIEW_EDIT;

        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(program)
                .load(PrismDisplayPropertyDefinition.PROGRAM_COMMENT_UPDATED) : commentDTO.getContent();

        ProgramDTO programDTO = (ProgramDTO) commentDTO.fetchResourceDTO();
        LocalDate dueDate = programDTO.getEndDate();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        if (viewEditAction && !program.getImported() && transitionState == null && dueDate.isAfter(new LocalDate())) {
            transitionState = programDAO.getPreviousState(program);
        }

        Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        update(programId, programDTO);

        return actionService.executeUserAction(program, action, comment);
    }

    public List<String> getPossibleLocations(Program program) {
        return programDAO.getPossibleLocations(program);
    }

    public List<String> getSuggestedDivisions(Integer programId, String location) {
        Program program = getById(programId);
        return programDAO.getSuggestedDivisions(program, location);
    }

    public List<String> getSuggestedStudyAreas(Integer programId, String location, String division) {
        Program program = getById(programId);
        return programDAO.getSuggestedStudyAreas(program, location, division);
    }

    public Integer getActiveProgramCount(Institution institution) {
        Long count = programDAO.getActiveProgramCount(institution);
        return count == null ? null : count.intValue();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return programDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getSitemapEntries(activeProgramStates);
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer programId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        SearchEngineAdvertDTO searchEngineDTO = programDAO.getSearchEngineAdvert(programId, activeProgramStates);

        if (searchEngineDTO != null) {
            searchEngineDTO.setRelatedProjects(projectService.getActiveProjectsByProgram(programId));

            List<String> relatedUsers = Lists.newArrayList();
            List<User> programAcademics = userService.getUsersForResourceAndRoles(getById(programId), PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR);
            for (User programAcademic : programAcademics) {
                relatedUsers.add(programAcademic.getSearchEngineRepresentation());
            }
            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

    public List<ResourceSearchEngineDTO> getActiveProgramsByInstitution(Integer institutionId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getActiveProgramsByInstitution(institutionId, activeProgramStates);
    }

    public List<Integer> getProjects(Integer program) {
        return programDAO.getProjects(program);
    }

    public List<Integer> getApplications(Integer program) {
        return programDAO.getApplications(program);
    }

    private void update(Integer programId, ProgramDTO programDTO) throws Exception {
        Program program = entityService.getById(Program.class, programId);

        DepartmentDTO departmentDTO = programDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        program.setDepartment(department);

        AdvertDTO advertDTO = programDTO.getAdvert();
        Advert advert = program.getAdvert();
        advertService.updateAdvert(userService.getCurrentUser(), advertDTO, advert);

        program.setDurationMinimum(programDTO.getDurationMinimum());
        program.setDurationMaximum(programDTO.getDurationMaximum());

        resourceService.setStudyLocations(program, programDTO.getStudyLocations());

        if (!program.getImported()) {
            OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, //
                    program.getInstitution(), programDTO.getOpportunityType().name());

            program.setOpportunityType(opportunityType);
            program.setTitle(advert.getTitle());

            LocalDate endDate = programDTO.getEndDate();
            if (endDate != null) {
                program.setEndDate(endDate);
            }

            resourceService.setStudyOptions(program, programDTO.getStudyOptions(), new LocalDate());
        }
    }

}
