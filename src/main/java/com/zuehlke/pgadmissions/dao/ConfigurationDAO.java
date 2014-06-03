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

    private final SessionFactory sessionFactory;

    public ConfigurationDAO() {
        this(null);
    }

    @Autowired
    public ConfigurationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getStudyDuration(Institution institution, ProgramType programType) {
        return (int) (Integer) sessionFactory.getCurrentSession().createCriteria(Configuration.class) //
                .setProjection(Projections.property("value")) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("programType", programType)) //
                .uniqueResult();
    }

}
