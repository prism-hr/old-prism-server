package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
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
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    public <T extends Resource> Resource getById(Class<T> resourceClass, Integer id) {
        return entityService.getById(resourceClass, id);
    }

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(Class<T> resourceClass, int loadIndex) {
        // TODO: Build filter and integrate
        return resourceDAO.getConsoleListBlock(userService.getCurrentUser(), resourceClass, scopeService.getParentScopes(resourceClass), loadIndex);
    }

    public <T extends Resource> List<ResourceReportListRowDTO> getReportList(Class<T> resourceType) {
        // TODO: Build the query and integrate with filter
        return Lists.newArrayList();
    }

    public ActionOutcomeDTO createResource(User user, Action action, Object newResourceDTO, String referer) throws WorkflowEngineException {
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
                throw new WorkflowEngineException("Attempted to create a resource of invalid type " + action.getCreationScope().getId().toString());
        }

        if (entityService.getDuplicateEntity(resource) != null && !user.isEnabled()) {
            actionService.throwWorkflowPermissionException(action, resource);
        }

        Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .withAssignedUser(user, roleService.getCreatorRole(resource));
        return actionService.executeUserAction(resource, action, comment);
    }

    public void persistResource(Resource resource) throws WorkflowEngineException {
        resource.setCreatedTimestamp(new DateTime());
        resource.setUpdatedTimestamp(new DateTime());

        switch (resource.getResourceScope()) {
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
                throw new WorkflowEngineException("Attempted to persist a resource of invalid type " + resource.getResourceScope().getLowerCaseName());
        }

        resource.setCode(generateResourceCode(resource));
        entityService.save(resource);
    }

    public String generateResourceCode(Resource resource) {
        return "PRiSM-" + PrismScope.getResourceScope(resource.getClass()).getShortCode() + "-" + String.format("%010d", resource.getId());
    }

    public void processResource(Resource resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        
        resource.setPreviousState(resource.getState());
        resource.setState(transitionState);

        LocalDate baselineCustom;
        LocalDate baseline = new LocalDate();
        
        switch (resource.getResourceScope()) {
        case PROGRAM:
            baselineCustom = programService.resolveDueDateBaseline((Program) resource, comment);
        case PROJECT:
            baselineCustom = projectService.resolveDueDateBaseline((Project) resource, comment);
        case APPLICATION:
            baselineCustom = applicationService.resolveDueDateBaseline((Application) resource, comment);
        default:
            baselineCustom = null;
        }
        
        baselineCustom = baselineCustom == null || baselineCustom.isBefore(baseline) ? baseline : baselineCustom;
        
        StateDuration stateDuration = stateService.getStateDuration(resource, transitionState);
        resource.setDueDate(baseline.plusDays(stateDuration == null ? 0 : stateDuration.getDuration()));
    }
    
    public void updateResource(Resource resource, Comment comment) {     
        DateTime baselineTime = new DateTime();
        LocalDate baselineDate = baselineTime.toLocalDate();
        
        String lastSequenceIdentifier = resourceDAO.getLastSequenceIdentifier(resource, baselineDate);
        
        lastSequenceIdentifier = lastSequenceIdentifier == null ? baselineDate.toString("yyyyMMdd") + "-0000000001" : lastSequenceIdentifier;
        String[] lastSequenceIdentifierParts = lastSequenceIdentifier.split("-");
        Integer lastSequenceIdentifierIndex = Integer.parseInt(lastSequenceIdentifierParts[1].replaceAll("^0+(?!$)", ""));
        
        Integer nextSequenceIdentifierIndex = lastSequenceIdentifierIndex + 1;
        resource.setSequenceIdentifier(lastSequenceIdentifierParts[0] + "-" + String.format("%010d", nextSequenceIdentifierIndex));
        
        resource.setUpdatedTimestamp(baselineTime);
        
        switch (resource.getResourceScope()) {
        case PROGRAM:
            programService.postProcessProgram((Program) resource, comment);
        case PROJECT:
            projectService.postProcessProject((Project) resource, comment);
            break;
        case APPLICATION:
            applicationService.postProcessApplication((Application) resource, comment);
            break;
        default:
            break;
        }
    }

    public HashMap<Resource, Action> getResourceEscalations() {
        LocalDate baseline = new LocalDate();
        HashMap<Resource, Action> escalations = Maps.newHashMap();

        for (Action action : actionService.getEscalationActions()) {
            List<Resource> resources = resourceDAO.getResourcesToEscalate(action, baseline);

            for (Resource resource : resources) {
                escalations.put(resource, action);
            }
        }
        return escalations;
    }

    public HashMap<Resource, Action> getResourcePropagations() {
        HashMap<Resource, Action> propagations = Maps.newHashMap();
        List<StateTransitionPending> stateTransitionsPending = stateService.getStateTransitionsPending();

        for (StateTransitionPending stateTransitionPending : stateTransitionsPending) {

            for (Action action : stateTransitionPending.getStateTransition().getPropagatedActions()) {
                List<Resource> resources = resourceDAO.getResourcesToPropagate(stateTransitionPending.getResource(), action);

                for (Resource resource : resources) {
                    if (!propagations.containsKey(resource)) {
                        propagations.put(resource, action);
                    }
                }

                if (resources.isEmpty()) {
                    entityService.delete(stateTransitionPending);
                }
            }
        }
        return propagations;
    }
    
    public Resource getOperativeResource(Resource resource, Action action) {
        return action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE ? resource.getParentResource() : resource;
    }
    
}
