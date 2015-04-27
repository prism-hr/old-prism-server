package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

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
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
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

    public void save(Project project) {
        entityService.save(project);
    }

    public Project create(User user, ProjectDTO projectDTO) throws Exception {
        PrismScope resourceScope = projectDTO.getResourceScope();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, projectDTO.getResourceId());

        AdvertDTO advertDTO = projectDTO.getAdvert();
        Advert advert = advertService.createAdvert(user, advertDTO);

        DepartmentDTO departmentDTO = projectDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);

        Program program = null;
        boolean imported = false;
        if (resourceScope == PROGRAM) {
            program = (Program) resource;
            imported = BooleanUtils.isTrue(program.getImported());
        }

        OpportunityType opportunityType;
        if (imported) {
            opportunityType = program.getOpportunityType();
        } else {
            opportunityType = importedEntityService.getByCode(OpportunityType.class, resource.getInstitution(), projectDTO.getOpportunityType().name());
        }

        Project project = new Project().withUser(user).withResource(resource).withDepartment(department).withAdvert(advert)
                .withOpportunityType(opportunityType).withTitle(advert.getTitle()).withDurationMinimum(projectDTO.getDurationMinimum())
                .withDurationMaximum(projectDTO.getDurationMaximum()).withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD));
        
        ResourceParentAttributesDTO attributes = projectDTO.getAttributes();
        resourceService.setResourceConditions(project, attributes.getConditions());

        if (!imported) {
            resourceService.setStudyOptions(project, attributes.getStudyOptions(), new LocalDate());
        }
        
        resourceService.setStudyLocations(project, attributes.getStudyLocations());
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

    private void update(Integer projectId, ProjectDTO projectDTO) throws Exception {
        Program project = entityService.getById(Program.class, projectId);

        DepartmentDTO departmentDTO = projectDTO.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);
        project.setDepartment(department);

        AdvertDTO advertDTO = projectDTO.getAdvert();
        Advert advert = project.getAdvert();
        advertService.updateAdvert(userService.getCurrentUser(), advertDTO, advert);

        project.setDurationMinimum(projectDTO.getDurationMinimum());
        project.setDurationMaximum(projectDTO.getDurationMaximum());

        Program program = null;
        boolean imported = false;
        ResourceParent resource = (ResourceParent) project.getParentResource();
        if (project.getParentResource().getResourceScope() == PROGRAM) {
            program = (Program) resource;
            imported = BooleanUtils.isTrue(program.getImported());
        }

        ResourceParentAttributesDTO attributes = projectDTO.getAttributes();
        resourceService.setResourceConditions(project, attributes.getConditions());
        
        if (!imported) {
            OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, //
                    project.getInstitution(), projectDTO.getOpportunityType().name());

            project.setOpportunityType(opportunityType);
            project.setTitle(advert.getTitle());

            LocalDate endDate = projectDTO.getEndDate();
            if (endDate != null) {
                project.setEndDate(endDate);
            }

            resourceService.setStudyOptions(project, attributes.getStudyOptions(), new LocalDate());
        }
        
        resourceService.setStudyLocations(project, attributes.getStudyLocations());
    }

}
