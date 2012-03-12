package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
	public List<Language> getAllLanguages() {
		return (List<Language>) sessionFactory.getCurrentSession().createCriteria(Language.class).addOrder(Order.asc("name")).list();
	
	}

	public Language getLanguageById(Integer id) {
		return (Language) sessionFactory.getCurrentSession().get(Language.class, id);
	}

}
