package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_PENDING_REACTIVATION;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO.ResourceParentAttributesDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ProjectService {

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private UserService userService;

    @Inject
    private StateService stateService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ApplicationContext applicationContext;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Project project = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PROJECT_VIEW_EDIT;
        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(project)
                .load(PROJECT_COMMENT_UPDATED) : commentDTO.getContent();

        OpportunityDTO projectDTO = (OpportunityDTO) commentDTO.getResource();
        LocalDate dueDate = projectDTO.getEndDate();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        if (viewEditAction && !project.getProgram().getImported() && transitionState == null && dueDate.isAfter(new LocalDate())) {
            transitionState = projectDAO.getPreviousState(project);
        }

        Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        update(programId, projectDTO);

        return actionService.executeUserAction(project, action, comment);
    }

    public void synchronizeProjects(Program program) {
        projectDAO.synchronizeProjectDueDates(program);
        projectDAO.synchronizeProjectEndDates(program);
    }

    public void restoreProjects(Program program, LocalDate baseline) {
        List<Project> projects = projectDAO.getProjectsPendingReactivation(program, baseline);
        if (!projects.isEmpty()) {
            State state = stateService.getById(PROJECT_APPROVED);
            State previousState = stateService.getById(PROJECT_DISABLED_PENDING_REACTIVATION);

            for (Project project : projects) {
                project.setState(state);
                project.setPreviousState(previousState);
                project.setDueDate(project.getEndDate());

                project.getResourceStates().clear();
                project.getResourcePreviousStates().clear();
                entityService.flush();

                entityService.createOrUpdate(new ResourceState().withResource(project).withState(state).withPrimaryState(true));
                entityService.createOrUpdate(new ResourcePreviousState().withResource(project).withState(previousState).withPrimaryState(true));
            }
        }
    }

    public Integer getActiveProjectCount(ResourceParent resource) {
        if (resource.getResourceScope() == PROJECT) {
            throw new Error();
        }
        Long count = projectDAO.getActiveProjectCount(resource);
        return count == null ? null : count.intValue();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return projectDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return projectDAO.getSitemapEntries(activeProjectStates);
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer projectId) {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        SearchEngineAdvertDTO searchEngineDTO = projectDAO.getSearchEngineAdvert(projectId, activeProjectStates);

        if (searchEngineDTO != null) {
            List<String> relatedUsers = Lists.newArrayList();
            List<User> projectAcademics = userService.getUsersForResourceAndRoles(getById(projectId), PROJECT_SUPERVISOR_GROUP.getRoles());
            for (User projectAcademic : projectAcademics) {
                relatedUsers.add(projectAcademic.getSearchEngineRepresentation());
            }

            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByProgram(Integer programId) {
        List<PrismState> activeStates = stateService.getActiveProjectStates();
        return projectDAO.getActiveProjectsByProgram(programId, activeStates);
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByInstitution(Integer institutionId) {
        List<PrismState> activeStates = stateService.getActiveProjectStates();
        return projectDAO.getActiveProjectsByInstitution(institutionId, activeStates);
    }

    private void update(Integer projectId, OpportunityDTO projectDTO) throws Exception {
        Project project = entityService.getById(Project.class, projectId);

        DepartmentDTO departmentDTO = projectDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        project.setDepartment(department);

        AdvertDTO advertDTO = projectDTO.getAdvert();
        Advert advert = project.getAdvert();
        advertService.updateAdvert(userService.getCurrentUser(), advertDTO, advert);
        project.setTitle(advert.getTitle());

        project.setDurationMinimum(projectDTO.getDurationMinimum());
        project.setDurationMaximum(projectDTO.getDurationMaximum());

        ResourceParentAttributesDTO attributes = projectDTO.getAttributes();
        resourceService.setResourceConditions(project, attributes.getResourceConditions());

        if (BooleanUtils.isFalse(project.getImported())) {
            OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, //
                    project.getInstitution(), projectDTO.getOpportunityType().name());

            project.setOpportunityType(opportunityType);
            LocalDate endDate = projectDTO.getEndDate();
            if (endDate != null) {
                project.setEndDate(endDate);
            }

            resourceService.setStudyOptions(project, attributes.getStudyOptions(), new LocalDate());
        }

        resourceService.setStudyLocations(project, attributes.getStudyLocations());
    }

}
