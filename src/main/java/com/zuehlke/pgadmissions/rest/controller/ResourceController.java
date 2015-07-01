package com.zuehlke.pgadmissions.rest.controller;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;
import java.util.Map;

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
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mappers.ActionMapper;
import com.zuehlke.pgadmissions.mappers.ApplicationMapper;
import com.zuehlke.pgadmissions.mappers.ResourceMapper;
import com.zuehlke.pgadmissions.mappers.UserMapper;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestUtils;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("api/{resourceScope:applications|projects|programs|institutions|systems}")
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
    private ApplicationMapper applicationMapper;

    @Inject
    private ApplicationService applicationService;

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
    public <T extends ResourceRepresentationExtended> ResourceRepresentationExtended getResource(
            @PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor) throws Exception {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceMapper.getResourceRepresentationClient(resource);
    }

    @RequestMapping(value = "/{resourceId}/displayProperties", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(
            @PathVariable Integer resourceId,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam PrismScope propertiesScope) throws Exception {
        Resource resource = loadResource(resourceId, resourceDescriptor);
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

    @RequestMapping(method = RequestMethod.GET, value = "{resourceId}", params = "type=summary")
    @PreAuthorize("isAuthenticated()")
    public Object getSummary(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId) {
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        if (resourceScope == SYSTEM) {
            throw new UnsupportedOperationException("Summary cannot be created for system");
        } else {
            Resource resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
            if (resourceScope == APPLICATION) {
                return applicationMapper.getApplicationSummary((Application) resource);
            }
            return resourceMapper.getResourceSummaryRepresentation((ResourceParent) resource);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceId}/plot")
    @PreAuthorize("isAuthenticated()")
    public ResourceSummaryPlotRepresentation getPlot(
            @ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId,
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
            @RequestBody Map<String, PrismRole> body) throws Exception {
        PrismRole role = body.get("role");
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, CREATE, role);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role,
            @ModelAttribute ResourceDescriptor resourceDescriptor) throws Exception {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, DELETE, role);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public UserRepresentationSimple addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceUserRolesRepresentation userRolesRepresentation) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentationSimple newUser = userRolesRepresentation.getUser();

        User user = userService.getOrCreateUserWithRoles(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), resource,
                userRolesRepresentation.getRoles());
        return userMapper.getUserRepresentationSimple(user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void removeUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(resource, user);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/setAsOwner", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void setAsOwner(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.setResourceOwner(resource, user);
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ActionOutcomeRepresentation executeAction(
            @PathVariable Integer resourceId,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @Valid @RequestBody CommentDTO commentDTO) throws Exception {
        if (commentDTO.getAction().getActionCategory().equals(PrismActionCategory.CREATE_RESOURCE)) {
            ResourceDTO newResource = commentDTO.getNewResource().getResource();
            if (newResource == null) {
                throw new Error("Cannot create new resource for " + resourceDescriptor.getResourceScope() + '#' + resourceId);
            }
            newResource.setResourceId(resourceId);
            newResource.setResourceScope(resourceDescriptor.getResourceScope());
        }

        ActionOutcomeDTO actionOutcome = resourceService.executeAction(userService.getCurrentUser(), resourceId, commentDTO);
        return actionMapper.getActionOutcomeRepresentation(actionOutcome);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestUtils.getResourceDescriptor(resourceScope);
    }

    private Resource loadResource(Integer resourceId, ResourceDescriptor resourceDescriptor) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        return resource;
    }

}
