package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.TRIAL_PERIOD_LENGTH;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
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
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
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

    public void save(Project project) {
        entityService.save(project);
    }

    public Project create(User user, ProjectDTO projectDTO) {
        PrismScope resourceScope = projectDTO.getResourceScope();
        ResourceParent resourceParent = (ResourceParent) resourceService.getById(resourceScope, projectDTO.getResourceId());

        DepartmentDTO departmentDTO = projectDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        department = resourceScope == PROGRAM ? resourceParent.getProgram().getDepartment() : null;

        Project project = new Project().withUser(user).withParentResource(resourceParent).withDepartment(department);
        copyProjectDetails(project, projectDTO);
        project.setEndDate(new LocalDate().plusMonths(TRIAL_PERIOD_LENGTH));
        return project;
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Project project = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PrismAction.PROJECT_VIEW_EDIT;

        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(project)
                .load(PrismDisplayPropertyDefinition.PROJECT_COMMENT_UPDATED) : commentDTO.getContent();

        ProjectDTO projectDTO = (ProjectDTO) commentDTO.fetchResourceDTO();
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

    public void postProcessProject(Project project, Comment comment) {
        DateTime updatedTimestamp = project.getUpdatedTimestamp();
        project.setUpdatedTimestampSitemap(updatedTimestamp);
        project.getProgram().setUpdatedTimestampSitemap(updatedTimestamp);
        project.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));
    }

    public void synchronizeProjects(Program program) {
        projectDAO.synchronizeProjectDueDates(program);
        projectDAO.synchronizeProjectEndDates(program);
    }

    public void restoreProjects(Program program, LocalDate baseline) {
        List<Project> projects = projectDAO.getProjectsPendingReactivation(program, baseline);
        if (!projects.isEmpty()) {
            State state = stateService.getById(PrismState.PROJECT_APPROVED);
            State previousState = stateService.getById(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION);

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
        if (resource.getResourceScope() == PrismScope.PROJECT) {
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

    public SocialMetadataDTO getSocialMetadata(Project project) {
        return advertService.getSocialMetadata(project.getAdvert());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer projectId) {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        SearchEngineAdvertDTO searchEngineDTO = projectDAO.getSearchEngineAdvert(projectId, activeProjectStates);

        if (searchEngineDTO != null) {
            List<String> relatedUsers = Lists.newArrayList();
            List<User> projectAcademics = userService.getUsersForResourceAndRoles(getById(projectId), PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR);
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

    private void update(Integer projectId, ProjectDTO projectDTO) {
        Project project = entityService.getById(Project.class, projectId);
        copyProjectDetails(project, projectDTO);
    }

    private void copyProjectDetails(Project project, ProjectDTO projectDTO) {
        Advert advert;
        if (project.getAdvert() == null) {
            advert = new Advert();
            advert.setAddress(advertService.createAddressCopy(project.getInstitution().getAdvert().getAddress()));
            project.setAdvert(advert);
        } else {
            advert = project.getAdvert();
        }

        String title = projectDTO.getTitle();
        
        // FIXME (Client): Always pass locale
        PrismLocale locale = projectDTO.getLocale();
        locale = locale == null ? project.getResourceParent().getLocale() : locale;
        
        project.setEndDate(projectDTO.getEndDate());
        project.setTitle(title);
        advert.setTitle(title);
        advert.setSummary(projectDTO.getSummary());
        advert.setApplyHomepage(projectDTO.getApplyHomepage());
        advert.setStudyDurationMinimum(projectDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(projectDTO.getStudyDurationMaximum());
    }

}
