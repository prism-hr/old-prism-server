package com.zuehlke.pgadmissions.domain.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AuthorityTest {

	@Test
	public void shouldHaveCorrectValues(){
		Authority[] authorities = Authority.values();
		assertEquals(8, authorities.length);
		assertEquals(Authority.APPLICANT, authorities[0]);
		assertEquals(Authority.REVIEWER, authorities[1]);
		assertEquals(Authority.ADMINISTRATOR, authorities[2]);
		assertEquals(Authority.APPROVER, authorities[3]);
		assertEquals(Authority.SUPERADMINISTRATOR, authorities[4]);
		assertEquals(Authority.REFEREE, authorities[5]);
		assertEquals(Authority.INTERVIEWER, authorities[6]);
		assertEquals(Authority.SUPERVISOR, authorities[7]);
	}
}
