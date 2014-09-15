package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.google.common.base.Joiner;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

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
    private ResourceListFilterService resourceListFilterService;

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

    public ActionOutcomeDTO performAction(Integer resourceId, CommentDTO commentDTO) throws Exception {
        switch (commentDTO.getAction().getScope()) {
            case INSTITUTION:
                return institutionService.performAction(resourceId, commentDTO);
            case PROGRAM:
                return programService.performAction(resourceId, commentDTO);
            case PROJECT:
                return projectService.performAction(resourceId, commentDTO);
            case APPLICATION:
                return applicationService.performAction(resourceId, commentDTO);
            default:
                throw new Error("Couldn't perform action " + commentDTO.getAction());
        }
    }

    public ActionOutcomeDTO createResource(User user, Action action, Object newResourceDTO, String referrer) throws DeduplicationException {
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
            actionService.throwWorkflowEngineException(resource, action, "Attempted to create a resource of invalid type");
        }

        if (entityService.getDuplicateEntity(resource) != null && !user.isEnabled()) {
            actionService.throwWorkflowPermissionException(resource, action);
        }

        resource.setReferrer(referrer);
        Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .addAssignedUser(user, roleService.getCreatorRole(resource));

        return actionService.executeUserAction(resource, action, comment);
    }

    public void persistResource(Resource resource, Action action) throws WorkflowEngineException {
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
            actionService.throwWorkflowEngineException(resource, action, "Attempted to persist a resource of invalid type");
        }

        resource.setCode(generateResourceCode(resource));
        entityService.save(resource);
    }

    public String generateResourceCode(Resource resource) {
        return "PRiSM-" + PrismScope.getResourceScope(resource.getClass()).getShortCode() + "-" + String.format("%010d", resource.getId());
    }

    public void recordStateTransition(Resource resource, State state, State transitionState) {
        resource.setPreviousState(state);
        resource.setState(transitionState);
    }

    public void processResource(Resource resource, Comment comment) {
        LocalDate baselineCustom;
        LocalDate baseline = new LocalDate();

        switch (resource.getResourceScope()) {
        case PROGRAM:
            baselineCustom = programService.resolveDueDateBaseline((Program) resource, comment);
            break;
        case PROJECT:
            baselineCustom = projectService.resolveDueDateBaseline((Project) resource, comment);
            break;
        case APPLICATION:
            baselineCustom = applicationService.resolveDueDateBaseline((Application) resource, comment);
            break;
        default:
            baselineCustom = null;
            break;
        }

        baselineCustom = baselineCustom == null || baselineCustom.isBefore(baseline) ? baseline : baselineCustom;

        StateDuration stateDuration = stateService.getStateDuration(resource);
        resource.setDueDate(baseline.plusDays(stateDuration == null ? 0 : stateDuration.getDuration()));
    }

    public void updateResource(Resource resource, Comment comment) throws DeduplicationException {
        DateTime baselineTime = new DateTime();
        LocalDate baselineDate = baselineTime.toLocalDate();

        DateTime rangeStart = baselineDate.toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);

        String lastSequenceIdentifier = resourceDAO.getLastSequenceIdentifier(resource, rangeStart, rangeClose);

        lastSequenceIdentifier = lastSequenceIdentifier == null ? baselineDate.toString("yyyyMMdd") + "-0000000001" : lastSequenceIdentifier;
        String[] lastSequenceIdentifierParts = lastSequenceIdentifier.split("-");
        Integer lastSequenceIdentifierIndex = Integer.parseInt(lastSequenceIdentifierParts[1].replaceAll("^0+(?!$)", ""));

        Integer nextSequenceIdentifierIndex = lastSequenceIdentifierIndex + 1;
        resource.setSequenceIdentifier(lastSequenceIdentifierParts[0] + "-" + String.format("%010d", nextSequenceIdentifierIndex));

        resource.setUpdatedTimestamp(baselineTime);

        switch (resource.getResourceScope()) {
        case PROGRAM:
            programService.postProcessProgram((Program) resource, comment);
            break;
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

    public Resource getOperativeResource(Resource resource, Action action) {
        return action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE ? resource.getParentResource() : resource;
    }

    public <T extends Resource> List<Integer> getResourcesToEscalate(Class<T> resourceClass, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceClass, actionId, baseline);
    }

    public <T extends Resource> List<Integer> getResourcesToPropagate(PrismScope propagatingResourceScope, Integer propagatingResourceId,
            PrismScope propagatedResourceScope, PrismAction actionId) {
        return resourceDAO.getResourcesToPropagate(propagatingResourceScope, propagatingResourceId, propagatedResourceScope, actionId);
    }

    public <T extends Resource> List<Integer> getResourcesRequiringAttention(Class<T> resourceClass) {
        return resourceDAO.getResourcesRequiringAttention(resourceClass);
    }

    public <T extends Resource> List<Integer> getRecentlyUpdatedResources(Class<T> resourceClass, DateTime rangeStart, DateTime rangeClose) {
        return resourceDAO.getRecentlyUpdatedResources(resourceClass, rangeStart, rangeClose);
    }

    public <T extends Resource> List<ResourceConsoleListRowDTO> getResourceConsoleList(PrismScope scopeId, ResourceListFilterDTO filterDTO,
            String lastSequenceIdentifier) throws DeduplicationException {
        User user = userService.getCurrentUser();
        if (scopeId == PrismScope.SYSTEM) {
            throw new Error("The system resource does not support resource listing");
        }

        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(scopeId);
        filterDTO = resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filterDTO);

        boolean urgentOnly = filterDTO == null ? false : BooleanUtils.toBoolean(filterDTO.getUrgentOnly());
        List<Integer> assignedResources = getAssignedResources(user, scopeId, parentScopeIds, urgentOnly);

        return resourceDAO.getResourceConsoleList(user, scopeId, parentScopeIds, assignedResources, filterDTO, lastSequenceIdentifier);
    }

    private List<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, boolean urgentOnly) {
        List<Integer> assigned = resourceDAO.getAssignedResources(user, scopeId, urgentOnly);
        for (PrismScope parentScopeId : parentScopeIds) {
            assigned.addAll(resourceDAO.getAssignedResources(user, parentScopeId, scopeId, assigned, urgentOnly));
        }
        System.out.println("IDS: " + Joiner.on(", ").join(assigned));
        return assigned;
    }

}
