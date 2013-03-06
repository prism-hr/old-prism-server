package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Domicile;

@Repository
public class DomicileDAO {

    private final SessionFactory sessionFactory;

    public DomicileDAO() {
        this(null);
    }

    @Autowired
    public DomicileDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<Domicile> getAllDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(Domicile.class).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Domicile> getAllEnabledDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    /**
     * Because PORTICO uses the Domicile reference data for two different purposes 
     * (Institutions and Country of Domicile) we need to filter the following domicile codes for 
     * displaying institutions: {@code XF, XG, ZZ, XH, XI}.
     * @return a list of all the enabled domicile codes except domicile's with the following codes: {@code XF, XG, ZZ, XH, XI}
     */
    @SuppressWarnings("unchecked")
    public List<Domicile> getAllEnabledDomicilesExceptAlternateValues() {
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.eq("enabled", true));
        conjunction.add(Restrictions.ne("code", "XF"));
        conjunction.add(Restrictions.ne("code", "XG"));
        conjunction.add(Restrictions.ne("code", "ZZ"));
        conjunction.add(Restrictions.ne("code", "XH"));
        conjunction.add(Restrictions.ne("code", "XI"));
        
        return sessionFactory.getCurrentSession()
                .createCriteria(Domicile.class)
                .add(conjunction)
                .addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    public Domicile getDomicileById(Integer id) {
        return (Domicile) sessionFactory.getCurrentSession().get(Domicile.class, id);
    }

	public void save(Domicile domicile) {
		sessionFactory.getCurrentSession().saveOrUpdate(domicile);
	}
}
