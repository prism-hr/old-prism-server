package uk.co.alumeni.prism.services;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.ActionDAO;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedactionType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.dto.ActionCreationScopeDTO;
import uk.co.alumeni.prism.dto.ActionDTO;
import uk.co.alumeni.prism.dto.ActionEnhancementDTO;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.ActionRedactionDTO;
import uk.co.alumeni.prism.exceptions.WorkflowPermissionException;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.user.UserRegistrationDTO;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

@Service
@Transactional
public class ActionService {

    @Inject
    private ActionDAO actionDAO;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    public Action getById(PrismAction id) {
        return entityService.getById(Action.class, id);
    }

    public void validateInvokeAction(Resource resource, Action action, User user, boolean declinedReponse) {
        resource = resourceService.getOperativeResource(resource, action);
        if (checkActionExecutable(resource, action, user, declinedReponse)) {
            return;
        }
        throw new WorkflowPermissionException(resource, action);
    }

    public List<Action> getActions(Resource resource) {
        return actionDAO.getActions(resource);
    }

    public ActionDTO getPermittedAction(User user, Resource resource, Action action) {
        PrismAction prismAction = action.getId();
        List<ActionDTO> permittedActions = getPermittedActions(user, resource, prismAction);
        if (isNotEmpty(permittedActions)) {
            return permittedActions.stream().filter(pa -> pa.getActionId().equals(prismAction)).findFirst().get();
        }
        return null;
    }

    public List<ActionDTO> getPermittedActions(User user, Resource resource) {
        return getPermittedActions(user, resource, null);
    }

    public TreeMultimap<Integer, ActionDTO> getPermittedActions(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources) {
        return getPermittedActions(user, scope, targeterEntities, resources, Collections.emptyList());
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope scope, Collection<Integer> resourceIds) {
        if (isNotEmpty(resourceIds)) {
            return actionDAO.getPermittedUnsecuredActions(scope, resourceIds, userService.isUserLoggedIn());
        }
        return Lists.newArrayList();
    }

