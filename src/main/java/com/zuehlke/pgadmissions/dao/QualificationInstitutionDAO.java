package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;

@Repository
@SuppressWarnings("unchecked")
public class QualificationInstitutionDAO {

    private final SessionFactory sessionFactory;

    public QualificationInstitutionDAO() {
        this(null);
    }

    @Autowired
    public QualificationInstitutionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<QualificationInstitution> getEnabledInstitutionsByDomicileCode(String domicileCode) {
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.eq("enabled", true));
        conjunction.add(Restrictions.eq("domicileCode", domicileCode));

        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class).add(conjunction).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public QualificationInstitution getInstitutionByCode(String institutionCode) {
        return (QualificationInstitution) sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.eq("code", institutionCode)).uniqueResult();
    }

    public QualificationInstitution getLastCustomInstitution() {
        DetachedCriteria maxCustomCode = DetachedCriteria.forClass(QualificationInstitution.class).setProjection(Projections.max("code"))
                .add(Restrictions.like("code", "CUST%"));
        return (QualificationInstitution) sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Property.forName("code").eq(maxCustomCode)).uniqueResult();
    }

    public void save(QualificationInstitution institution) {
        sessionFactory.getCurrentSession().saveOrUpdate(institution);
    }

}
