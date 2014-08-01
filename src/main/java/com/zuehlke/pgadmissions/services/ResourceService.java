package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceReportListRowDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(Class<T> resourceType, int page, int perPage) {
        // TODO: Build filter and integrate
        return resourceDAO.getConsoleListBlock(userService.getCurrentUser(), resourceType, page, perPage);
    }

    public <T extends Resource> List<ResourceReportListRowDTO> getReportList(Class<T> resourceType) {
        // TODO: Build the query and integrate with filter
        return Lists.newArrayList();
    }

    public void setTransitionState(Resource resource, State transitionState) {
        resource.setPreviousState(resource.getState());
        resource.setState(transitionState);
    }

    public void setDueDate(Resource resource, Comment comment, StateDuration stateDuration) {
        LocalDate dueDate = comment.getUserSpecifiedDueDate();
        if (dueDate == null && comment.getAction().getActionCategory() == PrismActionCategory.ESCALATE_RESOURCE) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            dueDate = dueDateBaseline.plusDays(stateDuration == null ? 0 : stateDuration.getDuration());
        }
        resource.setDueDate(dueDate);
    }

    public Resource getOperativeResource(Resource resource, Action action) {
        return action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE ? resource.getParentResource() : resource;
    }

    public void createResource(Resource resource, Action action, Comment comment) {
        persistResource(resource);
        comment.setRole(roleService.getCreatorRole(resource).toString());
    }
    
    public void updateResource(Resource resource, Action action, Comment comment) {
        if (action.getActionType() == PrismActionType.SYSTEM_INVOCATION) {
            comment.setRole(PrismRole.SYSTEM_ADMINISTRATOR.toString());
        } else {
            comment.setRole(Joiner.on(", ").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action)));
            if (comment.getDelegateUser() != null) {
                comment.setDelegateRole(Joiner.on(", ").join(roleService.getDelegateActionOwnerRoles(comment.getDelegateUser(), resource, action)));
            }
        }
    }
    
    public void transitionResource(Resource resource, Comment comment, State transitionState, StateDuration transitionStateDuration) {
        setTransitionState(resource, transitionState);
        comment.setTransitionState(transitionState);
        setDueDate(resource, comment, transitionStateDuration);
        resource.setUpdatedTimestamp(new DateTime());
    }

    public Resource create(User user, Action action, Object newResourceDTO) throws WorkflowEngineException {
        Resource resource = null;

        switch (action.getCreationScope().getId()) {
        case INSTITUTION:
            resource = institutionService.create(user, (InstitutionDTO) newResourceDTO);
            break;
        case PROGRAM:
            resource = programService.create(user, (ProgramDTO) newResourceDTO);
            break;
        case PROJECT:
            resource = projectService.create(user, (ProjectDTO) newResourceDTO);
            break;
        case APPLICATION:
            resource = applicationService.create(user, (ApplicationDTO) newResourceDTO);
            break;
        default:
            throw new WorkflowEngineException();
        }

        if (entityService.getDuplicateEntity(resource) != null) {
            throw new WorkflowEngineException();
        }
        
        Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .withAssignedUser(user, roleService.getCreatorRole(resource));
        return actionService.executeUserAction(resource, action, comment).getTransitionResource();
    }

    private void persistResource(Resource resource) {
        resource.setCreatedTimestamp(new DateTime());
        resource.setUpdatedTimestamp(new DateTime());
        
        switch (resource.getResourceScope()) {
        case SYSTEM:
            systemService.save((System) resource);
            break;
        case INSTITUTION:
            institutionService.save((Institution) resource);
            break;
        case PROGRAM:
            programService.save((Program) resource);
            break;
        case PROJECT:
            projectService.save((Project) resource);
            break;
        case APPLICATION:
            applicationService.save((Application) resource);
            break;
        default:
            break;
        }
        
        resource.setCode("PRiSM-" + PrismScope.getResourceScope(resource.getClass()).getShortCode() + "-" + String.format("%010d", resource.getId()));
        entityService.save(resource);
    }

}
