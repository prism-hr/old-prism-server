package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private SystemService systemService;

    @Autowired
    private AdvertService advertService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public void save(Project project) {
        entityService.save(project);
    }

    public ActionOutcomeDTO performAction(Integer projectId, CommentDTO commentDTO) throws DeduplicationException {
        Project project = entityService.getById(Project.class, projectId);
        PrismAction actionId = commentDTO.getAction();

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        ProjectDTO projectDTO = commentDTO.getProject();
        if (projectDTO != null) {
            update(projectId, projectDTO);
        }

        return actionService.executeUserAction(project, action, comment);
    }

    public Project create(User user, ProjectDTO projectDTO) {
        Program program = entityService.getById(Program.class, projectDTO.getProgramId());
        Project project = new Project().withUser(user).withSystem(systemService.getSystem()).withInstitution(program.getInstitution()).withProgram(program);
        copyProjectDetails(project, projectDTO);
        return project;
    }

    public void update(Integer projectId, ProjectDTO projectDTO) {
        Project project = entityService.getById(Project.class, projectId);
        copyProjectDetails(project, projectDTO);
    }

    private void copyProjectDetails(Project project, ProjectDTO projectDTO) {
        if (project.getAdvert() == null) {
            project.setAdvert(new Advert());
        }
        Advert advert = project.getAdvert();

        String title = projectDTO.getTitle();
        project.setDueDate(projectDTO.getDueDate());
        project.setTitle(title);
        advert.setTitle(title);
        advert.setSummary(projectDTO.getSummary());
        advert.setStudyDurationMinimum(projectDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(projectDTO.getStudyDurationMaximum());
        advert.setAddress(advertService.createAddressCopy(project.getInstitution().getAddress()));
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
