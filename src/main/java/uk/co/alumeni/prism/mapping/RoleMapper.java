package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.resource.ResourceUserRolesRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;

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

    public List<ResourceUserRolesRepresentation> getResourceUserRoleRepresentations(Resource resource, User currentUser) {
        resourceService.validateViewResource(resource);

        Set<ResourceUserRolesRepresentation> representations = newLinkedHashSet();
        userService.getResourceUsers(resource).forEach(user -> representations.add(getResourceUserRolesRepresentation(resource, user, currentUser)));

        resource.getStateActionPendings().stream().forEach(stateActionPending -> { //
                    PrismRole assignRole = stateActionPending.getAssignUserRole().getId();
                    @SuppressWarnings("unchecked")
                    List<UserRepresentationSimple> userRepresentations = prismJsonMappingUtils.readCollection(stateActionPending.getAssignUserList(),
                            List.class, UserRepresentationSimple.class);
                    userRepresentations.stream().forEach(userRepresentation -> {
                        representations.add(new ResourceUserRolesRepresentation()
                                .withUser(userRepresentation)
                                .withRoles(singletonList(assignRole))
                                .withMessage(stateActionPending.getAssignUserMessage())
                                .withPending(true));
                    });
                });

        return newLinkedList(representations);
    }

    private ResourceUserRolesRepresentation getResourceUserRolesRepresentation(Resource resource, User user, User currentUser) {
        return new ResourceUserRolesRepresentation().withUser(userMapper.getUserRepresentationSimpleWithEmail(user, currentUser))
                .withRoles(roleService.getRolesForResourceStrict(resource, user));
    }

}
