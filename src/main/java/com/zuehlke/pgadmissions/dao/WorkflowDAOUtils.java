package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class WorkflowDAOUtils {

    public static Junction getUserRoleConstraint(Resource resource) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                .add(Restrictions.eq("userRole.project", resource.getProject())) //
                .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                .add(Restrictions.eq("userRole.department", resource.getDepartment())) //
                .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                .add(Restrictions.eq("userRole.system", resource.getSystem()));
    }

    public static Junction getUserRoleWithPartnerConstraint(Resource resource) {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(getUserRoleConstraint(resource)) //
                        .add(Restrictions.eq("stateActionAssignment.externalMode", false))) //
                .add(getPartnerUserRoleConstraint()) //
                .add(getResourceStateActionConstraint());
    }

    public static Junction getUserRoleWithPartnerConstraint(Resource resource, User user) {
        return Restrictions.conjunction() //
                .add(getUserRoleWithPartnerConstraint(resource)) //
                .add(getUserEnabledConstraint(user));
    }

    public static Junction getPartnerUserRoleConstraint() {
        return Restrictions.conjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("targetAdvert.department", "userRole.department"))
                        .add(Restrictions.eqProperty("targetAdvert.institution", "userRole.institution"))
                        .add(Restrictions.eqProperty("targetAdvert.system", "userRole.system")))
                .add(Restrictions.eq("stateActionAssignment.externalMode", true));
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
        return Restrictions.eq("ownerRole.role.id", DEPARTMENT_STUDENT);
    }

    public static Junction getEndorsementActionFilterResolution() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eqProperty("action.partnershipState", "target.partnershipState")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("target.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("target.targetAdvertUser", "userRole.user")))
                        .add(getEndorsementActionVisibilityResolution()));
    }

    public static Junction getEndorsementActionVisibilityResolution() {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("scope.defaultShared", true)) //
                        .add(Restrictions.eq("resource.shared", true))) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("ownerDepartment.id", "targetAdvert.department.id"))
                                .add(Restrictions.eqProperty("ownerDepartment.institution.id", "targetAdvert.institution.id")))
                        .add(Restrictions.eq("resource.shared", true)));
    }

    public static ProjectionList getResourceOpportunityCategoryProjection() {
        return Projections.projectionList() //
                .add(Projections.groupProperty("id").as("id")) //
                .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                .add(Projections.property("opportunityCategories").as("opportunityCategories"));
    }

    public static Junction getOpportunityCategoryConstraint(PrismOpportunityCategory opportunityCategory) {
        String opportunityCategoryName = opportunityCategory.name();
        return Restrictions.disjunction() //
                .add(Restrictions.like("opportunityCategories", opportunityCategoryName + "|", MatchMode.START))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName + "|", MatchMode.ANYWHERE))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName, MatchMode.END));
    }

}
