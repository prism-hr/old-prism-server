package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.ProgramType;

@Repository
public class ConfigurationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public int getStudyDuration(Institution institution, ProgramType programType) {
        return (int) (Integer) sessionFactory.getCurrentSession().createCriteria(Configuration.class) //
                .setProjection(Projections.property("value")) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("programType", programType)) //
                .uniqueResult();
    }

}
