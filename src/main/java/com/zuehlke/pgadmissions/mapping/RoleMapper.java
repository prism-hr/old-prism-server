package com.zuehlke.pgadmissions.mapping;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.PrismJsonMappingUtils;

@Service
@Transactional
public class RoleMapper {

    @Inject
    private UserMapper userMapper;

    @Inject
    private ResourceService resourceService;
    
    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PrismJsonMappingUtils prismJsonMappingUtils;

    public List<ResourceUserRolesRepresentation> getResourceUserRoleRepresentations(Resource resource) {
        resourceService.validateViewResource(resource);

        HashSet<String> emailSet = new HashSet<>();
        List<User> users = userService.getResourceUsers(resource);

        Stream<ResourceUserRolesRepresentation> activeUserRepresentations = users.stream()
                .filter(user -> emailSet.add(user.getEmail()))
                .map(user -> getResourceUserRolesRepresentation(resource, user));

        Stream<ResourceUserRolesRepresentation> pendingUserRepresentations = resource.getStateActionPendings().stream()
                .flatMap(sa -> {
                    @SuppressWarnings("unchecked")
                    List<UserRepresentationSimple> userRepresentations = prismJsonMappingUtils.readCollection(sa.getAssignUserList(), List.class, UserRepresentationSimple.class);
                    return userRepresentations.stream()
                            .map(userRepresentation -> new ResourceUserRolesRepresentation()
                                    .withUser(userRepresentation)
                                    .withRoles(Collections.singletonList(sa.getAssignUserRole().getId()))
                                    .withMessage(sa.getAssignUserMessage())
                                    .withPending(true));
                })
                .filter(user -> emailSet.add(user.getUser().getEmail()));

        return Stream.concat(activeUserRepresentations, pendingUserRepresentations).collect(Collectors.toList());
    }

    private ResourceUserRolesRepresentation getResourceUserRolesRepresentation(Resource resource, User user) {
        return new ResourceUserRolesRepresentation().withUser(userMapper.getUserRepresentationSimple(user)).withRoles(
                roleService.getRolesForResourceStrict(resource, user));
    }

}
