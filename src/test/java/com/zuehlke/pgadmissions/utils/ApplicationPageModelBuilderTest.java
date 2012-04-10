package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;

public class ApplicationPageModelBuilderTest {

	private RegisteredUser userMock;
	private CountryService countryServiceMock;
	private LanguageService languageServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;
	private ApplicationPageModelBuilder builder;
	private UsernamePasswordAuthenticationToken authenticationToken;

	@Test
	public void shouldSetApplicationFormOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertSame(applicationForm, model.getApplicationForm());
	}

	@Test
	public void shouldSeCurrentUserOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertSame(userMock, model.getUser());
	}

	@Test
	public void shouldSetAllCoutriesOnModel() {
		List<Country> countries = Arrays.asList(new CountryBuilder().id(1).toCountry());
		EasyMock.expect(countryServiceMock.getAllCountries()).andReturn(countries);
		EasyMock.replay(countryServiceMock);
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertSame(countries, model.getCountries());
	}

	@Test
	public void shouldSetAllLanguagesOnModel() {
		List<Language> languages = Arrays.asList(new LanguageBuilder().id(1).toLanguage());
		EasyMock.expect(languageServiceMock.getAllLanguages()).andReturn(languages);
		EasyMock.replay(languageServiceMock);
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertSame(languages, model.getLanguages());
	}

	@Test
	public void shouldSetAllGendersOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(Gender.values().length, model.getGenders().size());
		assertTrue(model.getGenders().containsAll(Arrays.asList(Gender.values())));
	}

	@Test
	public void shouldSetAllStudyOptionsOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(StudyOption.values().length, model.getStudyOptions().size());
		assertTrue(model.getStudyOptions().containsAll(Arrays.asList(StudyOption.values())));
	}

	@Test
	public void shouldSetAllReferrersOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(Referrer.values().length, model.getReferrers().size());
		assertTrue(model.getReferrers().containsAll(Arrays.asList(Referrer.values())));
	}

	@Test
	public void shouldSetAllPhoneTypesOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
	}

	@Test
	public void shouldSetAllQaulificationLevelsOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(QualificationLevel.values().length, model.getQualificationLevels().size());
		assertTrue(model.getQualificationLevels().containsAll(Arrays.asList(QualificationLevel.values())));
	}

	@Test
	public void shouldSetAllFundingTypesOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(FundingType.values().length, model.getFundingTypes().size());
		assertTrue(model.getFundingTypes().containsAll(Arrays.asList(FundingType.values())));
	}

	@Test
	public void shouldSetAllDocumentTypesOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, null, null, null);
		assertEquals(DocumentType.values().length, model.getDocumentTypes().size());
		assertTrue(model.getDocumentTypes().containsAll(Arrays.asList(DocumentType.values())));
	}

	@Test
	public void shouldAddUploadErrorCode() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, "hello.world", null, null, null);
		assertEquals("hello.world", model.getUploadErrorCode());
	}

	@Test
	public void shouldSetViewOnModel() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, "bob", null, null);
		assertEquals("bob", model.getView());
	}

	@Test
	public void shouldSetErrorMessageForErrorViewParameter() {
		ApplicationPageModel model = builder.createAndPopulatePageModel(null, null, "errors", null, null);
		assertEquals("There are missing required fields on the form, please review.", model.getMessage());
	}

	@Test
	public void shouldGetNoCommentsIfCurrentUserApplicant() {
		ApplicationReview comment1 = new ApplicationReviewBuilder().id(1).toApplicationReview();
		ApplicationReview comment2 = new ApplicationReviewBuilder().id(2).toApplicationReview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		applicationForm.getApplicationComments().addAll(Arrays.asList(comment1, comment2));
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertTrue(model.getApplicationComments().isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetAllCommentsIfCurrentUserSuperadmin() {
		EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		EasyMock.expect(userMock.getAuthorities()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(userMock);
		ApplicationReview comment1 = new ApplicationReviewBuilder().id(1).toApplicationReview();
		ApplicationReview comment2 = new ApplicationReviewBuilder().id(2).toApplicationReview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		applicationForm.getApplicationComments().addAll(Arrays.asList(comment1, comment2));
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertEquals(2, model.getApplicationComments().size());
		assertTrue(model.getApplicationComments().containsAll(Arrays.asList(comment1, comment2)));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetAllCommentsIfCurrentUserAdmin() {
		EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true);
		EasyMock.expect(userMock.getAuthorities()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(userMock);
		ApplicationReview comment1 = new ApplicationReviewBuilder().id(1).toApplicationReview();
		ApplicationReview comment2 = new ApplicationReviewBuilder().id(2).toApplicationReview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		applicationForm.getApplicationComments().addAll(Arrays.asList(comment1, comment2));
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertEquals(2, model.getApplicationComments().size());
		assertTrue(model.getApplicationComments().containsAll(Arrays.asList(comment1, comment2)));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetAllCommentsIfCurrentUserApprover() {
		EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(userMock.getAuthorities()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(userMock);
		ApplicationReview comment1 = new ApplicationReviewBuilder().id(1).toApplicationReview();
		ApplicationReview comment2 = new ApplicationReviewBuilder().id(2).toApplicationReview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		applicationForm.getApplicationComments().addAll(Arrays.asList(comment1, comment2));
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertEquals(2, model.getApplicationComments().size());
		assertTrue(model.getApplicationComments().containsAll(Arrays.asList(comment1, comment2)));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetOnlyVisibleCommentsIfReviewer() {
		EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(true);
		EasyMock.expect(userMock.getAuthorities()).andReturn(Collections.EMPTY_LIST);
		ApplicationReview comment1 = new ApplicationReviewBuilder().id(1).toApplicationReview();
		ApplicationReview comment2 = new ApplicationReviewBuilder().id(2).toApplicationReview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		applicationForm.getApplicationComments().addAll(Arrays.asList(comment1, comment2));
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(applicationForm, userMock)).andReturn(Arrays.asList(comment2));
		EasyMock.replay(userMock, applicationReviewServiceMock);
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertEquals(1, model.getApplicationComments().size());
		assertTrue(model.getApplicationComments().containsAll(Arrays.asList(comment2)));

	}

	@Test
	public void shouldNotFailIfNoCurrentUser() {
		WebAuthenticationDetails webAuthenticationDetails = EasyMock.createMock(WebAuthenticationDetails.class);
		authenticationToken.setDetails(webAuthenticationDetails);

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationPageModel model = builder.createAndPopulatePageModel(applicationForm, null, null, null, null);
		assertSame(applicationForm, model.getApplicationForm());
	}

	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		countryServiceMock = EasyMock.createMock(CountryService.class);
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		applicationReviewServiceMock = EasyMock.createMock(ApplicationReviewService.class);

		builder = new ApplicationPageModelBuilder(applicationReviewServiceMock, countryServiceMock, languageServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
