package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;

public class OpportunityRequestTest {

	@Test
	public void testComputeStudyDuration() {
		OpportunityRequest request = new OpportunityRequestBuilder().studyDurationNumber(3).studyDurationUnit("MONTHS").build();
		
		request.computeStudyDuration();
		assertEquals(3, request.getStudyDuration().intValue());

		request.setStudyDurationUnit("YEARS");
		request.computeStudyDuration();
		assertEquals(36, request.getStudyDuration().intValue());
	}
	
	@Test
	public void testComputeStudyDurationNumberAndUnit() {
		OpportunityRequest request = new OpportunityRequestBuilder().studyDuration(15).build();
		
		request.computeStudyDurationNumberAndUnit();
		assertEquals(15, request.getStudyDurationNumber().intValue());
		assertEquals("MONTHS", request.getStudyDurationUnit());
		
		request.setStudyDuration(36);
		request.computeStudyDurationNumberAndUnit();
		assertEquals(3, request.getStudyDurationNumber().intValue());
		assertEquals("YEARS", request.getStudyDurationUnit());
	}

}
