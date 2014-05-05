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

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

@Repository
@SuppressWarnings("unchecked")
public class NotificationTemplateDAO {
	
	private final SessionFactory sessionFactory;
	
	public NotificationTemplateDAO() {
		this(null);
	}
	
	@Autowired
	public NotificationTemplateDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<NotificationTemplate> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.list();
	}
	
	public NotificationTemplate getActiveByName(NotificationTemplateId name) {
		return (NotificationTemplate) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.add(Restrictions.eq("name", name)).add(Restrictions.eq("active", true)).uniqueResult();
	}
	
	public void save(NotificationTemplate emailTemplate) {
		sessionFactory.getCurrentSession().saveOrUpdate(emailTemplate);
	}
	
	public List<NotificationTemplate> getByName(NotificationTemplateId name) {
		return (List<NotificationTemplate>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.add(Restrictions.eq("name", name)).list();
	}
	
	public Map<Long, Date> getVersionsByName(NotificationTemplateId name) {
		List<Object[]> templates = (List<Object[]>) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
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
	
	public NotificationTemplate getDefaultByName(NotificationTemplateId name) {
		return (NotificationTemplate) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.add(Restrictions.eq("name", name)).addOrder(Order.asc("version")).list().get(0);
	}
	
	public NotificationTemplate getLatestByName(NotificationTemplateId name) {
		return (NotificationTemplate) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.add(Restrictions.eq("name", name)).addOrder(Order.desc("version")).list().get(0);
	}
	
	public NotificationTemplate getByNameAndVersion(NotificationTemplateId name, Date version) {
		return (NotificationTemplate) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class)
				.add(Restrictions.eq("name", name)).add(Restrictions.eq("version", version)).uniqueResult();
	}

	public NotificationTemplate getById(Long id) {
		return (NotificationTemplate) sessionFactory.getCurrentSession().get(NotificationTemplate.class, id);
	}

	public void remove(Long id) {
		NotificationTemplate toRemove = getById(id);
		sessionFactory.getCurrentSession().delete(toRemove);
	}

	public void remove(NotificationTemplate template) {
		sessionFactory.getCurrentSession().delete(template);
	}
}
