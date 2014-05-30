package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;

public class OpportunityRequestTest {

	@Test
	public void testComputeStudyDuration() {
		OpportunityRequest request = new OpportunityRequestBuilder().studyDurationNumber(3).studyDurationUnit("MONTHS").build();
		
		assertEquals(3, request.getStudyDuration().intValue());

		request.setStudyDurationUnit("YEARS");
		assertEquals(36, request.getStudyDuration().intValue());

		request.setStudyDurationNumber(2);
		assertEquals(24, request.getStudyDuration().intValue());
	}
	
	@Test
	public void testComputeStudyDurationNumberAndUnit() {
		OpportunityRequest request = new OpportunityRequest();
		request.setStudyDuration(15);
		
		assertEquals(15, request.getStudyDurationNumber().intValue());
		assertEquals("MONTHS", request.getStudyDurationUnit());
		
		request.setStudyDuration(36);
		assertEquals(3, request.getStudyDurationNumber().intValue());
		assertEquals("YEARS", request.getStudyDurationUnit());
	}

}
