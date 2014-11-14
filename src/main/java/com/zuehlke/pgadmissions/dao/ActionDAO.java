package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PURGE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestion;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Action getDelegateAction(Resource resource, Action action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateActionAssignment.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias("delegatedAction", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "delegateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("delegatedAction", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("delegateAction.actionType", PrismActionType.USER_INVOCATION)) //
                .uniqueResult();
    }

    public Action getUserRedirectAction(Resource resource, User user) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("defaultAction", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult();
    }

    public Action getSystemRedirectAction(Resource resource) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.SYSTEM_INVOCATION)) //
                .add(Restrictions.eq("defaultAction", true)) //
                .uniqueResult();
    }

    public Action getPermittedAction(Resource resource, Action action, User user) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateActionAssignment.id")) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                                .add(Restrictions.eq("userRole.user", user)) //
                                .add(Restrictions.eq("userAccount.enabled", true)))) //
                .uniqueResult();
    }

    public List<ActionDTO> getPermittedActions(Resource resource, User user) {
        return (List<ActionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class, "resourceState") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("stateAction.action.id"), "actionId") //
                        .add(Projections.property("stateAction.raisesUrgentFlag"), "raisesUrgentFlag") //
                        .add(Projections.groupProperty("stateTransition.transitionState.id"), "transitionStateId") //
                        .add(Projections.groupProperty("roleTransition.transitionRole.id"), "transitionRoleId") //
                        .add(Projections.groupProperty("roleTransition.roleTransitionType"), "roleTransitionType") //
                        .add(Projections.property("roleTransition.minimumPermitted"), "minimumPermitted") //
                        .add(Projections.property("roleTransition.maximumPermitted"), "maximumPermitted")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("stateAction.stateTransitions", "stateTransition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("stateTransition.roleTransitions", "roleTransition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                                .add(Restrictions.ne("action.creationScope.id", PrismScope.APPLICATION)))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateActionAssignment.id")) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("userRole.user", user)) //
                                .add(Restrictions.eq("userAccount.enabled", true)))) //
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.asc("action.id")) //
                .addOrder(Order.asc("stateTransition.transitionState.id")) //
                .addOrder(Order.asc("roleTransition.transitionRole.id")) //
                .addOrder(Order.asc("roleTransition.roleTransitionType")) //
                .setResultTransformer(Transformers.aliasToBean(ActionDTO.class)) //
                .list();
    }

    public List<ActionRepresentation> getPermittedActions(PrismScope resourceScope, Integer resourceId, Integer systemId, Integer institutionId,
            Integer programId, Integer projectId, Integer applicationId, User user) {
        return (List<ActionRepresentation>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class, "resourceState") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("stateAction.action.id"), "name") //
                        .add(Projections.max("stateAction.raisesUrgentFlag"), "raisesUrgentFlag")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq(resourceScope.getLowerCaseName() + ".id", resourceId)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.system.id", systemId)) //
                        .add(Restrictions.eq("userRole.institution.id", institutionId)) //
                        .add(Restrictions.eq("userRole.program.id", programId)) //
                        .add(Restrictions.eq("userRole.project.id", projectId)) //
                        .add(Restrictions.eq("userRole.application.id", applicationId)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.CREATE_RESOURCE)) //
                                .add(Restrictions.ne("action.creationScope.id", PrismScope.APPLICATION)))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateActionAssignment.id")) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("userRole.user", user)) //
                                .add(Restrictions.eq("userAccount.enabled", true)))) //
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.asc("action.id")) //
                .setResultTransformer(Transformers.aliasToBean(ActionRepresentation.class)) //
                .list();
    }

    public List<ActionRedactionDTO> getRedactions(Resource resource, List<PrismRole> roleIds) {
        return (List<ActionRedactionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class, "comment")
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.groupProperty("redaction.redactionType"), "redactionType")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.redactions", "redaction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.in("redaction.role.id", roleIds)) //
                .setResultTransformer(Transformers.aliasToBean(ActionRedactionDTO.class)) //
                .list();
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.actionEnhancement")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateAction.actionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.actionEnhancement")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

    public Action getViewEditAction(Resource resource) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .uniqueResult();
    }

    public List<PrismAction> getEscalationActions() {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.in("action.actionCategory", Arrays.asList(ESCALATE_RESOURCE, PURGE_RESOURCE))) //
                .addOrder(Order.desc("scope.precedence")) //
                .list();
    }

    public List<PrismAction> getPropagatedActions(Integer stateTransitionPendingId) {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateTransitionPending.class) //
                .setProjection(Projections.property("propagatedAction.id")) //
                .createAlias("stateTransition", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.propagatedActions", "propagatedAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", stateTransitionPendingId)) //
                .list();
    }

    public List<Action> getCustomizableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("customizableAction", true)) //
                .list();
    }

    public List<Action> getConfigurableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("configurableAction", true)) //
                .list();
    }

    public void deleteActionConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, Action action) {
        String localeConstraint = locale == null ? "" : "and locale = " + locale.name() + " ";
        String programTypeConstraint = programType == null ? "" : "and programType = " + programType.name();
        sessionFactory.getCurrentSession().createQuery( //
                "update ActionPropertyConfiguration " //
                        + "set active = false " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + localeConstraint //
                        + programTypeConstraint).setParameter("resource", resource).executeUpdate();
    }

    public void restoreGlobalActionConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, Action action) {
        PrismScope resourceScope = resource.getResourceScope();
        String programTypeConstraint = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeConstraint = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                "update ActionPropertyConfiguration " //
                + "set active = false " //
                + "where action = :action " //
                    + "and (institution in (" //
                        + "from Institution " //
                        + "where system = :system) " //
                    + programTypeConstraint //
                    + localeConstraint + " "//
                    + "or program in (" //
                        + "from Program " //
                        + "where system = :system) " //
                    + programTypeConstraint //
                    + localeConstraint + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                "update ActionPropertyConfiguration " //
                + "set active = false " //
                + "where action = :action " //
                    + "and (program in (" //
                        + "from Program " //
                        + "where institution = :institution) " //
                    + programTypeConstraint //
                    + localeConstraint + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("action", action);

        if (programType != null) {
            query.setParameter("programType", programType);
        }

        if (locale != null) {
            query.setParameter("locale", locale);
        }

        query.executeUpdate();
    }

    public List<ActionCustomQuestion> getActionPropertyConfigurationByVersion(Integer version) {
        return (List<ActionCustomQuestion>) sessionFactory.getCurrentSession().createCriteria(ActionCustomQuestion.class) //
                .add(Restrictions.eq("version", version)) //
                .addOrder(Order.asc("index")) //
                .list();
    }

}
