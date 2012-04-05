package com.zuehlke.pgadmissions.domain;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

public class AddressTest {
	
	@Test
	public void shouldCreateNewAddress(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).toApplicationForm();
		Address address = new AddressBuilder().id(1).application(application).country(new Country())
				.location("UK").toAddress();
		Assert.assertNotNull(address.getLocation());
		Assert.assertNotNull(address.getApplication());
		Assert.assertNotNull(address.getCountry());
		Assert.assertNotNull(address.getId());
	}

}
