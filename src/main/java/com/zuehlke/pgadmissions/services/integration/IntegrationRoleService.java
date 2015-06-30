package com.zuehlke.pgadmissions.services.integration;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class IntegrationRoleService {

    @Inject
    private IntegrationUserService integrationUserService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    public List<ResourceUserRolesRepresentation> getResourceUserRoleRepresentations(Resource resource) {
        List<User> users = userService.getResourceUsers(resource);
        List<ResourceUserRolesRepresentation> resourceUserRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            resourceUserRolesRepresentations.add(getResourceUserRolesRepresentation(resource, user));
        }
        return resourceUserRolesRepresentations;
    }

    private ResourceUserRolesRepresentation getResourceUserRolesRepresentation(Resource resource, User user) {
        return new ResourceUserRolesRepresentation().withUser(integrationUserService.getUserRepresentationSimple(user)).withRoles(
                roleService.getRolesForResource(resource, user));
    }

}
