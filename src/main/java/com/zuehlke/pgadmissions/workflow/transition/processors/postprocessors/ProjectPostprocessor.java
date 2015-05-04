package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;

@Component
public class ProjectPostprocessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Project project = (Project) resource;
        DateTime updatedTimestamp = project.getUpdatedTimestamp();
        project.setUpdatedTimestampSitemap(updatedTimestamp);
        if(project.getProgram() != null) {
            project.getProgram().setUpdatedTimestampSitemap(updatedTimestamp);
        }
        project.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));

        if (comment.isCreateComment()) {
            resourceService.synchronizePartner(project, comment);
        }

        if (comment.isProjectViewEditComment()) {
            connectProjectSupervisors(project, comment);
        }

        if (comment.isSponsorshipComment()) {
            advertService.synchronizeSponsorship(project, comment);
        }
    }

    private void connectProjectSupervisors(Project project, Comment comment) {
        if (!comment.getAssignedUsers().isEmpty()) {
            List<User> users = Lists.newLinkedList(roleService.getRoleUsers(project, PROJECT_PRIMARY_SUPERVISOR));
            users.addAll(roleService.getRoleUsers(project, PROJECT_SECONDARY_SUPERVISOR));
            userService.createUserConnections(users);
        }
    }

}
