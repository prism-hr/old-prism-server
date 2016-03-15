package uk.co.alumeni.prism.rest.controller;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.ResourceChildCreationDTO;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.ActionMapper;
import uk.co.alumeni.prism.mapping.CommentMapper;
import uk.co.alumeni.prism.mapping.MessageMapper;
import uk.co.alumeni.prism.mapping.ResourceMapper;
import uk.co.alumeni.prism.mapping.RoleMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.PrismRestUtils;
import uk.co.alumeni.prism.rest.ResourceDescriptor;
import uk.co.alumeni.prism.rest.dto.MessageDTO;
import uk.co.alumeni.prism.rest.dto.ReplicableActionSequenceDTO;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.UserListFilterDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceReportFilterDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.representation.action.ActionOutcomeRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentTimelineRepresentation;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceListRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationCreation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceUserRolesRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationInvitationBounced;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.MessageService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.datatable.DataTable;

@RestController
@RequestMapping("api/{resourceScope:applications|projects|programs|departments|institutions|systems}")
public class ResourceController {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private MessageService messageService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private MessageMapper messageMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private RoleMapper roleMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ObjectMapper objectMapper;

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    public ResourceRepresentationExtended getResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationClient(resource, userService.getCurrentUser());
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=summary")
    public ResourceRepresentationSummary getResourceSummary(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationSummary(resource);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}/timeline", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public CommentTimelineRepresentation getResourceTimeline(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return commentMapper.getCommentTimelineRepresentation(resource);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}/users", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<ResourceUserRolesRepresentation> getResourceUsers(
            @PathVariable Integer resourceId, @RequestParam(required = false) PrismRole qRole,
            @RequestParam(required = false) String qTerm, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return roleMapper.getResourceUserRoleRepresentations(resource, qRole, qTerm);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=simple")
    @PreAuthorize("permitAll")
    public ResourceRepresentationSimple getResourceSimple(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationSimple(resource);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=activity")
    @PreAuthorize("permitAll")
    public ResourceRepresentationRelation getResourceRelation(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationRelation(resource, userService.getCurrentUser());
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=location")
    @PreAuthorize("permitAll")
    public ResourceRepresentationLocation getResourceLocation(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationLocation(resource);
    }

    @RequestMapping(value = "/{resourceId}/displayProperties", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor, @RequestParam PrismScope propertiesScope) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceService.getDisplayProperties(resource, propertiesScope);
    }

    @RequestMapping(value = "/{resourceId}/children", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public List<ResourceRepresentationCreation> getResources(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam PrismScope childResourceScope, @RequestParam Optional<String> q) {
        User user = userService.getCurrentUser();
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceService.getResources(resource, childResourceScope, q).stream()
                .map(rr -> resourceMapper.getResourceRepresentationLocation(rr, user)).collect(toList());
    }

    @RequestMapping(value = "/{resourceId}/acceptingResources", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public List<ResourceRepresentationIdentity> getResourcesForWhichUserCanCreateResource(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam PrismScope responseScope, @RequestParam PrismScope creationScope, @RequestParam Optional<String> q) {
        Resource enclosingResource = loadResource(resourceId, resourceDescriptor);
        List<ResourceChildCreationDTO> resources = resourceService.getResourcesForWhichUserCanCreateResource(enclosingResource, responseScope, creationScope,
                q.orElse(null));
        return resources.stream().map(resourceMapper::getResourceRepresentationChildCreation).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResourceListRepresentation getResources(@ModelAttribute ResourceDescriptor resourceDescriptor, @RequestParam(required = false) String filter,
            @RequestParam(required = false) String lastSequenceIdentifier) throws Exception {
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        ResourceListRepresentation representation = resourceMapper.getResourceListRepresentation(resourceScope, filterDTO, lastSequenceIdentifier);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET, params = "type=report")
    @PreAuthorize("isAuthenticated()")
    public void getReport(@ModelAttribute ResourceDescriptor resourceDescriptor, @RequestParam(required = false) String filter, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (resourceDescriptor.getResourceScope() != PrismScope.APPLICATION) {
            throw new UnsupportedOperationException("Report can only be generated for applications");
        }
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        DataTable reportTable = applicationService.getApplicationReport(filterDTO);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(request);
        DataSourceHelper.setServletResponse(reportTable, dataSourceRequest, response);
        String fileName = response.getHeader("Content-Disposition").replace("attachment; filename=", "");
        response.setHeader("file-name", fileName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceId}/plot")
    @PreAuthorize("isAuthenticated()")
    public ResourceSummaryPlotRepresentation getPlot(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @RequestParam(required = false) String filter) throws Exception {
        Resource resource = resourceService.getById(resourceDescriptor.getResourceScope(), resourceId);
        if (!(resource instanceof ResourceParent)) {
            throw new IllegalArgumentException("Unexpected resource scope: " + resourceDescriptor.getResourceScope());
        }
        ResourceReportFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceReportFilterDTO.class) : null;
        return resourceMapper.getResourceSummaryPlotRepresentation((ResourceParent) resource, filterDTO);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void addUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceUserRolesRepresentation body) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        Set<PrismRole> roles = body.getRoles();
        roleService.createUserRoles(userService.getCurrentUser(), resource, user, body.getMessage(), roles.toArray(new PrismRole[roles.size()]));
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role,
            @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(userService.getCurrentUser(), resource, user, role);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public UserRepresentationSimple addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceUserRolesRepresentation body) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentationSimple newUser = body.getUser();
        User user = userService.getOrCreateUserWithRoles(userService.getCurrentUser(), newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(),
                resource, body.getMessage(), body.getRoles());
        return userMapper.getUserRepresentationSimple(user, userService.getCurrentUser());
    }

    @RequestMapping(value = "{resourceId}/users/batch", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void addUsers(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody StateActionPendingDTO body) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        userService.getOrCreateUsersWithRoles(resource, body);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void deleteUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(userService.getCurrentUser(), resource, user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/setAsOwner", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void setUserAsOwner(@PathVariable Integer resourceId, @PathVariable Integer userId, @RequestBody Map<?, ?> undertow,
            @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.setResourceOwner(resource, user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/{decision:accept|reject}", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void verifyUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable String decision,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody Map<?, ?> undertow) {
        boolean accept = decision.equals("accept");
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.verifyUserRoles(userService.getCurrentUser(), (ResourceParent) resource, user, accept);
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ActionOutcomeRepresentation executeAction(@ModelAttribute ResourceDescriptor resourceDescriptor, @Valid @RequestBody CommentDTO commentDTO) {
        ActionOutcomeDTO actionOutcome = resourceService.executeAction(userService.getCurrentUser(), commentDTO);
        return actionOutcome == null ? null : actionMapper.getActionOutcomeRepresentation(actionOutcome);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void executeBulkAction(@Valid @RequestBody ReplicableActionSequenceDTO replicableActionSequenceDTO) {
        resourceService.executeBulkAction(replicableActionSequenceDTO);
    }

    @RequestMapping(value = "/{resourceId}/bouncedUsers", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<UserRepresentationInvitationBounced> getBouncedOrUnverifiedUsers(
            @PathVariable Integer resourceId, UserListFilterDTO filterDTO, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return userMapper.getUserUnverifiedRepresentations(resource, filterDTO);
    }

    @RequestMapping(value = "/{resourceId}/bouncedUsers/{userId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public void reassignBouncedOrUnverifiedUser(
            @PathVariable Integer resourceId, @PathVariable Integer userId,
            @Valid @RequestBody UserDTO userDTO, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        userService.reassignBouncedOrUnverifiedUser(resource, userId, userDTO);
    }

    @RequestMapping(value = "{resourceId}/threads", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<MessageThreadRepresentation> getThreads(
            @PathVariable Integer resourceId, @RequestParam(required = false) String q,
            @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return messageMapper.getMessageThreadRepresentations(resource, q);
    }

    @RequestMapping(value = "{resourceId}/threads", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void createThread(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @Valid @RequestBody MessageDTO messageDTO) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        messageService.postMessage(resource, null, messageDTO);
    }

    @RequestMapping(value = "{resourceId}/threads/{threadId}/messages", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void postMessage(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @PathVariable Integer threadId, @Valid @RequestBody MessageDTO messageDTO) {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        messageService.postMessage(resource, threadId, messageDTO);
    }

    @RequestMapping(value = "{resourceId}/threads/{threadId}/view", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void viewMessage(@RequestBody Map<String, Integer> body) {
        messageService.viewMessage(body.get("recipientId"));
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return PrismRestUtils.getResourceDescriptor(resourceScope);
    }

    private Resource loadResource(Integer resourceId, ResourceDescriptor resourceDescriptor) {
        Resource resource = resourceService.getById(resourceDescriptor.getType(), resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        return resource;
    }

}
