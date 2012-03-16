package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;

public class ViewApplicationFormControllerTest {

	private ViewApplicationFormController controller;
	private RegisteredUser userMock;
	private ApplicationsService applicationsServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser admin;
	private RegisteredUser adminAndReviewer;
	private RegisteredUser reviewer, reviewer2;
	ApplicationForm submittedNonApprovedApplication;
	ApplicationForm submittedApprovedApplication;
	ApplicationForm unsubmittedApplication;
	ApplicationReview applicationReviewForSubmittedNonApproved1, applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved3,
			applicationReviewForSubmittedNonApproved4;

	private CountryService countryServiceMock;
	private LanguageService languageServiceMock;

	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getViewApplicationPage("", 1, "");

	}

	@Test
	public void shouldGetApplicationFormView() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		assertEquals("private/pgStudents/form/main_application_page", modelAndView.getViewName());
	}

	@Test
	public void shouldGetApplicationFormFromIdAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, model.getApplicationForm());
	}

	@Test
	public void shouldCreateSetCorrectAttributesOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		EasyMock.expect(userMock.getFirstName()).andReturn("bob");
		EasyMock.expect(userMock.getLastName()).andReturn("Smith");
		EasyMock.expect(userMock.getEmail()).andReturn("email@test.com");
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		applicationsServiceMock.save(applicationForm);
		List<Country> countries = Arrays.asList(new CountryBuilder().id(1).toCountry());
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countries);
		List<Language> languages = Arrays.asList(new LanguageBuilder().id(1).toLanguage());
		EasyMock.expect(languageServiceMock.getAllLanguages()).andReturn(languages);
		EasyMock.replay(userMock, applicationsServiceMock, countryServiceMock,languageServiceMock );

		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");

		assertNotNull(model.getQualification());
		assertNotNull(model.getAddress());
		assertNotNull(model.getFunding());
		assertNotNull(model.getEmploymentPosition());
		assertNotNull(model.getReferrers());
		
		assertSame(countries, model.getCountries());
		assertSame(languages, model.getLanguages());
		assertEquals(userMock, model.getUser());
		assertEquals(ResidenceStatus.values().length, model.getResidenceStatuses().size());
		assertTrue(model.getResidenceStatuses().containsAll(Arrays.asList(ResidenceStatus.values())));
		assertEquals(Gender.values().length, model.getGenders().size());
		assertTrue(model.getGenders().containsAll(Arrays.asList(Gender.values())));
		assertEquals(StudyOption.values().length, model.getStudyOptions().size());
		assertTrue(model.getStudyOptions().containsAll(Arrays.asList(StudyOption.values())));
		assertEquals(Referrer.values().length, model.getReferrers().size());
		assertTrue(model.getReferrers().containsAll(Arrays.asList(Referrer.values())));
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
		
		assertEquals(LanguageAptitude.values().length, model.getLanguageAptitudes().size());
		assertTrue(model.getLanguageAptitudes().containsAll(Arrays.asList(LanguageAptitude.values())));
		
		assertEquals(DocumentType.values().length, model.getDocumentTypes().size());
		assertTrue(model.getDocumentTypes().containsAll(Arrays.asList(DocumentType.values())));
	}

	@Test
	public void shouldNotIncludeCVandPersonalStatementDocTypeIfAlreadyUploaded() {
		Document cv= new DocumentBuilder().type(DocumentType.CV).toDocument();
		Document statement = new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).toDocument();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		applicationForm.getSupportingDocuments().addAll(Arrays.asList(cv, statement));
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		EasyMock.expect(userMock.getFirstName()).andReturn("bob");
		EasyMock.expect(userMock.getLastName()).andReturn("Smith");
		EasyMock.expect(userMock.getEmail()).andReturn("email@test.com");
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);

		List<Country> countries = Arrays.asList(new CountryBuilder().id(1).toCountry());
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countries);
		List<Language> languages = Arrays.asList(new LanguageBuilder().id(1).toLanguage());
		EasyMock.expect(languageServiceMock.getAllLanguages()).andReturn(languages);
		EasyMock.replay(userMock, applicationsServiceMock, countryServiceMock,languageServiceMock );

		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");

		
		
		assertEquals(DocumentType.values().length - 2, model.getDocumentTypes().size());
		assertFalse(model.getDocumentTypes().contains(DocumentType.CV));
		assertFalse(model.getDocumentTypes().contains(DocumentType.PERSONAL_STATEMENT));
	}

	
	
	@Test
	public void shouldAddUploadErrorCodeIfPRovided() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		EasyMock.expect(userMock.getFirstName()).andReturn("bob");
		EasyMock.expect(userMock.getLastName()).andReturn("Smith");
		EasyMock.expect(userMock.getEmail()).andReturn("email@test.com");
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);

		List<Country> countries = Arrays.asList(new CountryBuilder().id(1).toCountry());
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countries);
		List<Language> languages = Arrays.asList(new LanguageBuilder().id(1).toLanguage());
		EasyMock.expect(languageServiceMock.getAllLanguages()).andReturn(languages);
		EasyMock.replay(userMock, applicationsServiceMock, countryServiceMock,languageServiceMock );

		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"hello.world");
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");

		assertEquals("hello.world", model.getUploadErrorCode());
	}

	@Test
	public void shouldCreateEmploymentPositionAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		EasyMock.expect(userMock.getFirstName()).andReturn("bob");
		EasyMock.expect(userMock.getLastName()).andReturn("Smith");
		EasyMock.expect(userMock.getEmail()).andReturn("email@test.com");
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model.getEmploymentPosition());
	}

	@Test
	public void shouldGetCurrentUserFromSecutrityContextAndSetOnEditModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(userMock.getAuthorities()).andReturn(null);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);

		ModelAndView modelAndView = controller.getViewApplicationPage("", 1,"");
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		assertEquals(userMock, model.getUser());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfCurrentCannotSeeApplicatioForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(userMock);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		controller.getViewApplicationPage("", 1,"");
	}

	@Test
	public void shouldShowAllCommentsForAdministrator() {
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1);
		comments.add(applicationReviewForSubmittedNonApproved2);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.replay(applicationReviewServiceMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2,"");
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(2, loadedComments.size());
		assertEquals(comments, loadedComments);
		assertEquals("private/staff/application/main_application_page", modelAndView.getViewName());
	}

	@Test
	public void shouldShowAllCommentsForReviewerExceptFromOtherReviewersComments() {
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1); // admin
		comments.add(applicationReviewForSubmittedNonApproved2); // reviewer
		comments.add(applicationReviewForSubmittedNonApproved3); // reviewer2
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(
				Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2,"");
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(2, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(!loadedComments.contains(applicationReviewForSubmittedNonApproved3));

	}

	@Test
	public void shouldShowAllCommentsForUserWhoIsBothAdminAndReviewer() {
		authenticationToken.setDetails(adminAndReviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1); // admin
		comments.add(applicationReviewForSubmittedNonApproved2); // reviewer
		comments.add(applicationReviewForSubmittedNonApproved3); // reviewer2
		comments.add(applicationReviewForSubmittedNonApproved4); // adminAndReviewer
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(
				Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2,"");
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(4, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved3));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved4));
	}

	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		userMock = EasyMock.createMock(RegisteredUser.class);
		countryServiceMock = EasyMock.createMock(CountryService.class);
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationReviewServiceMock = EasyMock.createMock(ApplicationReviewService.class);
		controller = new ViewApplicationFormController(applicationsServiceMock, applicationReviewServiceMock, countryServiceMock, languageServiceMock);

		admin = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();

		reviewer = new RegisteredUserBuilder().id(3).username("jane").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewer2 = new RegisteredUserBuilder().id(3).username("john").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		adminAndReviewer = new RegisteredUserBuilder().id(6).username("fred")
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole())
				.toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		submittedNonApprovedApplication = new ApplicationFormBuilder().id(2).reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		unsubmittedApplication = new ApplicationFormBuilder().id(3).toApplicationForm();
		applicationReviewForSubmittedNonApproved1 = new ApplicationReviewBuilder().id(1).application(submittedNonApprovedApplication)
				.comment("Amazing Research !!!").user(admin).toApplicationReview();
		applicationReviewForSubmittedNonApproved2 = new ApplicationReviewBuilder().id(2).application(submittedNonApprovedApplication)
				.comment("I'm not interested").user(reviewer).toApplicationReview();
		applicationReviewForSubmittedNonApproved3 = new ApplicationReviewBuilder().id(3).application(submittedNonApprovedApplication).comment("I'm interested")
				.user(reviewer2).toApplicationReview();
		applicationReviewForSubmittedNonApproved4 = new ApplicationReviewBuilder().id(4).application(submittedNonApprovedApplication)
				.comment("Comment By Admin And Reviewer").user(adminAndReviewer).toApplicationReview();
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
