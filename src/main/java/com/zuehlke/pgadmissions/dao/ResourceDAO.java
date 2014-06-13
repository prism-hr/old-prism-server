package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;

@Repository
public class ResourceDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @SuppressWarnings("unchecked")
    public List<PrismResourceDynamic> getConsoleList(PrismScope scope, User user, int pageIndex, int rowsPerPage) {
        String scopeName = scope.getLowerCaseName();
        return (List<PrismResourceDynamic>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("state." + scopeName + "s")) //
                .createAlias("role", "role", JoinType.INNER_JOIN)
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state." + scopeName + "s", scopeName, JoinType.INNER_JOIN)
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .addOrder(Order.desc("stateAction.raisesUrgentFlag")) //
                .addOrder(Order.desc(scopeName + ".updatedTimestamp")) //
                .setFirstResult(pageIndex) //
                .setMaxResults((pageIndex + 1) * rowsPerPage) //
                .list();
    }
    
    // TODO: implement
    public List<PrismResourceDynamic> getReportList(Class<? extends PrismResourceDynamic> clazz, User user) {
        return null;
    }
    
}
