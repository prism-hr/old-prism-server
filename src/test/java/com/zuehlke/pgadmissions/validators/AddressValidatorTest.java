package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
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
	
	@Test
	public void shouldRejectIfPostcodeIsNull() {
		address.setAddressPostCode(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfPurposeIsNull() {
		address.setAddressPurpose(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfStartDateIsNull() {
		address.setAddressStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void shouldRejectIfStartDateLaterThanEndDate() {
		address.setAddressStartDate(new Date(2010,1,1));
		address.setAddressEndDate(new Date(2001,1,1));
	}
	
	@Before
	public void setup(){
		validator = new AddressValidator();
		
		address = new Address();
		address.setAddressContactAddress("YES");
		address.setAddressCountry(2);
		address.setAddressId(2);
		address.setAddressLocation("London");
		address.setAddressPostCode("NW3445");
		address.setAddressPurpose(AddressPurpose.EDUCATION);
		address.setAddressStartDate(new Date(2001,1,1));
		address.setAddressEndDate(null);
	}
}
