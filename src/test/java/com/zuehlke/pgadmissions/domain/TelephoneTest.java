package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;

public class TelephoneTest {
	@Test	
	public void shouldReturnCorrectjsonString(){			
		Telephone telephone = new TelephoneBuilder().telephoneType(PhoneType.MOBILE).telephoneNumber("something").toTelephone();
		assertEquals("{\"type\": \"MOBILE\", \"number\": \"something\"}", telephone.getAsJson());
	}
}
