package com.zuehlke.pgadmissions.dao;

import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class WorkflowDAOUtils {

    public static Junction getUserRoleConstraint(Resource resource, String targetEntity) {
        Junction constraint = Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                                .add(Restrictions.eq("userRole.project", resource.getProject())) //
                                .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                                .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                                .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                        .add(Restrictions.eq(targetEntity + ".partnerMode", false))); //

        Institution partner = resource.getPartner();
        if (partner != null) {
            constraint.add(Restrictions.conjunction() //
                    .add(Restrictions.eq(targetEntity + ".partnerMode", true)) //
                    .add(Restrictions.eq("userRole.institution", resource.getPartner())));
        }

        return constraint;
    }

    public static Junction getUserRoleConstraint(Resource resource, User user, String targetEntity) {
        return Restrictions.conjunction() //
                .add(getUserRoleConstraint(resource, targetEntity)) //
                .add(getResourceStateActionConstraint()) //
                .add(getUserEnabledConstraint(user));
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

}
