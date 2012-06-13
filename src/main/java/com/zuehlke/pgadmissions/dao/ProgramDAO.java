package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;

@Repository
public class ProgramDAO {

	
	private final SessionFactory sessionFactory;

	ProgramDAO() {
		this(null);
	}

	@Autowired
	public ProgramDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	@SuppressWarnings("unchecked")
	public List<Program> getAllPrograms() {
		return sessionFactory.getCurrentSession().createCriteria(Program.class).list();
	
	}

	public Program getProgramById(Integer programId) {
		return (Program) sessionFactory.getCurrentSession().get(Program.class, programId);
	}

	public void save(Program program) {
		sessionFactory.getCurrentSession().saveOrUpdate(program);
	}
}
