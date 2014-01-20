package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

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

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }

    public Program getProgram(Advert advert) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("advert", advert));
        return (Program) criteria.uniqueResult();
    }
    
    public Project getProject(Advert advert) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Project.class).add(Restrictions.eq("advert", advert));
        return (Project) criteria.uniqueResult();
    }

    public Advert getAdvertById(int advertId) {
        return (Advert) sessionFactory.getCurrentSession().get(Advert.class, advertId);
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getActiveProgramAdverts() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class).add(Restrictions.eq("active", true));
        return criteria.list();
    }

	public void delete(Advert advert) {
		if(advert == null || advert.getId() == null){
			return;
		}
		 sessionFactory.getCurrentSession().delete(advert);
	}

}