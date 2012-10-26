package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Language;

@Repository
public class LanguageDAO {

	private final SessionFactory sessionFactory;

	LanguageDAO() {
		this(null);
	}

	@Autowired
	public LanguageDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	@SuppressWarnings("unchecked")
    public List<Language> getAllEnabledLanguages() {
        return sessionFactory.getCurrentSession().createCriteria(Language.class).add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
	
	@SuppressWarnings("unchecked")
	public List<Language> getAllLanguages() {
        return sessionFactory.getCurrentSession().createCriteria(Language.class).addOrder(Order.asc("name"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public Language getLanguageById(Integer id) {
		return (Language) sessionFactory.getCurrentSession().get(Language.class, id);
	}

	public void save(Language language) {
		sessionFactory.getCurrentSession().saveOrUpdate(language);
	}

}
