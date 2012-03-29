package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationReviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;


public class ApplicationReviewServiceTest {

	private ApplicationReviewDAO applicationReviewDAOMock;
	private ApplicationReviewService service;
	
	@Test
	public void shouldGetApplicationReviewByApplication() {
		ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);
		ApplicationReview applicationReview = EasyMock.createMock(ApplicationReview.class);
		EasyMock.expect(applicationReviewDAOMock.getReviewsByApplication(applicationForm)).andReturn(Arrays.asList(applicationReview));
		EasyMock.replay(applicationForm, applicationReview, applicationReviewDAOMock);
		
		List<ApplicationReview> applicationReviewsByApplication = service.getApplicationReviewsByApplication(applicationForm);
		Assert.assertEquals(1, applicationReviewsByApplication.size());
		Assert.assertTrue(applicationReviewsByApplication.contains(applicationReview));
	}
	
	@Test
	public void shouldGetReviewById() {
		ApplicationReview applicationReview = EasyMock.createMock(ApplicationReview.class);
		EasyMock.expect(applicationReviewDAOMock.get(23)).andReturn(applicationReview);
		EasyMock.replay(applicationReview, applicationReviewDAOMock);
		
		Assert.assertEquals(applicationReview, service.getReviewById(23));
	}
	
	@Test
	public void shouldDelegateApplicationReviewSaveToDAO() {
		ApplicationReview appReview = EasyMock.createMock(ApplicationReview.class);
		applicationReviewDAOMock.save(appReview);
		EasyMock.replay(appReview, applicationReviewDAOMock);
		service.save(appReview);
		EasyMock.verify(applicationReviewDAOMock);
	}
	
	@Test
	public void shouldDelegateUserSaveToDAO() {
		RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
		applicationReviewDAOMock.saveUser(user);
		EasyMock.replay(user, applicationReviewDAOMock);
		service.saveUser(user);
		EasyMock.verify(applicationReviewDAOMock);
	}
	
	@Test
	public void shouldGetVisibleComments() {
		RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(user.isInRole(Authority.REVIEWER)).andReturn(true);
		EasyMock.expect(user.isInRole(Authority.REVIEWER)).andReturn(true);
		
		ApplicationForm form = EasyMock.createMock(ApplicationForm.class);
		
		ApplicationReview commentOne = EasyMock.createMock(ApplicationReview.class);
		ApplicationReview commentTwo = EasyMock.createMock(ApplicationReview.class);
		
		EasyMock.expect(commentOne.getUser()).andReturn(user);
		EasyMock.expect(commentTwo.getUser()).andReturn(user);
		
		EasyMock.expect(commentOne.getUser()).andReturn(user);
		EasyMock.expect(commentTwo.getUser()).andReturn(user);
		
		EasyMock.expect(applicationReviewDAOMock.getReviewsByApplication(form)).andReturn(Arrays.asList(commentOne, commentTwo));
		
		EasyMock.replay(user, form, commentOne, commentTwo, applicationReviewDAOMock);
		
		List<ApplicationReview> visibleComments = service.getVisibleComments(form, user);
		Assert.assertEquals(2, visibleComments.size());
		Assert.assertTrue(visibleComments.contains(commentOne));
		Assert.assertTrue(visibleComments.contains(commentTwo));
	}
	
	@Before
	public void setUp() {
		applicationReviewDAOMock = EasyMock.createMock(ApplicationReviewDAO.class);
		service = new ApplicationReviewService(applicationReviewDAOMock);
	}
}
