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
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class AddressSectionDTOValidatorTest {

    @Autowired  
    private Validator validator;  
    
	private AddressSectionDTOValidator addressSectionDTOValidator;
    
	private AddressSectionDTO address;

	@Before
	public void setup() {
	    address = new AddressSectionDTO();
	    address.setApplication(new ApplicationFormBuilder().id(8).build());
	    address.setContactAddress1("London");
	    address.setContactAddress3("Londo3n");
	    address.setContactAddressCountry(new Country());
	    address.setCurrentAddress1("New York");
	    address.setCurrentAddress3("New York3");
	    address.setCurrentAddressCountry(new Country());
	    
	    addressSectionDTOValidator = new AddressSectionDTOValidator();
	    addressSectionDTOValidator.setValidator((javax.validation.Validator) validator);
	}
	
	@Test
	public void shouldSupportAddress() {
		assertTrue(addressSectionDTOValidator.supports(AddressSectionDTO.class));
	}
	
	@Test
	public void shouldAcceptAddress() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentLocationIsNull() {
		address.setCurrentAddress1(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactLocationIsNull() {
		address.setContactAddress1(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactCountryIsNull() {
		address.setContactAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentCountryIsNull() {
		address.setCurrentAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCurrentAddressTooLong() {
		StringBuilder currentAddressLoc = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			currentAddressLoc.append("a");
		}
		address.setCurrentAddress1(currentAddressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfContactAddressTooLong() {
		StringBuilder contactAddressLoc = new StringBuilder();
		for (int i = 0; i <=2000; i++) {
			contactAddressLoc.append("a");
		}
		address.setContactAddress1(contactAddressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(address, "address");
		addressSectionDTOValidator.validate(address, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
}
