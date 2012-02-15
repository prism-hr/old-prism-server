package com.zuehlke.pgadmissions.dao.mappings;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zuehlke.pgadmissions.domain.DomainObject;


public abstract class AutomaticRollbackTestCase {

	protected SessionFactory sessionFactory;
	protected Transaction transaction;

	public AutomaticRollbackTestCase() {
		sessionFactory = (SessionFactory) new ClassPathXmlApplicationContext("hibernateTestConfig.xml").getBean("sessionFactory");
	}

	@SuppressWarnings("rawtypes")
	protected void save(DomainObject... domainObjects) {
		for (DomainObject domainObject : domainObjects) {
			sessionFactory.getCurrentSession().save(domainObject);
		}
	}

	@Before
	public void setUp() {
		transaction = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() {
		transaction.rollback();
	}

	protected void flushAndClearSession() {
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}
}
