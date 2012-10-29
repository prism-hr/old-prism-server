package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;

public class DomicileDAOTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldGetNationalityById() {
		Domicile dom1 = new DomicileBuilder().enabled(true).name("AAAAA").code("AA").toDomicile();
		Domicile dom2 = new DomicileBuilder().enabled(true).name("BBBBB").code("BB").toDomicile();
		
		save(dom1, dom2);
		flushAndClearSession();
		
		Integer id = dom1.getId();
		
		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		Domicile domicileById = domicileDAO.getDomicileById(id);
		
		assertEquals(dom1, domicileById);
	}
	
	@Test
    public void shouldGetEnabledNationalities() {
	    BigInteger numberOfDomiciles = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from DOMICILE WHERE enabled = true").uniqueResult();
	    
        Domicile dom1 = new DomicileBuilder().enabled(true).name("AAAAA").code("AA").toDomicile();
        Domicile dom2 = new DomicileBuilder().enabled(false).name("BBBBB").code("BB").toDomicile();
        
        save(dom1, dom2);
        
        flushAndClearSession();
        
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        
        assertEquals(numberOfDomiciles.intValue() + 1, domicileDAO.getAllEnabledDomiciles().size());
    }
}
