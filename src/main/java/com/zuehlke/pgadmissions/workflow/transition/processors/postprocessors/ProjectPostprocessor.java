package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProjectPostprocessor implements ResourceProcessor {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Project project = (Project) resource;
        DateTime updatedTimestamp = project.getUpdatedTimestamp();
        project.setUpdatedTimestampSitemap(updatedTimestamp);
        if (project.getProgram() != null) {
            project.getProgram().setUpdatedTimestampSitemap(updatedTimestamp);
        }
        project.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));

        if (comment.isCreateComment()) {
            resourceService.synchronizePartner(project, comment);
        }

        if (comment.isProjectViewEditComment()) {
            connectProjectSupervisors(project, comment);
            resourceService.resynchronizePartner(project, comment);
        }

        if (comment.isProjectPartnerApproveComment()) {
            postProcessProjectPartnerApproval(project, comment);
        }
    }

    private void connectProjectSupervisors(Project project, Comment comment) {
        if (!comment.getAssignedUsers().isEmpty()) {
            List<User> users = Lists.newLinkedList(roleService.getRoleUsers(project, PROJECT_PRIMARY_SUPERVISOR));
            users.addAll(roleService.getRoleUsers(project, PROJECT_SECONDARY_SUPERVISOR));
            userService.createUserConnections(users);
        }
    }

    private void postProcessProjectPartnerApproval(Project project, Comment comment) throws Exception {
        User user = comment.getUser();
        Action action = actionService.getById(PROJECT_COMPLETE_APPROVAL_STAGE);
        if (actionService.checkActionAvailable(project, action, user, false)) {
            State transitionState = stateService.getById(PROJECT_APPROVED);
            Comment approveComment = new Comment().withUser(user).withResource(project).withAction(action).withDeclinedResponse(false)
                    .withTransitionState(transitionState).withCreatedTimestamp(new DateTime());
            actionService.executeAction(project, action, approveComment);
        }
    }

}
