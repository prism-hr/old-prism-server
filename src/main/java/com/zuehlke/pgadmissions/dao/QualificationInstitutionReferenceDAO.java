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
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;

@Repository
@SuppressWarnings("unchecked")
public class QualificationInstitutionReferenceDAO {

    private final SessionFactory sessionFactory;

    public QualificationInstitutionReferenceDAO() {
        this(null);
    }

    @Autowired
    public QualificationInstitutionReferenceDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<QualificationInstitutionReference> getAllEnabledInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitutionReference.class)
                .add(Restrictions.eq("enabled", true)).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<QualificationInstitutionReference> getAllInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitutionReference.class)
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    public List<QualificationInstitutionReference> getAllInstitutionByName(String name) {
        return (List<QualificationInstitutionReference>) sessionFactory.getCurrentSession().createCriteria(QualificationInstitutionReference.class)
                .add(Restrictions.eq("name", name))
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public QualificationInstitution getInstitutionById(Integer id) {
        return (QualificationInstitution) sessionFactory.getCurrentSession().get(QualificationInstitutionReference.class, id);
    }

    public List<QualificationInstitutionReference> getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(String domicileCode, String term) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(QualificationInstitutionReference.class)
                .add(Restrictions.and(
                        Restrictions.eq("enabled", true),
                        Restrictions.and(
                                Restrictions.eq("domicileCode", domicileCode),
                                Restrictions.or(Restrictions.ilike("name", term, MatchMode.END),
                                        Restrictions.ilike("name", term, MatchMode.START)))))
                .addOrder(Order.asc("name")).list();
    }

    public void save(QualificationInstitutionReference institution) {
        sessionFactory.getCurrentSession().saveOrUpdate(institution);
    }
}
