package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;

@Repository
public class QualificationInstitutionDAO {

    private final SessionFactory sessionFactory;

    QualificationInstitutionDAO() {
        this(null);
    }

    @Autowired
    public QualificationInstitutionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getAllInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class).addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public QualificationInstitution getInstitutionById(Integer id) {
        return (QualificationInstitution) sessionFactory.getCurrentSession().get(QualificationInstitution.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getInstitutionsByCountryName(String country_name) {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.eq("country_name", country_name))
                .addOrder(Order.asc("name")).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(String name, String term) {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.and(Restrictions.eq("country_name", name), 
                        Restrictions.or(Restrictions.ilike("name", term, MatchMode.END), Restrictions.ilike("name", term, MatchMode.START))))
                .addOrder(Order.asc("name")).list();
    }
}
