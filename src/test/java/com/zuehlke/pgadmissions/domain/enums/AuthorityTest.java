package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthorityTest {

	@Test
	public void shouldHaveCorrectValues(){
		Authority[] authorities = Authority.values();
		assertEquals(3, authorities.length);
		assertEquals(Authority.APPLICANT, authorities[0]);
		assertEquals(Authority.REVIEWER, authorities[1]);
		assertEquals(Authority.RECRUITER, authorities[2]);
	}
}
