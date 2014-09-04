package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceReportListRowDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
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
                .withAssignedUser(user, roleService.getCreatorRole(resource));

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
        return resourceDAO.getResourcesToPropagate(propagatingResourceScope, propagatingResourceId, propagatingResourceScope, actionId);
    }

    public <T extends Resource> List<Integer> getResourcesRequiringAttention(Class<T> resourceClass) {
        return resourceDAO.getResourcesRequiringAttention(resourceClass);
    }

    public <T extends Resource> List<Integer> getRecentlyUpdatedResources(Class<T> resourceClass, DateTime rangeStart, DateTime rangeClose) {
        return resourceDAO.getRecentlyUpdatedResources(resourceClass, rangeStart, rangeClose);
    }

    public <T extends Resource> DetachedCriteria getResourceListFilter(User user, Class<T> resourceClass, List<PrismState> stateWithUrgentActionIds,
            ResourceListFilterDTO filterDTO, String lastSequenceIdentifier) {
        List<PrismScope> parentScopeIds = scopeService.getParentScopes(resourceClass);
        return resourceDAO.getResourceListFilter(user, resourceClass, parentScopeIds, filterDTO, lastSequenceIdentifier);
    }

}
