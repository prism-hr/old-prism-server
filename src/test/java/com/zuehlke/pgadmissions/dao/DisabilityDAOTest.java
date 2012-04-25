package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;

public class DisabilityDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		DisabilityDAO disabilityDAO = new DisabilityDAO();
		Disability disability = new DisabilityBuilder().id(1).name("ZZZZZZ").toDisability();
		disabilityDAO.getDisabilityById(disability.getId());
	}

	@Test
	public void shouldGetAllDisabilitiesInAlhphabeticalOrder() {
		BigInteger numberOfDisabilities = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from disability").uniqueResult();
		Disability disability1 = new DisabilityBuilder().name("ZZZZZZ").toDisability();
		Disability disability2 = new DisabilityBuilder().name("AAAAAAAA").toDisability();
		save(disability1, disability2);
		flushAndClearSession();
		DisabilityDAO disabilityDAO = new DisabilityDAO(sessionFactory);
		List<Disability> allDisability = disabilityDAO.getAllDisabilities();
		assertEquals(numberOfDisabilities.intValue() + 2, allDisability.size());

		assertEquals("AAAAAAAA", allDisability.get(0).getName());
		assertEquals("ZZZZZZ", allDisability.get(numberOfDisabilities.intValue() + 1).getName());
	}

	@Test
	public void shouldGetDisabilityById() {
		Disability disability1 = new DisabilityBuilder().name("ZZZZZZ").toDisability();
		Disability disability2 = new DisabilityBuilder().name("mmmmmm").toDisability();

		save(disability1, disability2);
		flushAndClearSession();
		Integer id = disability1.getId();
		DisabilityDAO disabilityDAO = new DisabilityDAO(sessionFactory);
		Disability reloadedDisability = disabilityDAO.getDisabilityById(id);
		assertEquals(disability1, reloadedDisability);
	}
}
