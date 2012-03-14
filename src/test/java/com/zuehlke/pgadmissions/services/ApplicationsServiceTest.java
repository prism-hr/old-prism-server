package com.zuehlke.pgadmissions.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;



public class ApplicationsServiceTest{

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;
	
	@Test
	public void shouldgetListOfApplicationsForApplicant(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicant(user)).andReturn(Arrays.asList(form));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertTrue(visibleApplications.contains(form));
		Assert.assertEquals(1, visibleApplications.size());
	}
	
	@Test
	public void shouldGetListOfQualificationsForApplication() throws ParseException{
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		Qualification qual = new QualificationBuilder().id(3).application(applicationForm).q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).q_grade("").q_institution("").q_language_of_study("").q_level("").q_name_of_programme("").q_score("").q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();
		EasyMock.expect(applicationFormDAOMock.getQualificationsByApplication(applicationForm)).andReturn(Arrays.asList(qual));
		EasyMock.replay(applicationFormDAOMock);
		List<Qualification> qualifications = applicationsService.getQualificationsByApplication(applicationForm);
		Assert.assertEquals(qual, qualifications.get(0));
	}
	
	@Test
	public void shouldGetListOfApplicationsForAssignedReviewer(){
		RegisteredUser reviewer = new RegisteredUserBuilder().id(2).username("tom").roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(reviewer);
		Assert.assertEquals(1, visibleApplications.size());
		Assert.assertTrue(visibleApplications.contains(underReviewForm));
	}
	
	@Test
	public void shouldNotGetListOfApplicationsForUnAssignedReviewer(){
		RegisteredUser reviewer = new RegisteredUserBuilder().id(2).username("tom").roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(reviewer);
		Assert.assertEquals(0, visibleApplications.size());
		Assert.assertFalse(visibleApplications.contains(underReviewForm));
	}
	
	@Test
	public void shouldGetMostRecentApplicationFirst() throws InterruptedException{
		ApplicationForm app1 = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).appDate(new Date()).toApplicationForm();
		Thread.sleep(1000);
		ApplicationForm app2 = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).appDate(new Date()).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicant(user)).andReturn(Arrays.asList(app1,app2));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertEquals(2, visibleApplications.size());
		Assert.assertEquals(app2, visibleApplications.get(0));
		Assert.assertEquals(app1, visibleApplications.get(1));
	}
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		
		applicationsService = new ApplicationsService(applicationFormDAOMock);
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
