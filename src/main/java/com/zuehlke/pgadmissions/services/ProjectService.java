package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public Project create(User user, ProjectDTO projectDTO) {
        // TODO: remember to set title in advert to value of project title
        return null;
    }

    public void save(Project project) {
        entityService.save(project);
    }

    public ActionOutcomeDTO performAction(Integer projectId, CommentDTO commentDTO) throws Exception {
        Project project = entityService.getById(Project.class, projectId);
        PrismAction actionId = commentDTO.getAction();

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        ProjectDTO projectDTO = commentDTO.getProject();
        if (projectDTO != null) {
            // modify project
            update(projectId, projectDTO);
        }

        return actionService.executeUserAction(project, action, comment);
    }

    public void update(Integer projectId, ProjectDTO projectDTO) {
        String title = projectDTO.getTitle();
        Project project = entityService.getById(Project.class, projectId);
        Advert advert = project.getAdvert();

        project.setDueDate(projectDTO.getDueDate());
        project.setTitle(title);
        advert.setTitle(title);
        advert.setSummary(projectDTO.getSummary());
        advert.setStudyDurationMinimum(projectDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(projectDTO.getStudyDurationMaximum());
    }

    public LocalDate resolveDueDateBaseline(Project project, Comment comment) {
        if (comment.isProjectCreateOrUpdateComment()) {
            AdvertClosingDate closingDate = project.getAdvert().getClosingDate();
            if (closingDate == null) {
                return programService.getProgramClosureDate(project.getProgram());
            } else {
                return closingDate.getClosingDate();
            }
        }
        return null;
    }

    public void postProcessProject(Project project, Comment comment) {
        if (comment.isProjectCreateOrUpdateComment()) {
            project.getAdvert().setSequenceIdentifier(project.getSequenceIdentifier() + "-" + project.getResourceScope().getShortCode());
        }
    }

    public void updateProjectsLinkedToProgramDueDate(Program program) {
        List<Project> linkedProjects = projectDAO.getProjectsLinkedToProgramDueDate(program);

        for (Project linkedProject : linkedProjects) {
            linkedProject.setDueDate(program.getDueDate());
        }
    }

}
