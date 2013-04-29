package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class StageDurationDAO {

	private final SessionFactory sessionFactory;

	public StageDurationDAO() {
		this(null);
	}

	@Autowired
	public StageDurationDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void save(StageDuration stageDuration) {
		sessionFactory.getCurrentSession().saveOrUpdate(stageDuration);
	}
	
	public StageDuration getByStatus(ApplicationFormStatus stage) {
		return (StageDuration)sessionFactory.getCurrentSession().createCriteria(StageDuration.class)
				.add(Restrictions.eq("stage", stage))
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<StageDuration> getAllStagesDurations() {
        return (List<StageDuration>) sessionFactory.getCurrentSession().createCriteria(StageDuration.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
}
