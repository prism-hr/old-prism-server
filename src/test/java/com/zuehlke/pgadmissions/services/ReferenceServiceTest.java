package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ReferenceDAO;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;

public class ReferenceServiceTest {

	private ReferenceDAO referenceDAOMock;
	private ReferenceService referenceService;

	@Test
	public void shouldGetReferenceFromDAO() {
		ReferenceComment reference = new ReferenceCommentBuilder().id(1).build();
		EasyMock.expect(referenceDAOMock.getReferenceById(1)).andReturn(reference);
		EasyMock.replay(referenceDAOMock);
		ReferenceComment returnedReference = referenceService.getReferenceById(1);
		assertEquals(reference, returnedReference);
	}

	@Before
	public void setup() {
		referenceDAOMock = EasyMock.createMock(ReferenceDAO.class);
		referenceService = new ReferenceService(referenceDAOMock);
	}
}
