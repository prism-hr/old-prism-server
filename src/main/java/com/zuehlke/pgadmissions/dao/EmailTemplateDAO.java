package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Repository
public class EmailTemplateDAO {
	
	private final SessionFactory sessionFactory;
	
	public EmailTemplateDAO() {
		this(null);
	}
	
	@Autowired
	public EmailTemplateDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<EmailTemplate> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.list();
	}
	
	public void save(EmailTemplate emailTemplate) {
		sessionFactory.getCurrentSession().saveOrUpdate(emailTemplate);
	}
	
	public EmailTemplate getByName(EmailTemplateName name) {
		return (EmailTemplate) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
	}

}
