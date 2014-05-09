package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;

public class DisabilityDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		DisabilityDAO disabilityDAO = new DisabilityDAO();
		Disability disability = new DisabilityBuilder().id(1).name("ZZZZZZ").code("1").enabled(true).build();
		disabilityDAO.getDisabilityById(disability.getId());
	}

	@Test
	public void shouldGetAllDisabilitiesInIDOrder() {
		BigInteger numberOfDisabilities = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from DISABILITY").uniqueResult();
		Disability disability1 = new DisabilityBuilder().name("ZZZZZZ").code("1").enabled(true).build();
		Disability disability2 = new DisabilityBuilder().name("AAAAAAAA").code("2").enabled(true).build();
		save(disability1, disability2);
		flushAndClearSession();
		DisabilityDAO disabilityDAO = new DisabilityDAO(sessionFactory);
		List<Disability> allDisability = disabilityDAO.getAllDisabilities();
		assertEquals(numberOfDisabilities.intValue() + 2, allDisability.size());

		assertEquals("ZZZZZZ", allDisability.get(numberOfDisabilities.intValue()).getName());
		assertEquals("AAAAAAAA", allDisability.get(numberOfDisabilities.intValue() + 1).getName());
	}

	@Test
	public void shouldGetDisabilityById() {
		Disability disability1 = new DisabilityBuilder().name("ZZZZZZ").code("1").enabled(true).build();
		Disability disability2 = new DisabilityBuilder().name("mmmmmm").code("2").enabled(true).build();

		save(disability1, disability2);
		flushAndClearSession();
		Integer id = disability1.getId();
		DisabilityDAO disabilityDAO = new DisabilityDAO(sessionFactory);
		Disability reloadedDisability = disabilityDAO.getDisabilityById(id);
		assertEquals(disability1.getId(), reloadedDisability.getId());
	}
	

    @Test
    public void shouldGetAllDisabilitiesEnabledOnly() {
        BigInteger numberOfDisabilities = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from DISABILITY WHERE enabled = true").uniqueResult();
        Disability disability1 = new DisabilityBuilder().name("ZZZZZZ").code("1").enabled(false).build();
        Disability disability2 = new DisabilityBuilder().name("AAAAAAAA").code("2").enabled(true).build();
        save(disability1, disability2);
        flushAndClearSession();
        DisabilityDAO disabilityDAO = new DisabilityDAO(sessionFactory);
        List<Disability> allDisability = disabilityDAO.getAllEnabledDisabilities();
        assertEquals(numberOfDisabilities.intValue() + 1, allDisability.size());
    }
}
