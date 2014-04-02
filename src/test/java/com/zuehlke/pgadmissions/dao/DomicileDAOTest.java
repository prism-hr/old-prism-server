package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;

public class DomicileDAOTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldGetNationalityById() {
		Domicile dom1 = new DomicileBuilder().enabled(true).name("AAAAA").code("AA").build();
		Domicile dom2 = new DomicileBuilder().enabled(true).name("BBBBB").code("BB").build();
		
		save(dom1, dom2);
		flushAndClearSession();
		
		Integer id = dom1.getId();
		
		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		Domicile domicileById = domicileDAO.getDomicileById(id);
		
		assertEquals(dom1.getId(), domicileById.getId());
	}
	
	@Test
    public void shouldGetEnabledNationalities() {
	    BigInteger numberOfDomiciles = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from DOMICILE WHERE enabled = true").uniqueResult();
	    
        Domicile dom1 = new DomicileBuilder().enabled(true).name("AAAAA").code("AA").build();
        Domicile dom2 = new DomicileBuilder().enabled(false).name("BBBBB").code("BB").build();
        
        save(dom1, dom2);
        
        flushAndClearSession();
        
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        
        assertEquals(numberOfDomiciles.intValue() + 1, domicileDAO.getAllEnabledDomiciles().size());
    }
	
	@Test
	public void shouldReturnEnabledDomicilesWithoutAlternateValues() {
	    DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
	    List<Domicile> domiciles = domicileDAO.getAllEnabledDomiciles();
	    for (Domicile domicile : domiciles) {
	        Assert.assertTrue("The list contains a disabled domicile.", domicile.getEnabled());
	        Assert.assertFalse("The list contains an alternate code.", StringUtils.equalsIgnoreCase(domicile.getCode(), "XF"));
	        Assert.assertFalse("The list contains an alternate code.", StringUtils.equalsIgnoreCase(domicile.getCode(), "XG"));
	        Assert.assertFalse("The list contains an alternate code.", StringUtils.equalsIgnoreCase(domicile.getCode(), "ZZ"));
	        Assert.assertFalse("The list contains an alternate code.", StringUtils.equalsIgnoreCase(domicile.getCode(), "XH"));
	        Assert.assertFalse("The list contains an alternate code.", StringUtils.equalsIgnoreCase(domicile.getCode(), "XI"));
	    }
	}
}