    public TreeMultimap<Integer, ActionDTO> getCreateResourceActions(PrismScope scope, Collection<Integer> resourceIds) {
        TreeMultimap<Integer, ActionDTO> creationActions = TreeMultimap.create();
        for (ActionDTO resourceListActionDTO : getPermittedUnsecuredActions(scope, resourceIds)) {
            creationActions.put(resourceListActionDTO.getResourceId(), resourceListActionDTO);
        }
        return creationActions;
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(User user, Resource resource) {
        return getPermittedActionEnhancements(user, resource, getViewEditAction(resource).getId());
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(User user, Resource resource, PrismAction action) {
        return getPermittedActionEnhancements(user, resource, newArrayList(action), advertService.getAdvertTargeterEntities(user, resource.getResourceScope()))
                .stream().map(ae -> ae.getActionEnhancement()).collect(toList()); //
    }

    public List<ActionEnhancementDTO> getPermittedActionEnhancements(User user, Resource resource, Collection<PrismAction> actions) {
        return getPermittedActionEnhancements(user, resource, actions, advertService.getAdvertTargeterEntities(user, resource.getResourceScope()));
    }

    public ActionOutcomeDTO executeUserAction(Resource resource, Action action, Comment comment) {
        validateInvokeAction(resource, action, comment.getUser(), comment.getDeclinedResponse());
        return executeAction(resource, action, comment);
    }

    public ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment) {
        return executeAction(resource, action, comment, true);
    }

    public ActionOutcomeDTO executeActionSilent(Resource resource, Action action, Comment comment) {
        return executeAction(resource, action, comment, false);
    }

    public ActionOutcomeDTO executeRegistrationAction(User user, UserRegistrationDTO registrationDTO) {
        CommentDTO commentDTO = registrationDTO.getComment();
        if (commentDTO != null) {
            commentDTO.setUser(user.getId());
            return resourceService.executeAction(user, commentDTO);
        }
        return null;
    }

    public Action getViewEditAction(Resource resource) {
        return actionDAO.getViewEditAction(resource);
    }

    public List<Action> getActions() {
        return entityService.getAll(Action.class);
    }

    public List<PrismAction> getEscalationActions() {
        return actionDAO.getEscalationActions();
    }

    public HashMultimap<PrismAction, PrismActionRedactionType> getRedactions(Resource resource, User user, List<PrismRole> overridingRoles) {
        HashMultimap<PrismAction, PrismActionRedactionType> actionRedactions = HashMultimap.create();
        if (overridingRoles.isEmpty()) {
            List<PrismRole> roleIds = roleService.getRolesForResource(resource, user);
            if (!roleIds.isEmpty()) {
                List<ActionRedactionDTO> redactions = actionDAO.getRedactions(resource, roleIds);
                for (ActionRedactionDTO redaction : redactions) {
                    actionRedactions.put(redaction.getActionId(), redaction.getRedactionType());
                }
            }
        }
        return actionRedactions;
    }

    public boolean hasRedactions(User user, PrismScope resourceScope, Collection<Integer> resources) {
        List<PrismRole> userRoles = roleService.getRolesByScope(user, resourceScope);
        if (userRoles.isEmpty()) {
            return false;
        }

        List<PrismRole> rolesOverridingRedations = roleService.getRolesOverridingRedactions(user, resourceScope, resources);
        if (!rolesOverridingRedations.isEmpty()) {
            return false;
        }

        List<PrismRole> rolesWithRedactions = roleService.getRolesWithRedactions(resourceScope);
        userRoles.removeAll(rolesWithRedactions);
        return userRoles.isEmpty();
    }

    public List<Action> getCustomizableActions() {
        return actionDAO.getCustomizableActions();
    }

    public List<Action> getConfigurableActions() {
        return actionDAO.getConfigurableActions();
    }

    public void setCreationActions() {
        List<ActionCreationScopeDTO> actionCreationScopes = actionDAO.getCreationActions();
        for (ActionCreationScopeDTO actionCreationScope : actionCreationScopes) {
            Scope creationScope = actionCreationScope.getCreationScope();
            PrismScope prismCreationScope = creationScope.getId();

            Action action = actionCreationScope.getAction();
            action.setActionCondition(prismCreationScope.ordinal() > INSTITUTION.ordinal() ? PrismActionCondition.valueOf("ACCEPT_" + prismCreationScope.name())
                    : null);
            action.setCreationScope(creationScope);
        }
    }

    public void setFallbackActions() {
        List<Action> actions = getActions();
        Map<PrismAction, Action> fallbackActions = Maps.newHashMap();
        for (Action action : actions) {
            Scope creationScope = action.getCreationScope();
            PrismScope actionScopeId = creationScope == null ? action.getScope().getId() : creationScope.getId();
            PrismAction fallbackActionId = actionScopeId == SYSTEM ? SYSTEM_VIEW_EDIT : PrismAction.valueOf("SYSTEM_VIEW_" + actionScopeId.name() + "_LIST");
            Action fallbackAction = fallbackActions.get(fallbackActionId);
            if (fallbackAction == null) {
                fallbackAction = getById(fallbackActionId);
                fallbackActions.put(fallbackActionId, fallbackAction);
            }
            action.setFallbackAction(fallbackAction);
        }
    }

    public void setStateGroupTransitionActions() {
        List<PrismAction> actions = actionDAO.getStateGroupTransitionActions();
        if (!actions.isEmpty()) {
            actionDAO.setStateGroupTransitionActions(actions);
        }
    }

    public void setSequenceStartActions() {

    }

    public void setSequenceCloseActions() {

    }

    public List<PrismActionCondition> getActionConditions(PrismScope prismScope) {
        return actionDAO.getActionConditions(prismScope);
    }

    public Map<PrismScope, PrismAction> getCreateResourceActions(PrismScope creationScope) {
        Map<PrismScope, PrismAction> createResourceActions = Maps.newHashMap();
        List<PrismAction> creationActions = actionDAO.getCreateResourceActions(creationScope);
        for (PrismAction creationAction : creationActions) {
            createResourceActions.put(creationAction.getScope(), creationAction);
        }
        return createResourceActions;
    }

    public List<PrismActionCondition> getExternalConditions(ResourceParent resource) {
        return actionDAO.getExternalConditions(resource);
    }

    public boolean checkActionVisible(Resource resource, Action action, User user) {
        boolean visible = true;
        Set<PrismActionEnhancement> expectedActionEnhancements = getExpectedActionEnhancements(resource, action);
        if (expectedActionEnhancements.size() > 0) {
            visible = getPermittedActionEnhancements(user, resource, action.getId()).stream().anyMatch(ae -> ae.name().contains("_VIEW"));
        }
        return visible ? checkActionAvailable(resource, action, user, false) : false;
    }

    public boolean checkActionExecutable(Resource resource, Action action, User user) {
        return checkActionExecutable(resource, action, user, false);
    }

    public boolean checkActionExecutable(Resource resource, Action action, User user, boolean declinedResponse) {
        boolean executable = true;
        Set<PrismActionEnhancement> expectedActionEnhancements = getExpectedActionEnhancements(resource, action);
        if (expectedActionEnhancements.size() > 0) {
            executable = getPermittedActionEnhancements(user, resource, action.getId()).stream().anyMatch(ae -> ae.name().contains("_VIEW_EDIT"));
        }
        return executable ? checkActionAvailable(resource, action, user, declinedResponse) : false;
    }

    public boolean checkActionAvailable(Resource resource, Action action, User user, boolean declinedResponse) {
        if (action.getDeclinableAction() && toBoolean(declinedResponse)) {
            return true;
        } else if (actionDAO.getPermittedUnsecuredAction(resource, action, userService.isUserLoggedIn()) != null) {
            return true;
        } else if (getPermittedAction(user, resource, action) != null) {
            return true;
        }
        return false;
    }

    public PrismActionEnhancement[] getAdministratorActionEnhancements(PrismScope scope) {
        String scopeName = scope.name();
        List<PrismActionEnhancement> actionEnhancements = Lists.newArrayList();
        for (PrismActionEnhancement actionEnhancement : PrismActionEnhancement.values()) {
            String actionEnhancementName = actionEnhancement.name();
            if (actionEnhancementName.contains(scopeName) && actionEnhancementName.contains("VIEW_EDIT")) {
                actionEnhancements.add(actionEnhancement);
            }
        }
        return actionEnhancements.toArray(new PrismActionEnhancement[actionEnhancements.size()]);
    }

    private ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment, boolean notify) {
        User user = comment.getUser();

        if (commentService.prepareComment(comment)) {
            boolean createAction = action.getActionCategory().equals(CREATE_RESOURCE);
            if (createAction || action.getActionCategory().equals(VIEW_EDIT_RESOURCE)) {
                Resource duplicate = entityService.getDuplicateEntity(resource);

                if (duplicate != null) {
                    if (createAction) {
                        return new ActionOutcomeDTO().withUser(user).withResource(duplicate).withTransitionResource(duplicate)
                                .withTransitionAction(getViewEditAction(duplicate));
                    } else if (!equal(resource.getId(), duplicate.getId())) {
                        throw new WorkflowPermissionException(resource, action);
                    }
                }
            }

            StateTransition stateTransition = stateService.executeStateTransition(resource, action, comment, notify);
            Action transitionAction = stateTransition == null ? action.getFallbackAction() : stateTransition.getTransitionAction();
            Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

            ActionOutcomeDTO actionOutcome = new ActionOutcomeDTO().withUser(user).withResource(resource).withTransitionResource(transitionResource)
                    .withTransitionAction(transitionAction).withStateTransition(stateTransition);

            LinkedList<Comment> replicableSequenceComments = null;
            if (stateTransition != null && isTrue(stateTransition.getReplicableSequenceClose())) {
                replicableSequenceComments = newLinkedList();
                for (Comment transitionComment : commentService.getTransitionCommentHistory(transitionResource)) {
                    replicableSequenceComments.push(transitionComment);
                    StateAction stateAction = stateService.getStateAction(transitionComment.getState(), transitionComment.getAction());
                    if (isTrue(stateAction.getReplicableSequenceStart())) {
                        break;
                    }
                }
            }

            if (isNotEmpty(replicableSequenceComments)) {
                if (isNotEmpty(resourceService.getResourcesForStateActionPendingAssignment(user, transitionResource, stateTransition,
                        replicableSequenceComments))) {
                    actionOutcome.setReplicableSequenceComments(replicableSequenceComments);
                }
            }

            return actionOutcome;
        }

        commentService.createOrUpdateComment(resource, comment);
        return new ActionOutcomeDTO().withUser(user).withResource(resource).withTransitionResource(resource).withTransitionAction(action);
    }

