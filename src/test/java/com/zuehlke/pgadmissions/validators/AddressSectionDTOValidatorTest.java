package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AddressSectionDTOValidatorTest {

    @Autowired
	private AddressSectionDTOValidator validator;
    
	private AddressSectionDTO address;

	@Before
	public void setup() {
	    address = new AddressSectionDTO();
	    address.setApplication(new ApplicationFormBuilder().id(8).toApplicationForm());
	    address.setContactAddressLocation("London");
	    address.setContactAddressCountry(new Country());
	    address.setCurrentAddressLocation("New York");
	    address.setCurrentAddressCountry(new Country());
	}
	
	@Test
	public void shouldSupportAddress() {
		assertTrue(validator.supports(AddressSectionDTO.class));
	}
	
	@Test
	public void shouldAcceptAddress() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentLocationIsNull() {
		address.setCurrentAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactLocationIsNull() {
		address.setContactAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactCountryIsNull() {
		address.setContactAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentCountryIsNull() {
		address.setCurrentAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentAddressTooLong() {
		StringBuilder currentAddressLoc = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			currentAddressLoc.append("a");
		}
		address.setCurrentAddressLocation(currentAddressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactAddressTooLong() {
		StringBuilder contactAddressLoc = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			contactAddressLoc.append("a");
		}
		address.setContactAddressLocation(contactAddressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
}
