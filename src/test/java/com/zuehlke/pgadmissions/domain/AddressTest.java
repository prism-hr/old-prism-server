package com.zuehlke.pgadmissions.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;

public class AddressTest {
	
	@Test
	public void shouldCreateNewAddress() throws ParseException{
		ApplicationForm application = new ApplicationFormBuilder().id(1).toApplicationForm();
		Address address = new AddressBuilder().id(1).application(application).contactAddress(AddressStatus.NO).country(new Country())
				.endDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).location("UK").postCode("WC1").purpose(AddressPurpose.EDUCATION)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).toAddress();
		Assert.assertNotNull(address.getLocation());
		Assert.assertNotNull(address.getPostCode());
		Assert.assertNotNull(address.getApplication());
		Assert.assertNotNull(address.getContactAddress());
		Assert.assertNotNull(address.getCountry());
		Assert.assertNotNull(address.getEndDate());
		Assert.assertNotNull(address.getPurpose());
		Assert.assertNotNull(address.getId());
		Assert.assertNotNull(address.getStartDate());
	}

}
