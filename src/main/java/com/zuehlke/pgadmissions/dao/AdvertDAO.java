package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;

@Repository
public class AdvertDAO {

    private final SessionFactory sessionFactory;

    public AdvertDAO() {
        this(null);
    }

    @Autowired
    public AdvertDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Advert programAdvert) {
        sessionFactory.getCurrentSession().saveOrUpdate(programAdvert);
    }

    public Advert getProgramAdvert(Program program) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("isProgramAdvert", true));
        return (Advert) criteria.uniqueResult();
    }

    public void merge(Advert programAdvert) {
        sessionFactory.getCurrentSession().merge(programAdvert);
    }

	@SuppressWarnings("unchecked")
	public List<Advert> getActiveProgramAdverts() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class).add(Restrictions.eq("active", true))
				 .add(Restrictions.eq("isProgramAdvert", true));
		return criteria.list();
	}


}
