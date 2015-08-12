package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class DepartmentReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (Department oldDepartment : oldUser.getDepartments()) {
            resourceService.reassignResource(oldDepartment, newUser, userProperty);
        }
    }

}
