package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;

public class EthnicityDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		EthnicityDAO ethnicityDAO = new EthnicityDAO();
		Ethnicity ethnicity = new EthnicityBuilder().id(1).name("ZZZZZZ").toEthnicity();
		ethnicityDAO.getEthnicityById(ethnicity.getId());
	}

	@Test
	public void shouldGetAllEthnicitiesInAlhphabeticalOrder() {
		BigInteger numberOfEthnicities = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from ETHNICITY").uniqueResult();
		Ethnicity ethnicity1 = new EthnicityBuilder().name("ZZZZZZ").toEthnicity();
		Ethnicity ethnicity2 = new EthnicityBuilder().name("AAAAAAAA").toEthnicity();
		save(ethnicity1, ethnicity2);
		flushAndClearSession();
		EthnicityDAO ethnicityDAO = new EthnicityDAO(sessionFactory);
		List<Ethnicity> allEthnicity = ethnicityDAO.getAllEthnicities();
		assertEquals(numberOfEthnicities.intValue() + 2, allEthnicity.size());

		assertEquals("AAAAAAAA", allEthnicity.get(0).getName());

		assertEquals("ZZZZZZ", allEthnicity.get(numberOfEthnicities.intValue() + 1).getName());
	}

	@Test
	public void shouldGetEthnicityById() {
		Ethnicity ethnicity1 = new EthnicityBuilder().name("ZZZZZZ").toEthnicity();
		Ethnicity ethnicity2 = new EthnicityBuilder().name("mmmmmm").toEthnicity();

		save(ethnicity1, ethnicity2);
		flushAndClearSession();
		Integer id = ethnicity1.getId();
		EthnicityDAO ethnicityDAO = new EthnicityDAO(sessionFactory);
		Ethnicity reloadedEthnicity = ethnicityDAO.getEthnicityById(id);
		assertEquals(ethnicity1, reloadedEthnicity);
	}
}
