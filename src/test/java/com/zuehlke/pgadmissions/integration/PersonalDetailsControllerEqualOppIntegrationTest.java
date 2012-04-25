package com.zuehlke.pgadmissions.integration;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.controllers.PersonalDetailsController;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:integrationTestContext.xml")
public class PersonalDetailsControllerEqualOppIntegrationTest {

	@Autowired
	private PersonalDetailsController controller;

	@Autowired
	private SessionFactory sessionFactory;

	private Disability dis1;
	private Disability dis2;
	private Ethnicity eth1;
	private Ethnicity eth2;

	@Before
	public void setUp() {
		dis1 = new DisabilityBuilder().name("ABC").toDisability();
		dis2 = new DisabilityBuilder().name("XYZ").toDisability();

		sessionFactory.getCurrentSession().save(dis1);
		sessionFactory.getCurrentSession().save(dis2);

		eth1 = new EthnicityBuilder().name("ABC").toEthnicity();
		eth2 = new EthnicityBuilder().name("XYZ").toEthnicity();

		sessionFactory.getCurrentSession().save(eth1);
		sessionFactory.getCurrentSession().save(eth2);
		flush();
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void returnAllEthnicities() {
		List<Ethnicity> allEthnicities = controller.getAllEthnicities();

		Assert.assertNotNull(allEthnicities);
		Assert.assertTrue(allEthnicities.contains(eth1));
		Assert.assertTrue(allEthnicities.contains(eth2));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void returnAllDisabilities() {
		List<Disability> allEthnicities = controller.getAllDisabilities();

		Assert.assertNotNull(allEthnicities);
		Assert.assertTrue(allEthnicities.contains(dis1));
		Assert.assertTrue(allEthnicities.contains(dis2));
	}

	private void flush() {
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}
}
