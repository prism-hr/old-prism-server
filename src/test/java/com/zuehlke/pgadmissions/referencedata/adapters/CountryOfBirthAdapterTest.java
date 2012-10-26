package com.zuehlke.pgadmissions.referencedata.adapters;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;

public class CountryOfBirthAdapterTest {

    private Countries.Country country;

	@Test
    public void testCreateDomainObject() {
		CountryOfBirthAdapter countryOfBirthAdapter = new CountryOfBirthAdapter(country);
		Country domainObject = countryOfBirthAdapter.createDomainObject();		
		Assert.assertEquals(country.getCode(), domainObject.getCode());
		Assert.assertEquals(country.getName(), domainObject.getName());
		Assert.assertEquals(Boolean.TRUE, domainObject.getEnabled());
    }
    
    @Before
	public void setUp() {
    	country = new Countries.Country();
    	country.setCode("UK");
    	country.setName("country");
    }
        
}
