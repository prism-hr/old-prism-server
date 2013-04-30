package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Repository
@SuppressWarnings("unchecked")
public class EmailTemplateDAO {
	
	private final SessionFactory sessionFactory;
	
	public EmailTemplateDAO() {
		this(null);
	}
	
	@Autowired
	public EmailTemplateDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<EmailTemplate> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.list();
	}
	
	public EmailTemplate getActiveByName(EmailTemplateName name) {
		return (EmailTemplate) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).add(Restrictions.eq("active", true)).uniqueResult();
	}
	
	public void save(EmailTemplate emailTemplate) {
		sessionFactory.getCurrentSession().saveOrUpdate(emailTemplate);
	}
	
	public List<EmailTemplate> getByName(EmailTemplateName name) {
		return (List<EmailTemplate>) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).list();
	}
	
	public Map<Long, Date> getVersionsByName(EmailTemplateName name) {
		List<Object[]> templates = (List<Object[]>) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).addOrder(Order.asc("version"))
				.setProjection(Projections.projectionList()
						.add(Projections.property("id"))
						.add(Projections.property("version"))
				).list();
		Map<Long, Date> result = new HashMap<Long, Date>();
		for (Object[] template : templates) {
			result.put((Long)template[0], (Date)template[1]);
		}
		return result;
	}
	
	public EmailTemplate getDefaultByName(EmailTemplateName name) {
		return (EmailTemplate) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).addOrder(Order.asc("version")).list().get(0);
	}
	
	public EmailTemplate getLatestByName(EmailTemplateName name) {
		return (EmailTemplate) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).addOrder(Order.desc("version")).list().get(0);
	}
	
	public EmailTemplate getByNameAndVersion(EmailTemplateName name, Date version) {
		return (EmailTemplate) sessionFactory.getCurrentSession().createCriteria(EmailTemplate.class)
				.add(Restrictions.eq("name", name)).add(Restrictions.eq("version", version)).uniqueResult();
	}

	public EmailTemplate getById(Long id) {
		return (EmailTemplate) sessionFactory.getCurrentSession().get(EmailTemplate.class, id);
	}

	public void remove(Long id) {
		EmailTemplate toRemove = getById(id);
		sessionFactory.getCurrentSession().delete(toRemove);
	}

	public void remove(EmailTemplate template) {
		sessionFactory.getCurrentSession().delete(template);
	}
}
