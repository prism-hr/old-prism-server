package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Project> getProjectsLinkedToProgramDueDate(Program program) {
        return (List<Project>) sessionFactory.getCurrentSession().createCriteria(Project.class)
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.in("state.id", Arrays.asList(PrismState.PROJECT_APPROVED, PrismState.PROJECT_DEACTIVATED))) //
                .add(Restrictions.isNull("advert.closingDate")) //
                .list();
    }
    
}
