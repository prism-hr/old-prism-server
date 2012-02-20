package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;

@Repository
public class ProjectDAO {

	private final SessionFactory sessionFactory;

	ProjectDAO(){
		this(null);
	}
	@Autowired
	public ProjectDAO(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
	
	public List<Project> getAllProjects() {
		@SuppressWarnings("unchecked")
		List<Project> list = sessionFactory.getCurrentSession()
				.createCriteria(Project.class).list();
		return list;
		
	}

}
