package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class WorkflowDAOUtils {

    public static Junction getUserRoleConstraint(Resource resource, String targetEntity) {
        PrismScope resourceScope = resource.getResourceScope();
        Junction constraint = Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(getUserRoleConstraint(resource)) //
                        .add(Restrictions.eq(targetEntity + ".partnerMode", false))) //
                .add(getPartnerUserRoleConstraint(resourceScope, targetEntity)); //
        return constraint;
    }

    public static Junction getUserRoleConstraint(Resource resource, User user, String targetEntity) {
        return Restrictions.conjunction() //
                .add(getUserRoleConstraint(resource, targetEntity)) //
                .add(getResourceStateActionConstraint()) //
                .add(getUserEnabledConstraint(user));
    }

    public static Junction getUserRoleConstraint(Resource resource) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                .add(Restrictions.eq("userRole.project", resource.getProject())) //
                .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                .add(Restrictions.eq("userRole.department", resource.getDepartment())) //
                .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                .add(Restrictions.eq("userRole.system", resource.getSystem()));
    }

    public static Junction getPartnerUserRoleConstraint(PrismScope resourceScope, String targetEntity) {
        return Restrictions.conjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("targetAdvert.department", "userRole.department"))
                        .add(Restrictions.eqProperty("targetAdvert.institution", "userRole.institution"))
                        .add(Restrictions.eqProperty(resourceScope.equals(SYSTEM) ? "system" : resourceScope.getLowerCamelName() + ".system", "userRole.system")))
                .add(Restrictions.eq(targetEntity + ".partnerMode", true));
    }

    public static Junction getUserEnabledConstraint(User user) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true));
    }

    public static Junction getResourceStateActionConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("stateAction.actionCondition")) //
                .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"));
    }

    public static Junction getSimilarUserRestriction(String searchTerm) {
        return getSimilarUserRestriction(null, searchTerm);
    }

    public static Junction getSimilarUserRestriction(String alias, String searchTerm) {
        alias = StringUtils.isEmpty(alias) ? "" : alias + ".";
        return Restrictions.disjunction() //
                .add(Restrictions.like(alias + "firstName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "lastName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "fullName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "email", searchTerm, MatchMode.START));
    }

    public static Criterion getEndorsementActionJoinResolution() {
        return Restrictions.eq("advertTarget.selected", true);
    }

    public static Junction getEndorsementActionFilterResolution() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eqProperty("action.partnershipState", "advertTarget.partnershipState")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eq("role.roleCategory", ADMINISTRATOR)) //
                                .add(Restrictions.eqProperty("advertTarget.valueUser", "userRole.user")))
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ne("action.scope.id", APPLICATION)) //
                                .add(Restrictions.eqProperty("ownerAdvert.advert", "advertTarget.value"))));
    }

    public static ProjectionList getResourceOpportunityCategoryProjection() {
        return Projections.projectionList() //
                .add(Projections.groupProperty("id").as("id")) //
                .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                .add(Projections.property("opportunityCategories").as("opportunityCategories"));
    }

}
