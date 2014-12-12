package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationContext applicationContext;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public void save(Project project) {
        entityService.save(project);
    }

    public Project create(User user, ProjectDTO projectDTO) {
        Program program = entityService.getById(Program.class, projectDTO.getProgramId());
        Project project = new Project().withUser(user).withSystem(systemService.getSystem()).withInstitution(program.getInstitution()).withProgram(program);
        copyProjectDetails(project, projectDTO);
        return project;
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws DeduplicationException, InstantiationException,
            IllegalAccessException {
        User user = userService.getById(commentDTO.getUser());
        Project project = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PrismAction.PROJECT_VIEW_EDIT;

        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(project, user)
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

        if (projectDTO != null) {
            update(programId, projectDTO);
        }

        return actionService.executeUserAction(project, action, comment);
    }

    private void update(Integer projectId, ProjectDTO projectDTO) {
        Project project = entityService.getById(Project.class, projectId);
        copyProjectDetails(project, projectDTO);
    }

    private void copyProjectDetails(Project project, ProjectDTO projectDTO) {
        Advert advert;
        if (project.getAdvert() == null) {
            advert = new Advert();
            advert.setAddress(advertService.createAddressCopy(project.getInstitution().getAddress()));
            project.setAdvert(advert);
        } else {
            advert = project.getAdvert();
        }

        String title = projectDTO.getTitle();
        project.setEndDate(projectDTO.getEndDate());
        project.setTitle(title);
        advert.setTitle(title);
        advert.setSummary(projectDTO.getSummary());
        advert.setApplyHomepage(projectDTO.getApplyHomepage());
        advert.setStudyDurationMinimum(projectDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(projectDTO.getStudyDurationMaximum());
    }

    public void postProcessProject(Project project, Comment comment) {
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));
    }

    public void sychronizeProjects(Program program) {
        projectDAO.synchronizeProjectDueDates(program);
        projectDAO.synchronizeProjectEndDates(program);
    }

    public void restoreProjects(Program program, LocalDate baseline) {
        for (Project project : projectDAO.getProjectsPendingReactivation(program, baseline)) {
            project.setState(stateService.getById(PrismState.PROJECT_APPROVED));
            project.setPreviousState(stateService.getById(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION));
            project.setDueDate(project.getEndDate());
        }
    }

    public Integer getActiveProjectCount(ResourceParent resource) {
        if (resource.getResourceScope() == PrismScope.PROJECT) {
            throw new Error();
        }
        Long count = projectDAO.getActiveProjectCount(resource);
        return count == null ? null : count.intValue();
    }

}
