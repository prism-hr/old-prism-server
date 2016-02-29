package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

import com.google.common.collect.HashMultimap;

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

    @SuppressWarnings("unchecked")
    public List<ResourceUserRolesRepresentation> getResourceUserRoleRepresentations(Resource resource, PrismRole searchRole, String searchTerm) {
        resourceService.validateViewResource(resource);
        User currentUser = userService.getCurrentUser();

        Set<ResourceUserRolesRepresentation> representations = newTreeSet();
        HashMultimap<User, PrismRole> userRoles = roleService.getUserRolesStrict(resource, searchRole, searchTerm);
        userRoles.keySet().stream().forEach(user -> {
            representations.add(new ResourceUserRolesRepresentation()
                    .withUser(userMapper.getUserRepresentationSimpleWithEmail(user, currentUser))
                    .withRoles(userRoles.get(user)) //
                    .withOwner(user.equals(resource.getUser()))
                    .withPending(false));
        });

        boolean filterRole = searchRole != null;
        boolean filterTerm = isNotBlank(searchTerm);
        resource.getStateActionPendings().stream().forEach(stateActionPending -> {
            PrismRole assignRole = stateActionPending.getAssignUserRole().getId();
            if (!filterRole || assignRole.equals(searchRole)) {
                List<UserRepresentationSimple> userRepresentations = prismJsonMappingUtils.readCollection(stateActionPending.getAssignUserList(),
                        List.class, UserRepresentationSimple.class);
                userRepresentations.stream().forEach(userRepresentation -> {
                    if (!filterTerm || (userRepresentation.getFirstName().contains(searchTerm)
                            || userRepresentation.getLastName().contains(searchTerm)
                            || userRepresentation.getFullName().contains(searchTerm))
                            || userRepresentation.getEmail().contains(searchTerm)) {
                        representations.add(new ResourceUserRolesRepresentation()
                                .withUser(userRepresentation)
                                .withRoles(newHashSet(assignRole))
                                .withMessage(stateActionPending.getAssignUserMessage())
                                .withPending(true));
                    }
                });
            }
        });

        return newLinkedList(representations);
    }
}
