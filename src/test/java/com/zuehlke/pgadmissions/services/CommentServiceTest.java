package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Comment;

public class CommentServiceTest {

	private CommentDAO commentDAOMock;
	private CommentService service;

	@Test
	public void shouldGetReviewById() {
		Comment comment = EasyMock.createMock(Comment.class);
		EasyMock.expect(commentDAOMock.get(23)).andReturn(comment);
		EasyMock.replay(comment, commentDAOMock);

		Assert.assertEquals(comment, service.getReviewById(23));
	}

	@Test
	public void shouldDelegateCommentSaveToDAO() {
		Comment appReview = EasyMock.createMock(Comment.class);
		commentDAOMock.save(appReview);
		EasyMock.replay(appReview, commentDAOMock);
		service.save(appReview);
		EasyMock.verify(commentDAOMock);
	}

	@Before
	public void setUp() {
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		service = new CommentService(commentDAOMock);
	}
}
