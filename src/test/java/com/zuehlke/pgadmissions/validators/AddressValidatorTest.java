package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	public void shouldRejectIfStartDateLaterThanEndDate() throws ParseException {
		address.setAddressStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		address.setAddressEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2005/09/09"));
	}
	
	@Test
	public void shouldRejectIfStartDateAndEndDateAreFutureDates(){
		Date tomorrow, dayAfterTomorrow;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		tomorrow = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		dayAfterTomorrow = calendar.getTime();
		address.setAddressStartDate(tomorrow);
		address.setAddressEndDate(dayAfterTomorrow);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		validator.validate(address, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("address.startDate.future",mappingResult.getFieldError("addressStartDate").getCode());
		Assert.assertEquals("address.endDate.future",mappingResult.getFieldError("addressEndDate").getCode());
	}
	
	@Before
	public void setup() throws ParseException{
		validator = new AddressValidator();
		
		address = new Address();
		address.setAddressContactAddress("YES");
		address.setAddressCountry(2);
		address.setAddressId(2);
		address.setAddressLocation("London");
		address.setAddressPostCode("NW3445");
		address.setAddressPurpose(AddressPurpose.EDUCATION);
		address.setAddressStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"));
		address.setAddressEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2007/09/09"));
	}
}
