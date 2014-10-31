package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateDuration;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.services.builders.ResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

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

    @Autowired
    private ApplicationContext applicationContext;

    public <T extends Resource> Resource getById(Class<T> resourceClass, Integer id) {
        return entityService.getById(resourceClass, id);
    }

    public Integer getResourceId(Resource resource) {
        return resource == null ? null : resource.getId();
    }

    public ActionOutcomeDTO executeAction(Integer resourceId, CommentDTO commentDTO) throws DeduplicationException {
        PrismAction actionId = commentDTO.getAction();
        PrismScope resourceScope = actionId.getScope();
        switch (resourceScope) {
        case APPLICATION:
            return applicationService.executeAction(resourceId, commentDTO);
        default:
            User user = userService.getById(commentDTO.getUser());
            Resource resource = getById(resourceScope.getResourceClass(), resourceId);
            Action action = actionService.getById(actionId);

            String commentContent = actionId.name().endsWith("VIEW_EDIT") ? applicationContext.getBean(PropertyLoader.class).localize(resource, user)
                    .load(PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED")) : commentDTO.getContent();

            State transitionState = stateService.getById(commentDTO.getTransitionState());
            Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                    .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

            Object resourceDTO = commentDTO.fetchResouceDTO();
            if (resourceDTO != null) {
                updateResource(resourceScope, resourceId, resourceDTO);
            }

            return actionService.executeUserAction(resource, action, comment);
        }
    }

    public ActionOutcomeDTO createResource(User user, Action action, Object newResourceDTO, String referrer) throws DeduplicationException {
        Resource resource = null;
        PrismScope resourceScope = action.getCreationScope().getId();

        switch (resourceScope) {
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
        user.setLatestCreationScope(scopeService.getById(resourceScope));
        Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false)
                .addAssignedUser(user, roleService.getCreatorRole(resource), PrismRoleTransitionType.CREATE);

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

    public void postProcessResource(Resource resource, Comment comment) throws DeduplicationException {
        DateTime baselineTime = new DateTime();
        resource.setUpdatedTimestamp(baselineTime);
        resource.setSequenceIdentifier(Objects.toString(baselineTime.getMillis()) + String.format("%010d", resource.getId()));

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

    public void executeUpdate(Resource resource, PrismDisplayProperty messageIndex) throws DeduplicationException {
        executeUpdate(resource, messageIndex, null);
    }

    public void executeUpdate(Resource resource, PrismDisplayProperty messageIndex, CommentAssignedUser assignee) throws DeduplicationException {
        User user = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);

        Comment comment = new Comment().withUser(user).withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localize(resource, user).load(messageIndex)).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());

        if (assignee != null) {
            comment.addAssignedUser(assignee.getUser(), assignee.getRole(), assignee.getRoleTransitionType());
            entityService.evict(assignee);
        }

        actionService.executeUserAction(resource, action, comment);
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

    public <T extends Resource> List<Integer> getResourcesRequiringIndividualReminders(Class<T> resourceClass, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringIndividualReminders(resourceClass, baseline);
    }

    public <T extends Resource> List<Integer> getResourcesRequiringSyndicatedReminders(Class<T> resourceClass, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringSyndicatedReminders(resourceClass, baseline);
    }

    public <T extends Resource> List<Integer> getResourcesRequiringSyndicatedUpdates(Class<T> resourceClass, LocalDate baseline, DateTime rangeStart,
            DateTime rangeClose) {
        return resourceDAO.getResourceRequiringSyndicatedUpdates(resourceClass, baseline, rangeStart, rangeClose);
    }

    public <T extends Resource> List<ResourceConsoleListRowDTO> getResourceConsoleList(PrismScope scopeId, ResourceListFilterDTO filter,
            String lastSequenceIdentifier) throws DeduplicationException {
        User user = userService.getCurrentUser();
        if (scopeId == PrismScope.SYSTEM) {
            throw new Error();
        }

        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(scopeId);
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filter);

        Integer maxRecords = scopeId.getMaxConsoleListRecords();
        Set<Integer> assignedResources = getAssignedResources(user, scopeId, parentScopeIds, filter, lastSequenceIdentifier, maxRecords);
        return resourceDAO.getResourceConsoleList(user, scopeId, parentScopeIds, assignedResources, filter, lastSequenceIdentifier, maxRecords);
    }

    public void filterResourceListData(ResourceListRowRepresentation representation, User currentUser) {
        switch (representation.getResourceScope()) {
        case APPLICATION:
            applicationService.filterResourceListData(representation, currentUser);
            break;
        case INSTITUTION:
        case PROGRAM:
        case PROJECT:
        case SYSTEM:
            break;
        }
    }

    private Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, ResourceListFilterDTO filter,
            String lastSequenceIdentifier, Integer maxRecords) {
        Set<Integer> assigned = Sets.newHashSet();
        Junction conditions = getFilterConditions(scopeId, filter);
        assigned.addAll(resourceDAO.getAssignedResources(user, scopeId, filter, conditions, lastSequenceIdentifier, maxRecords));
        for (PrismScope parentScopeId : parentScopeIds) {
            assigned.addAll(resourceDAO.getAssignedResources(user, scopeId, parentScopeId, filter, conditions, lastSequenceIdentifier, maxRecords));
        }
        return assigned;
    }

    private Junction getFilterConditions(PrismScope scopeId, ResourceListFilterDTO filter) {
        if (filter.hasConstraints()) {
            Junction conditions = Restrictions.conjunction();
            if (filter.getMatchMode() == FilterMatchMode.ANY) {
                conditions = Restrictions.disjunction();
            }

            for (ResourceListFilterConstraintDTO constraint : filter.getConstraints()) {
                FilterProperty property = constraint.getFilterProperty();

                if (FilterProperty.isPermittedFilterProperty(scopeId, property)) {
                    String propertyName = property.getPropertyName();
                    Boolean negated = BooleanUtils.toBoolean(constraint.getNegated());
                    switch (property) {
                    case CLOSING_DATE:
                        ResourceListConstraintBuilder.appendClosingDateFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
                                constraint.getValueDateStart(), constraint.getValueDateClose(), negated);
                        break;
                    case CODE:
                    case REFERRER:
                        ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
                        break;
                    case CONFIRMED_START_DATE:
                    case DUE_DATE:
                        ResourceListConstraintBuilder.appendDateFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
                                constraint.getValueDateStart(), constraint.getValueDateClose(), negated);
                        break;
                    case CREATED_TIMESTAMP:
                    case UPDATED_TIMESTAMP:
                        ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
                                constraint.computeValueDateTimeStart(), constraint.computeValueDateTimeClose(), negated);
                        break;
                    case INSTITUTION_TITLE:
                    case PROGRAM_TITLE:
                    case PROJECT_TITLE:
                        List<Integer> parentResourceIds = resourceDAO.getMatchingParentResources(PrismScope.valueOf(property.name().replace("_TITLE", "")),
                                constraint.getValueString());
                        ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, parentResourceIds, negated);
                        break;
                    case TITLE:
                        ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
                        break;
                    case RATING:
                        ResourceListConstraintBuilder.appendDecimalFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
                                constraint.getValueDecimalStart(), constraint.getValueDecimalClose(), negated);
                        break;
                    case STATE_GROUP_TITLE:
                        List<PrismState> stateIds = stateService.getStatesByStateGroup(constraint.getValueStateGroup());
                        ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, stateIds, negated);
                        break;
                    case SUBMITTED_TIMESTAMP:
                        ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraint.getFilterExpression(),
                                constraint.computeValueDateTimeStart(), constraint.computeValueDateTimeClose(), negated);
                        break;
                    case USER:
                        List<Integer> userIds = userService.getMatchingUsers(constraint.getValueString());
                        ResourceListConstraintBuilder.appendPropertyInFilterCriterion(conditions, propertyName, userIds, negated);
                        break;
                    case SUPERVISOR:
                        appendUserRoleFilterCriteria(scopeId, conditions, constraint, propertyName, Arrays.asList(PrismRole.PROJECT_PRIMARY_SUPERVISOR,
                                PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_SUGGESTED_SUPERVISOR, PrismRole.APPLICATION_PRIMARY_SUPERVISOR,
                                PrismRole.APPLICATION_SECONDARY_SUPERVISOR), negated);
                        break;
                    case THEME:
                        ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraint.getValueString(), negated);
                    }
                } else {
                    ResourceListConstraintBuilder.throwResourceFilterListMissingPropertyError(scopeId, property);
                }
            }

            return conditions;
        }
        return null;
    }

    private void appendUserRoleFilterCriteria(PrismScope scopeId, Junction conditions, ResourceListFilterConstraintDTO constraint, String propertyName,
            List<PrismRole> valueRoles, Boolean negated) {
        boolean doAddCondition = false;
        Disjunction condition = Restrictions.disjunction();
        for (PrismRole valueRole : valueRoles) {
            PrismScope roleScope = valueRole.getScope();
            String actualPropertyName = scopeId == roleScope ? propertyName : roleScope.getLowerCaseName();
            List<Integer> resourceIds = resourceDAO.getByMatchingUsersInRole(scopeId, constraint.getValueString(), valueRole);
            if (!resourceIds.isEmpty()) {
                ResourceListConstraintBuilder.appendPropertyInFilterCriterion(condition, actualPropertyName, resourceIds, negated);
                doAddCondition = true;
            }
        }
        if (doAddCondition) {
            conditions.add(condition);
        }
    }

    public void updateResource(PrismScope resourceScope, Integer resourceId, Object resourceDTO) {
        switch (resourceScope) {
        case INSTITUTION:
            institutionService.update(resourceId, (InstitutionDTO) resourceDTO);
            break;
        case PROGRAM:
            programService.update(resourceId, (ProgramDTO) resourceDTO);
            break;
        case PROJECT:
            projectService.update(resourceId, (ProjectDTO) resourceDTO);
            break;
        default:
            throw new Error();
        }
    }

}