    private List<ActionDTO> getPermittedActions(User user, Resource resource, PrismAction action) {
        Integer resourceId = resource.getId();
        PrismScope scope = resource.getResourceScope();
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        return newLinkedList(getPermittedActions(user, scope, targeterEntities, newArrayList(resourceId), action).get(resourceId));
    }

    private Set<PrismActionEnhancement> getExpectedActionEnhancements(Resource resource, Action action) {
        Set<PrismActionEnhancement> expected = Sets.newHashSet(actionDAO.getExpectedDefaultActionEnhancements(resource, action));
        expected.addAll(actionDAO.getExpectedCustomActionEnhancements(resource, action));
        return expected;
    }

    private List<ActionEnhancementDTO> getPermittedActionEnhancements(User user, Resource resource, Collection<PrismAction> actions,
            Collection<Integer> targeterEntities) {
        PrismScope scope = resource.getResourceScope();
        Set<ActionEnhancementDTO> enhancements = Sets.newHashSet();
        for (String enhancementProperty : new String[] { "stateAction.actionEnhancement", "stateActionAssignment.actionEnhancement" }) {
            enhancements.addAll(getPermittedActionEnhancements(user, scope, targeterEntities, newArrayList(resource.getId()), actions, enhancementProperty));
        }
        return newArrayList(enhancements);
    }

