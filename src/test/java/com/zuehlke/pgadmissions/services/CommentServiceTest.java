package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;


public class CommentServiceTest {

	private CommentDAO commentDAOMock;
	private CommentService service;
	
	@Test
	public void shouldGetCommentByApplication() {
		ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);
		Comment comment = EasyMock.createMock(Comment.class);
		EasyMock.expect(commentDAOMock.getReviewsByApplication(applicationForm)).andReturn(Arrays.asList(comment));
		EasyMock.replay(applicationForm, comment, commentDAOMock);
		
		List<Comment> commentsByApplication = service.getCommentsByApplication(applicationForm);
		Assert.assertEquals(1, commentsByApplication.size());
		Assert.assertTrue(commentsByApplication.contains(comment));
	}
	
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
	
	
	@Test
	public void shouldGetVisibleComments() {
		RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(user.isInRole(Authority.REVIEWER)).andReturn(true);
		EasyMock.expect(user.isInRole(Authority.REVIEWER)).andReturn(true);
		
		ApplicationForm form = EasyMock.createMock(ApplicationForm.class);
		
		Comment commentOne = EasyMock.createMock(Comment.class);
		Comment commentTwo = EasyMock.createMock(Comment.class);
		
		EasyMock.expect(commentOne.getUser()).andReturn(user);
		EasyMock.expect(commentTwo.getUser()).andReturn(user);
		
		EasyMock.expect(commentOne.getUser()).andReturn(user);
		EasyMock.expect(commentTwo.getUser()).andReturn(user);
		
		EasyMock.expect(commentDAOMock.getReviewsByApplication(form)).andReturn(Arrays.asList(commentOne, commentTwo));
		
		EasyMock.replay(user, form, commentOne, commentTwo, commentDAOMock);
		
		List<Comment> visibleComments = service.getVisibleComments(form, user);
		Assert.assertEquals(2, visibleComments.size());
		Assert.assertTrue(visibleComments.contains(commentOne));
		Assert.assertTrue(visibleComments.contains(commentTwo));
	}
	
	@Before
	public void setUp() {
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		service = new CommentService(commentDAOMock);
	}
}
