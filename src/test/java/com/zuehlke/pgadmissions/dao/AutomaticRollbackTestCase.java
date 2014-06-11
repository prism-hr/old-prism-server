package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

import com.zuehlke.pgadmissions.domain.builders.TestObjectProvider;
import com.zuehlke.pgadmissions.utils.ApplicationContext;

public abstract class AutomaticRollbackTestCase {

	protected SessionFactory sessionFactory;
	
	protected Transaction transaction;
	
	protected TestObjectProvider testObjectProvider;

	public AutomaticRollbackTestCase() {
		sessionFactory = (SessionFactory) ApplicationContext.getInstance().getClassPathXmlApplicationContext().getBean("sessionFactory");
		testObjectProvider = new TestObjectProvider(sessionFactory);
	}

	protected void save(List<? extends Object> domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }
	
	protected void save(Object... domainObjects) {
		for (Object domainObject : domainObjects) {
			sessionFactory.getCurrentSession().save(domainObject);
		}
	}

	@Before
	public void setup() {
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