    private TreeMultimap<Integer, ActionDTO> getPermittedActions(User user, PrismScope scope, Collection<Integer> targeterEntities,
            Collection<Integer> resources,
            PrismAction action) {
        return getPermittedActions(user, scope, targeterEntities, resources, action == null ? null : newArrayList(action));
    }

    private TreeMultimap<Integer, ActionDTO> getPermittedActions(User user, PrismScope scope, Collection<Integer> targeterEntities,
            Collection<Integer> resources,
            Collection<PrismAction> actions) {
        TreeMultimap<Integer, ActionDTO> permittedActions = TreeMultimap.create();
        getActionEntities(user, scope, targeterEntities, resources, actions,
                Projections.projectionList() //
                        .add(Projections.groupProperty("resource.id").as("resourceId")) //
                        .add(Projections.groupProperty("action.id").as("actionId")) //
                        .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                        .add(Projections.max("primaryState").as("primaryState")) //
                        .add(Projections.min("stateActionAssignment.externalMode").as("onlyAsPartner")) //
                        .add(Projections.property("action.declinableAction").as("declinable")),
                ActionDTO.class).forEach(permittedAction -> permittedActions.put(permittedAction.getResourceId(), permittedAction));

        return permittedActions;
    }

    private List<ActionEnhancementDTO> getPermittedActionEnhancements(User user, PrismScope scope, Collection<Integer> targeterEntities,
            Collection<Integer> resources,
            Collection<PrismAction> actions, String column) {
        return newArrayList(getActionEntities(user, scope, targeterEntities, resources, actions,
                Projections.projectionList() //
                        .add(Projections.groupProperty("action.id").as("action")) //
                        .add(Projections.groupProperty(column).as("actionEnhancement")),
                Restrictions.isNotNull(column),
                ActionEnhancementDTO.class));
    }

    private <T> Set<T> getActionEntities(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources,
            Collection<PrismAction> actions,
            ProjectionList columns, Class<T> responseClass) {
        return getActionEntities(user, scope, targeterEntities, resources, actions, columns, null, responseClass);
    }

    private <T> Set<T> getActionEntities(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources,
            Collection<PrismAction> actions,
            ProjectionList columns, Criterion restriction, Class<T> responseClass) {
        Set<T> actionEntities = Sets.newHashSet();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
        actionEntities.addAll(actionDAO.getActionEntities(user, scope, resources, actions, columns, restriction, responseClass));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                actionEntities.addAll(actionDAO.getActionEntities(user, scope, parentScope, resources, actions, columns, restriction, responseClass));
            }

            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    for (PrismScope targetScope : organizationScopes) {
                        actionEntities.addAll(
                                actionDAO.getActionEntities(user, scope, targeterScope, targetScope, targeterEntities, resources, actions, columns,
                                        restriction, responseClass));
                    }
                }
            }
        }

        return actionEntities;
    }

}
