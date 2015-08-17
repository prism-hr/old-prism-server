package com.zuehlke.pgadmissions.rest.controller;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mapping.ActionMapper;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.mapping.UserMapper;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestUtils;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationUnverified;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("api/{resourceScope:applications|projects|programs|departments|institutions|systems}")
public class ResourceController {

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ObjectMapper objectMapper;

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResourceRepresentationExtended getResource(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationClient(resource);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=simple")
    @PreAuthorize("isAuthenticated()")
    public ResourceRepresentationSimple getResourceSimple(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationSimple(resource);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "type=location")
    @PreAuthorize("isAuthenticated()")
    public ResourceRepresentationLocation getResourceLocation(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationLocation(resource);
    }


    @RequestMapping(value = "/{resourceId}/displayProperties", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(
            @PathVariable Integer resourceId,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam PrismScope propertiesScope) throws Exception {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        return resourceService.getDisplayProperties(resource, propertiesScope);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                                            @RequestParam(required = false) String filter, @RequestParam(required = false) String lastSequenceIdentifier) throws Exception {
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;

        return resourceMapper.getResourceListRowRepresentations(resourceScope,
                resourceService.getResourceList(resourceScope, filterDTO, lastSequenceIdentifier));
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
    public ResourceSummaryPlotRepresentation getPlot(
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
            @RequestParam(required = false) String filter) throws Exception {
        Resource<?> resource = resourceService.getById(resourceDescriptor.getResourceScope(), resourceId);
        if (!(resource instanceof ResourceParent)) {
            throw new IllegalArgumentException("Unexpected resource scope: " + resourceDescriptor.getResourceScope());
        }
        ResourceReportFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceReportFilterDTO.class) : null;
        return resourceMapper.getResourceSummaryPlotRepresentation((ResourceParent<?>) resource, filterDTO);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void addUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                            @RequestBody Map<String, PrismRole> body) throws Exception {
        PrismRole role = body.get("role");
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, CREATE, role);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role,
                               @ModelAttribute ResourceDescriptor resourceDescriptor) throws Exception {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, DELETE, role);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public UserRepresentationSimple addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                            @RequestBody ResourceUserRolesRepresentation userRolesRepresentation) throws Exception {
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentationSimple newUser = userRolesRepresentation.getUser();

        User user = userService.getOrCreateUserWithRoles(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), resource,
                userRolesRepresentation.getRoles());
        return userMapper.getUserRepresentationSimple(user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void removeUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(resource, user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/setAsOwner", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void setAsOwner(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.setResourceOwner(resource, user);
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ActionOutcomeRepresentation executeAction(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                                     @Valid @RequestBody CommentDTO commentDTO) throws Exception {
        ActionOutcomeDTO actionOutcome = resourceService.executeAction(userService.getCurrentUser(), resourceId, commentDTO);
        return actionMapper.getActionOutcomeRepresentation(actionOutcome);
    }

    @RequestMapping(value = "/{resourceId}/availableThemes", method = RequestMethod.GET)
    public Set<String> getAvailableThemes(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        return advertService.getAvailableAdvertThemes(resource.getAdvert(), null);
    }

    // FIXME - pass 'stop scope' for rendering from client so that we can clean up server code
    // FIXME - problem with rendering on client? data is being generated but not display
    @RequestMapping(value = "/{resourceId}/acceptingResources", method = RequestMethod.GET)
    public List<ResourceChildCreationRepresentation> getAcceptingResources(@PathVariable Integer resourceId, @RequestParam PrismScope targetScope,
                                                                           @RequestParam PrismScope stopScope, @RequestParam Optional<String> searchTerm, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        return resourceMapper.getResourceChildCreationRepresentations(resourceDescriptor.getResourceScope(), resourceId, targetScope, stopScope, searchTerm.orElse(null));
    }

    @RequestMapping(value = "/{resourceId}/bouncedUsers", method = RequestMethod.GET)
    public List<UserRepresentationUnverified> getBouncedOrUnverifiedUsers(
            @PathVariable Integer resourceId, UserListFilterDTO filterDTO, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        return userMapper.getUserUnverifiedRepresentations(resource, filterDTO);
    }

    @RequestMapping(value = "/{resourceId}/bouncedUsers/{userId}", method = RequestMethod.PUT)
    public void correctBouncedOrUnverifiedUser(@PathVariable Integer resourceId, @PathVariable Integer userId,
                                               @Valid @RequestBody UserCorrectionDTO userCorrectionDTO, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = loadResource(resourceId, resourceDescriptor);
        userService.correctBouncedOrUnverifiedUser(resource, userId, userCorrectionDTO);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestUtils.getResourceDescriptor(resourceScope);
    }

    private Resource<?> loadResource(Integer resourceId, ResourceDescriptor resourceDescriptor) {
        Resource<?> resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource<?> not found");
        }
        return resource;
    }

}
