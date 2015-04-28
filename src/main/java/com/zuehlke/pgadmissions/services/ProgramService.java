package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.*;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO.ResourceParentAttributesDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;

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

        OpportunityDTO programDTO = (OpportunityDTO) commentDTO.getResource();
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
            List<User> programAcademics = userService.getUsersForResourceAndRoles(getById(programId), PROJECT_SUPERVISOR_GROUP.getRoles());
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

    private void update(Integer programId, OpportunityDTO programDTO) throws Exception {
        Program program = entityService.getById(Program.class, programId);

        DepartmentDTO departmentDTO = programDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        program.setDepartment(department);

        AdvertDTO advertDTO = programDTO.getAdvert();
        Advert advert = program.getAdvert();
        advertService.updateAdvert(userService.getCurrentUser(), advertDTO, advert);

        program.setDurationMinimum(programDTO.getDurationMinimum());
        program.setDurationMaximum(programDTO.getDurationMaximum());

        ResourceParentAttributesDTO attributes = programDTO.getAttributes();
        resourceService.setResourceConditions(program, attributes.getResourceConditions());
        resourceService.setStudyLocations(program, attributes.getStudyLocations());

        if (!program.getImported()) {
            OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, //
                    program.getInstitution(), programDTO.getOpportunityType().name());

            program.setOpportunityType(opportunityType);
            program.setTitle(advert.getTitle());

            LocalDate endDate = programDTO.getEndDate();
            if (endDate != null) {
                program.setEndDate(endDate);
            }

            resourceService.setStudyOptions(program, attributes.getStudyOptions(), new LocalDate());
        }
    }

}
