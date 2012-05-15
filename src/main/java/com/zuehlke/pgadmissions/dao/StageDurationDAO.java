package com.zuehlke.pgadmissions.dao;


import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	
	public void save(StageDuration stageDuration) {
		sessionFactory.getCurrentSession().saveOrUpdate(stageDuration);
	}
	
//	public StageDuration getByStatus(ApplicationFormStatus status) {
//		StageDuration stageDuration = new StageDuration();
//		stageDuration.setStage(status);
//		if( status == ApplicationFormStatus.VALIDATION){
//			stageDuration.setDuration(7);
//		}
//		if( status == ApplicationFormStatus.REVIEW){
//			stageDuration.setDuration(14);
//		}
//		return stageDuration;
//	}
	
	public StageDuration getByStatus(ApplicationFormStatus stage) {
		return (StageDuration)sessionFactory.getCurrentSession().createCriteria(StageDuration.class)
				.add(Restrictions.eq("stage", stage))
				.uniqueResult();

	}


}
