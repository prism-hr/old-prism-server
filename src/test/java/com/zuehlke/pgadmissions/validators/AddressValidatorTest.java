package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dto.Address;



public class AddressValidatorTest {

	private AddressValidator validator;
	private Address address;

	@Test
	public void shouldSupportAddress() {
		assertTrue(validator.supports(Address.class));
	}
	
	@Test
	public void shouldAcceptAddress() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfLocationIsNull() {
		address.setAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup(){
		validator = new AddressValidator();
		
		address = new Address();
		address.setAddressContactAddress("YES");
		address.setAddressCountry(2);
		address.setAddressId(2);
		address.setAddressLocation("London");
	}
}
