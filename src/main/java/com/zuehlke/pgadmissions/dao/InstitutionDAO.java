package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    private SessionFactory sessionFactory;

    public InstitutionDAO() {
    }

    @Autowired
    public InstitutionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Institution> getEnabledByDomicile(InstitutionDomicile domicile) {
        return (List<Institution>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .addOrder(Order.asc("name")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutionsByDomicile(Domicile domicile) {
        return (List<ImportedInstitution>) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .addOrder(Order.asc("name")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Institution getByCode(String institutionCode) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("code", institutionCode)) //
                .uniqueResult();
    }

    public Institution getByDomicileAndName(InstitutionDomicile domicile, String institutionName) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class).add(Restrictions.eq("domicile", domicile))
                .add(Restrictions.eq("name", institutionName)).uniqueResult();
    }

    public Institution getLastCustomInstitution() {
        DetachedCriteria maxCustomCode = DetachedCriteria.forClass(Institution.class).setProjection(Projections.max("code"))
                .add(Restrictions.like("code", "CUST%"));
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Property.forName("code") //
                        .eq(maxCustomCode)).uniqueResult();
    }

    public void save(Institution institution) {
        sessionFactory.getCurrentSession().saveOrUpdate(institution);
    }

}
