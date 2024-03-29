package uk.co.alumeni.prism.dao;

import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.ResourceState;

import javax.inject.Inject;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static uk.co.alumeni.prism.PrismConstants.FULL_STOP;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.EQUAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

@Component
public class WorkflowDAO {

    @Inject
    private SessionFactory sessionFactory;

    public static PrismScope[] opportunityScopes = new PrismScope[]{PROJECT, PROGRAM};

    public static PrismScope[] organizationScopes = new PrismScope[]{DEPARTMENT, INSTITUTION};

    public static PrismScope[] advertScopes = new PrismScope[]{PROJECT, PROGRAM, DEPARTMENT, INSTITUTION};

    public Criteria getWorkflowCriteriaList(PrismScope scope, Projection projection) {
        return getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("role.scope.id", scope))
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope scope, PrismScope parentScope, Projection projection) {
        return getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("role.scope.id", scope))
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Projection projection) {
        return getWorkflowCriteriaList(scope, targeterScope, targetScope, null, projection);
    }

    public Criteria getWorkflowCriteriaList(PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> targeterEntities,
            Projection projection) {
        Criteria criteria = getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert.targets", "targeterTarget", JoinType.INNER_JOIN) //
                .createAlias("targeterTarget.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN) //
                .createAlias("targetResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("role.scope.id", scope))
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN);

        if (CollectionUtils.isNotEmpty(targeterEntities)) {
            criteria.add(Restrictions.in(scope.equals(APPLICATION) ? "resource.id" : "targeterResource.advert.id", targeterEntities));
        }

        return criteria //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public static Junction getTargetActionConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.conjunction() //
                                        .add(Restrictions.isNull("target.id")) //
                                        .add(Restrictions.eq("action.partnershipTransitionState", ENDORSEMENT_REVOKED))) //
                                .add(Restrictions.eqProperty("action.partnershipState", "target.partnershipState"))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eq("resource.shared", true)) //
                                .add(Restrictions.eq("scope.defaultShared", true))));
    }

    public static Junction getMatchingUserConstraint(String searchTerm) {
        return getMatchingUserConstraint(null, searchTerm);
    }

    public static Junction getMatchingUserConstraint(String alias, String searchTerm) {
        alias = isEmpty(alias) ? "" : alias + ".";
        return Restrictions.disjunction() //
                .add(Restrictions.like(alias + "firstName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "lastName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "fullName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "email", searchTerm, MatchMode.START));
    }

    public static Criterion getLikeConstraint(String property, String query) {
        return Restrictions.like(property, query, MatchMode.ANYWHERE);
    }

    public static MatchMode getMatchMode(PrismResourceListFilterExpression expression) {
        return expression.equals(EQUAL) ? MatchMode.EXACT : MatchMode.ANYWHERE;
    }

    public static Junction getReadMessageConstraint() {
        return Restrictions.conjunction() //
                .add(Restrictions.isNotNull("participant.lastViewedMessage")) //
                .add(Restrictions.geProperty("participant.lastViewedMessage.id", "message.id")); //
    }

    public static Junction getUnreadMessageConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("participant.lastViewedMessage")) //
                .add(Restrictions.ltProperty("participant.lastViewedMessage.id", "message.id"));
    }

    public static Junction getReadOrUnreadMessageConstraint(boolean read) {
        return read ? getReadMessageConstraint() : getUnreadMessageConstraint();
    }

    public static Junction getVisibleMessageConstraint() {
        return getVisibleMessageConstraint(null);
    }

    public static Junction getVisibleMessageConstraint(String messageAlias) {
        String messageIdReference = Joiner.on(FULL_STOP).skipNulls().join(messageAlias, "id");
        return Restrictions.conjunction() //
                .add(Restrictions.geProperty(messageIdReference, "participant.startMessage.id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("participant.closeMessage")) //
                        .add(Restrictions.ltProperty(messageIdReference, "participant.closeMessage.id")));
    }

    public static Junction getMatchingFlattenedPropertyConstraint(String property, String searchTerm) {
        return Restrictions.disjunction(Restrictions.eq(property, searchTerm))
                .add(Restrictions.like(property, searchTerm + "|", MatchMode.START))
                .add(Restrictions.like(property, "|" + searchTerm + "|", MatchMode.ANYWHERE))
                .add(Restrictions.like(property, "|" + searchTerm, MatchMode.END));
    }

    public static String getResolvedAliasReference(String aliasReference) {
        return isEmpty(aliasReference) ? aliasReference : aliasReference + ".";
    }

    public static Criterion getResourceParentManageableStateConstraint(String stateAlias) {
        return Restrictions.eq(stateAlias + ".manageable", true);
    }

    private Criteria getWorkflowCriteriaListResource(PrismScope scope, Projection projection) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(projection) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN);
    }

}
