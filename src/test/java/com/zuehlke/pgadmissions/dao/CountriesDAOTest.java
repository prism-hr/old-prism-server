package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;


public class CountriesDAOTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldGetAllCountries() {
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Assert.assertEquals(243, countriesDAO.getAllCountries().size());
		
	}
}
