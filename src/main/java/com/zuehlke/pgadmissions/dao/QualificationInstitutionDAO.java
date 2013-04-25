package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
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

    /**
     * @deprecated Use {@link #getEnableAndValiddInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(String, String)} instead.
     */
    @Deprecated
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
    
    /**
     * Returns all the institutions matching the given domicile code which title contains the provided term. 
     * Additionally, this method filters any institutions out which are not part of the reference data 
     * provided by PORTICO.
     * @param domicileCode the domicile code of the institution
     * @param term a search term such as "Univers" which then finds all universities containing 
     * "Univers" in its name e.g "University of London", "University of Cambridge" etc.
     * @return a list of matching institutions
     * 
     * @deprecated PRISM is switching back to a drop down element rather than autosuggest.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<QualificationInstitution> getEnableAndValiddInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive(String domicileCode, String term) {        
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
    
    @SuppressWarnings("unchecked")
    public List<QualificationInstitution> getEnabledInstitutionsByCountryCode(String domicileCode) {
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.eq("enabled", true));
        conjunction.add(Restrictions.eq("domicileCode", domicileCode));
        
        return sessionFactory.getCurrentSession()
            .createCriteria(QualificationInstitution.class)
            .add(conjunction)
            .addOrder(Order.asc("name"))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }
}
