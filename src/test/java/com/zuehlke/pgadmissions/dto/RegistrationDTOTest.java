package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.*;

import org.junit.Test;

public class RegistrationDTOTest {

	@Test
	public void shouldBeEqualIfAllFieldEqual(){
		RegistrationDTO recordDTO1 = new RegistrationDTO();
		recordDTO1.setFirstname("Mark");
		recordDTO1.setLastname("Euston");
		recordDTO1.setEmail("meuston@gmail.com");
		recordDTO1.setPassword("1234");
		recordDTO1.setConfirmPassword("1234");
		
		RegistrationDTO recordDTO2 = new RegistrationDTO();
		recordDTO2.setFirstname("Mark");
		recordDTO2.setLastname("Euston");
		recordDTO2.setEmail("meuston@gmail.com");
		recordDTO2.setPassword("1234");
		recordDTO2.setConfirmPassword("1234");
		assertEquals(recordDTO1, recordDTO2);
		
		RegistrationDTO recordDTO3 = new RegistrationDTO();
		recordDTO3.setFirstname("bob");
		recordDTO3.setLastname("Euston");
		recordDTO3.setEmail("meuston@gmail.com");
		recordDTO3.setPassword("1234");
		recordDTO3.setConfirmPassword("1234");
		assertFalse(recordDTO1.equals(recordDTO3));
	}
}
