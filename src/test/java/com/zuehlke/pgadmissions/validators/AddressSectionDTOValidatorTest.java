package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;



public class AddressSectionDTOValidatorTest {

	private AddressSectionDTOValidator validator;
	private AddressSectionDTO address;

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
	
	@Before
	public void setup(){
		validator = new AddressSectionDTOValidator();
		
		address = new AddressSectionDTO();
		address.setContactAddressLocation("London");
		address.setContactAddressCountry(new Country());
		address.setCurrentAddressLocation("New York");
		address.setCurrentAddressCountry(new Country());
	}
}
