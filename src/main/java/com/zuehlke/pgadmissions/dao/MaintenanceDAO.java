package com.zuehlke.pgadmissions.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.PrismMaintenanceTask;

@Repository
public class MaintenanceDAO {

	@Inject
	private SessionFactory sessionFactory;

	public void setTaskCompleted(PrismMaintenanceTask prismMaintenanceTask) {
		sessionFactory.getCurrentSession().createQuery( //
		        "delete from MaintenanceTask " //
		                + "where id = :id") //
		        .setParameter("id", prismMaintenanceTask) //
		        .executeUpdate();
	}

}
