package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.StateTransitionDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.NextStateRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<StateTransition> getPotentialStateTransitions(Resource resource, Action action) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state." + resourceReference + "s", resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference + ".id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.isNotNull("transitionState")) //
                .list();
    }

    public List<StateTransition> getPotentialUserStateTransitions(Resource resource, Action action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceState.primaryState", true)) //
                                .add(Restrictions.isNotNull("transitionState"))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceState.primaryState", false)) //
                                .add(Restrictions.isNull("transitionState")))) //
                .list();
    }

    public StateTransition getSecondaryStateTransition(Resource resource, State state, Action action) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getPreviousState())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("transitionState", state)) //
                .uniqueResult();
    }

    public StateTransition getStateTransition(Resource resource, Action action) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .createAlias("stateAction.state", "state") //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.isNull("transitionState")) //
                .addOrder(Order.desc("resourceState.primaryState")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public StateTransition getStateTransition(Resource resource, Action action, PrismState transitionStateId) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .createAlias("stateAction.state", "state") //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("transitionState.id", transitionStateId)) //
                .addOrder(Order.desc("resourceState.primaryState")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        String scopeReference = scopeId.getLowerCamelName();
        return (List<StateTransitionPendingDTO>) sessionFactory.getCurrentSession().createCriteria(StateTransitionPending.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property(scopeReference + ".id"), "resourceId") //
                        .add(Projections.property("action.id"), "actionId")) //
                .add(Restrictions.isNotNull(scopeReference)) //
                .addOrder(Order.asc(scopeReference + ".id")) //
                .addOrder(Order.asc("id")) //
                .setResultTransformer(Transformers.aliasToBean(StateTransitionPendingDTO.class)).list();
    }

    public List<State> getConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.isNotNull("stateDurationDefinition")) //
                .list();
    }

    public void deleteObsoleteStateDurations() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateDurationConfiguration " //
                        + "where stateDurationDefinition not in ( " //
                        + "select stateDurationDefinition " //
                        + "from State " //
                        + "group by stateDurationDefinition)") //
                .executeUpdate();
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateTransition.transitionState")) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("transitionState.scope", "scope", JoinType.INNER_JOIN) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("state", state));

        for (State excludedTransitionState : excludedTransitionStates) {
            criteria.add(Restrictions.ne("stateTransition.transitionState", excludedTransitionState));
        }

        return (List<State>) criteria //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .addOrder(Order.asc("scope.ordinal")) //
                .addOrder(Order.asc("stateGroup.ordinal")) //
                .list();
    }

    public List<PrismState> getInstitutionStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("scope.id", INSTITUTION)) //
                .list();
    }

    public List<PrismState> getActiveInstitutionStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .add(Restrictions.eq("action.id", INSTITUTION_CREATE_APPLICATION)) //
                .list();
    }

    public List<PrismState> getProgramStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("scope.id", PROGRAM)) //
                .list();
    }

    public List<PrismState> getActiveProgramStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .add(Restrictions.eq("action.id", PROGRAM_CREATE_APPLICATION)) //
                .list();
    }

    public List<PrismState> getProjectStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.eq("scope.id", PROJECT)) //
                .list();
    }

    public List<PrismState> getActiveProjectStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .add(Restrictions.eq("action.id", PROJECT_CREATE_APPLICATION)) //
                .list();
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("stateGroup.id", stateGroupId)) //
                .list();
    }

    public List<State> getCurrentStates(Resource resource) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.desc("primaryState")) //
                .list();
    }

    public String getRecommendedNextStates(Resource resource) {
        Resource parentResource = resource.getParentResource();
        return (String) sessionFactory.getCurrentSession().createCriteria(ResourceStateTransitionSummary.class) //
                .setProjection(Projections.property("transitionStateSelection")) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCamelName(), parentResource)) //
                .add(Restrictions.eq("stateGroup", resource.getState().getStateGroup())) //
                .add(Restrictions.ge("frequency", 3)) //
                .addOrder(Order.desc("frequency")) //
                .addOrder(Order.desc("updatedTimestamp")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<State> getSecondaryResourceStates(Resource resource) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("primaryState", false)) //
                .list();
    }

    public List<PrismStateGroup> getSecondaryResourceStateGroups(PrismScope resourceScope, Integer resourceId) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state.stateGroup.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceScope.getLowerCamelName() + ".id", resourceId)) //
                .add(Restrictions.eq("primaryState", false)) //
                .list();
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state, PrismAction actionId, boolean importedResource) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("transitionState.id"), "state") //
                        .add(Projections.property("transitionState.parallelizable"), "parallelizable")) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq("stateTransitionEvaluation.nextStateSelection", true)) //
                .add(Restrictions.isNotNull("transitionState"));

        appendImportedResourceConstraint(criteria, importedResource);

        return (List<NextStateRepresentation>) criteria.addOrder(Order.asc("transitionStateGroup.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(NextStateRepresentation.class)) //
                .list();
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state, boolean importedResource) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("transitionState.id"), "state") //
                        .add(Projections.property("transitionState.parallelizable"), "parallelizable")) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateTransitionEvaluation.nextStateSelection", true)) //
                .add(Restrictions.isNotNull("transitionState"));

        appendImportedResourceConstraint(criteria, importedResource);

        return (List<NextStateRepresentation>) criteria.addOrder(Order.asc("transitionStateGroup.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(NextStateRepresentation.class)) //
                .list();
    }

    public List<StateTransitionDTO> getStateTransitions() {
        return (List<StateTransitionDTO>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("state"), "state") //
                        .add(Projections.property("action"), "action") //
                        .add(Projections.property("stateTransition.transitionState"), "transitionState")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("action.transitionAction")) //
                .add(Restrictions.isNotNull("stateTransition.transitionState")) //
                .addOrder(Order.asc("state")) //
                .addOrder(Order.asc("action")) //
                .addOrder(Order.asc("stateTransition.transitionState")) //
                .setResultTransformer(Transformers.aliasToBean(StateTransitionDTO.class)) //
                .list();
    }

    public List<PrismState> getHiddenStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("stateActions", "stateAction", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.isNull("stateAction.id")) //
                .list();
    }

    public void setHiddenStates(List<PrismState> states) {
        sessionFactory.getCurrentSession().createQuery( //
                "update State " //
                        + "set hidden = true " //
                        + "where id in (:states)")
                .setParameterList("states", states) //
                .executeUpdate();
    }

    public List<PrismState> getParallelizableStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("stateTransition.transitionState")) //
                .list();
    }

    public void setParallelizableStates(List<PrismState> states) {
        sessionFactory.getCurrentSession().createQuery( //
                "update State " //
                        + "set parallelizable = true " //
                        + "where id in (:states)")
                .setParameterList("states", states) //
                .executeUpdate();
    }

    private void appendImportedResourceConstraint(Criteria criteria, boolean importedResource) {
        if (importedResource) {
            criteria.add(Restrictions.ne("transitionState.id", PROGRAM_DISABLED_COMPLETED)); //
        }
    }

}
