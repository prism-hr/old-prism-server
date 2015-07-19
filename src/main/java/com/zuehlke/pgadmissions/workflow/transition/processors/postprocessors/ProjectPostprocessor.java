package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProjectPostprocessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Project project = (Project) resource;
        DateTime updatedTimestamp = project.getUpdatedTimestamp();
        project.setUpdatedTimestampSitemap(updatedTimestamp);

        Program program = project.getProgram();
        if (program != null) {
            program.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        Department department = project.getDepartment();
        if (department != null) {
            department.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        project.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));

        if (comment.isProjectViewEditComment()) {
            connectProjectSupervisors(project, comment);
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
