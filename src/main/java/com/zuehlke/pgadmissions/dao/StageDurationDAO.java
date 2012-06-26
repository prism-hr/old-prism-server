package com.zuehlke.pgadmissions.dao;


import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class StageDurationDAO {

	private final SessionFactory sessionFactory;

	StageDurationDAO() {
		this(null);
	}

	@Autowired
	public StageDurationDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public void save(StageDuration stageDuration) {
		sessionFactory.getCurrentSession().saveOrUpdate(stageDuration);
	}
	
	@Transactional
	public StageDuration getByStatus(ApplicationFormStatus stage) {
		return (StageDuration)sessionFactory.getCurrentSession().createCriteria(StageDuration.class)
				.add(Restrictions.eq("stage", stage))
				.uniqueResult();

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<StageDuration> getAllStagesDurations() {
		return (List<StageDuration>) sessionFactory.getCurrentSession().createCriteria(StageDuration.class)
				.list();
	}
	


}
