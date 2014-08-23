package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public List<Integer> getActiveProjectIds() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action.id", PrismAction.PROGRAM_CREATE_APPLICATION)) //
                .list();
    }
    
}
