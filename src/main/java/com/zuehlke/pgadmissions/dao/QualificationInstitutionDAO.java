package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;

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
    public List<QualificationInstitution> getAllEnabledInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.eq("enabled", true)).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getAllInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getAllInstitutionByName(String name) {
        return (List<QualificationInstitution>) sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.eq("name", name))
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public QualificationInstitution getInstitutionById(Integer id) {
        return (QualificationInstitution) sessionFactory.getCurrentSession().get(QualificationInstitution.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(String domicileCode, String term) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(QualificationInstitution.class)
                .add(Restrictions.and(
                        Restrictions.eq("enabled", true),
                        Restrictions.and(
                                Restrictions.eq("domicileCode", domicileCode),
                                Restrictions.or(Restrictions.ilike("name", term, MatchMode.END),
                                        Restrictions.ilike("name", term, MatchMode.START)))))
                .addOrder(Order.asc("name")).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitiveOnlyValidReferenceData(String domicileCode, String term) {        
        DetachedCriteria qReferenceInstitutionCriteria = DetachedCriteria.forClass(QualificationInstitutionReference.class, "qr");
        qReferenceInstitutionCriteria.setProjection(Projections.property("qr.code"));
        qReferenceInstitutionCriteria.add(Restrictions.eq("enabled", true));

        DetachedCriteria qInstitutionsCriteria = DetachedCriteria.forClass(QualificationInstitution.class, "q");
        qInstitutionsCriteria.add(
                Restrictions.and(
                        Restrictions.eq("domicileCode", domicileCode),
                        Restrictions.or(Restrictions.ilike("name", term, MatchMode.END),
                                Restrictions.ilike("name", term, MatchMode.START)))).addOrder(Order.asc("name"));
        qInstitutionsCriteria.add(Property.forName("q.code").in(qReferenceInstitutionCriteria));

        return qInstitutionsCriteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list(); 
    }
}
