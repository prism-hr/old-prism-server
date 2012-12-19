package com.zuehlke.pgadmissions.dao.mappings;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

import com.zuehlke.pgadmissions.utils.ApplicationContext;

public abstract class AutomaticRollbackTestCase {

	protected SessionFactory sessionFactory;
	protected Transaction transaction;

	public AutomaticRollbackTestCase() {
		sessionFactory = (SessionFactory) ApplicationContext.getInstance().getClassPathXmlApplicationContext().getBean("sessionFactory");
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
