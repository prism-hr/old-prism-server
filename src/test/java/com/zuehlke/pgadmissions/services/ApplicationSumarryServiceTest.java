package com.zuehlke.pgadmissions.services;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class ApplicationSumarryServiceTest {
    
    private static final String PERSONAL_STATEMENT_FILE_NAME = "MyPersonalStatement.pdf";

    private static final String FUNDING_DESCRIPTION = "borsa crivelli";

    private static final String MOST_RECENT_QUALIFICATION_TITLE = "Laurea in cura del cane e del gatto";

    private static final String APPLICANT_PHONE_NUMBER = "+393407965218";

    private static final Title APPLICANT_TITLE = Title.LORD;

    private static final boolean ATTENTION_IS_REQUIRED = true;

    private static final Long NUMBER_OF_APPLICATIONS = 3L;

    private static final int CURRENT_USER_ID = 2;

    private static final int APPLICANT_ID = 1;

    private static final String SAMPLE_APPLICATION_NUMBER = "TMRSECSING01-2013-000004";

    private static final String CURRENT_USER_EMAIL_ADDRESS = "admin1@mail.com";

    private static final String APPLICANT_SURNAME = "Capatonda";

    private static final String APPLICANT_NAME = "Maccio";

    private static final String APPLICANT_EMAIL_ADDRESS = "capatonda@mail.com";

    private static final String CV_FILE_NAME = "MyCV.pdf";

    private static Date DATE_OF_LAST_UPDATE;
    
    private static Date DATE_OF_SUBMISSION;
    
    private RegisteredUser currentUser;
    
    private ApplicationActionsDefinition actionsDefinitionMock;
    
    private UserService userServiceMock;
    
    private EncryptionHelper encryptionHelperMock;
    
    private ApplicationsService applicationsServiceMock;
    
    private ApplicationSummaryService service;
    
    @Before
    public void setup() {
        DATE_OF_SUBMISSION = (new DateTime(2013, 4, 23, 9, 20)).toDate();
        DATE_OF_LAST_UPDATE = (new DateTime(2013, 4, 20, 9, 20)).toDate();
        currentUser = getCurrentUser();
        actionsDefinitionMock = createMock(ApplicationActionsDefinition.class);
        userServiceMock = createMock(UserService.class);
        encryptionHelperMock = createMock(EncryptionHelper.class);
        applicationsServiceMock = createMock(ApplicationsService.class);
        service = new ApplicationSummaryService(applicationsServiceMock, userServiceMock, encryptionHelperMock);
    }
    
    @Test
    public void shouldReturnEmptySummaryIfApplicationIsWithdrawn() {
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.WITHDRAWN);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
            .andReturn(form);
        
        replay(applicationsServiceMock);
        assertTrue(service.getSummary("APP").isEmpty());
        verify(applicationsServiceMock);
    }
    
    @Test
    public void shouldReturnEmptySummaryIfApplicationIsUnsubmitted() {
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.UNSUBMITTED);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        replay(applicationsServiceMock);
        assertTrue(service.getSummary("APP").isEmpty());
        verify(applicationsServiceMock);
    }
    
    @Test
    public void shouldReturnSummary() {
        ApplicationForm form = getSampleApplicationForm();
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        expect(userServiceMock.getCurrentUser())
        .andReturn(currentUser);
        
        expect(applicationsServiceMock.getActionsDefinition(currentUser, form))
        .andReturn(actionsDefinitionMock);
        
        expect(actionsDefinitionMock.isRequiresAttention())
        .andReturn(ATTENTION_IS_REQUIRED);
        
        expect(userServiceMock.getNumberOfActiveApplicationsForApplicant(form.getApplicant()))
        .andReturn(NUMBER_OF_APPLICATIONS);
        
        expect(encryptionHelperMock.encrypt(form.getPersonalStatement().getId()))
        .andReturn("XYZ");
        expect(encryptionHelperMock.encrypt(form.getCv().getId()))
        .andReturn("XYZ");
        
        
        replay(userServiceMock, encryptionHelperMock, actionsDefinitionMock, applicationsServiceMock);
        Map<String, String> result = service.getSummary("APP");
        verify(userServiceMock, actionsDefinitionMock, encryptionHelperMock, applicationsServiceMock);
        
        assertFalse(result.isEmpty());
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        assertEquals(dateFormat.format(DATE_OF_SUBMISSION), result.get("applicationSubmissionDate"));
        assertEquals(dateFormat.format(DATE_OF_LAST_UPDATE), result.get("applicationUpdateDate"));
        assertEquals("true", result.get("requiresAttention"));
        assertEquals(NUMBER_OF_APPLICATIONS.toString(), result.get("numberOfActiveApplications"));
        assertEquals("XYZ", result.get("personalStatementId"));
        assertEquals(PERSONAL_STATEMENT_FILE_NAME, result.get("personalStatementFilename"));
        assertEquals("true", result.get("cvProvided"));
        assertEquals("XYZ", result.get("cvId"));
        assertEquals(CV_FILE_NAME, result.get("cvFilename"));
        assertEquals("1", result.get("numberOfReferences"));
        
        String expectedApplicantJson = "{\"title\":\""+APPLICANT_TITLE.getDisplayValue()+"\",\"phoneNumber\":\""+APPLICANT_PHONE_NUMBER+"\",\"fundingRequirements\":"+
                "\"[\\\""+FUNDING_DESCRIPTION+"\\\"]\",\"email\":\""+APPLICANT_EMAIL_ADDRESS+"\",\"applicationStatus\":\"Approved\",\"name\":" +
                "\""+APPLICANT_NAME+" "+APPLICANT_SURNAME+"\",\"mostRecentQualification\":\""+MOST_RECENT_QUALIFICATION_TITLE+"\",\"mostRecentEmployment\":" +
                		"\"Shortcuts production\"}";
        assertEquals(expectedApplicantJson, result.get("applicant"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnSummaryWithNoQualificationProvided() {
        ApplicationForm form = getSampleApplicationForm();
        
        form.setQualifications(Collections.EMPTY_LIST);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        expect(userServiceMock.getCurrentUser())
        .andReturn(currentUser);
        
        expect(applicationsServiceMock.getActionsDefinition(currentUser, form))
        .andReturn(actionsDefinitionMock);
        
        expect(actionsDefinitionMock.isRequiresAttention())
        .andReturn(ATTENTION_IS_REQUIRED);
        
        expect(userServiceMock.getNumberOfActiveApplicationsForApplicant(form.getApplicant()))
        .andReturn(NUMBER_OF_APPLICATIONS);
        
        expect(encryptionHelperMock.encrypt(form.getPersonalStatement().getId()))
        .andReturn("XYZ");
        expect(encryptionHelperMock.encrypt(form.getCv().getId()))
        .andReturn("XYZ");
        
        
        replay(userServiceMock, encryptionHelperMock, actionsDefinitionMock, applicationsServiceMock);
        Map<String, String> result = service.getSummary("APP");
        verify(userServiceMock, actionsDefinitionMock, encryptionHelperMock, applicationsServiceMock);
        
        assertFalse(result.isEmpty());
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        assertEquals(dateFormat.format(DATE_OF_SUBMISSION), result.get("applicationSubmissionDate"));
        assertEquals(dateFormat.format(DATE_OF_LAST_UPDATE), result.get("applicationUpdateDate"));
        assertEquals("true", result.get("requiresAttention"));
        assertEquals(NUMBER_OF_APPLICATIONS.toString(), result.get("numberOfActiveApplications"));
        assertEquals("XYZ", result.get("personalStatementId"));
        assertEquals(PERSONAL_STATEMENT_FILE_NAME, result.get("personalStatementFilename"));
        assertEquals("true", result.get("cvProvided"));
        assertEquals("XYZ", result.get("cvId"));
        assertEquals(CV_FILE_NAME, result.get("cvFilename"));
        assertEquals("1", result.get("numberOfReferences"));
        
        String expectedApplicantJson = "{\"title\":\""+APPLICANT_TITLE.getDisplayValue()+"\",\"phoneNumber\":\""+APPLICANT_PHONE_NUMBER+"\",\"fundingRequirements\":"+
                "\"[\\\""+FUNDING_DESCRIPTION+"\\\"]\",\"email\":\""+APPLICANT_EMAIL_ADDRESS+"\",\"applicationStatus\":\"Approved\",\"name\":" +
                "\""+APPLICANT_NAME+" "+APPLICANT_SURNAME+"\",\"mostRecentQualification\":\"None provided\",\"mostRecentEmployment\":" +
                "\"Shortcuts production\"}";
        assertEquals(expectedApplicantJson, result.get("applicant"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnSummaryWithNoEmploymentPositionsProvided() {
        ApplicationForm form = getSampleApplicationForm();
        
        form.setEmploymentPositions(Collections.EMPTY_LIST);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        expect(userServiceMock.getCurrentUser())
        .andReturn(currentUser);
        
        expect(applicationsServiceMock.getActionsDefinition(currentUser, form))
        .andReturn(actionsDefinitionMock);
        
        expect(actionsDefinitionMock.isRequiresAttention())
        .andReturn(ATTENTION_IS_REQUIRED);
        
        expect(userServiceMock.getNumberOfActiveApplicationsForApplicant(form.getApplicant()))
        .andReturn(NUMBER_OF_APPLICATIONS);
        
        expect(encryptionHelperMock.encrypt(form.getPersonalStatement().getId()))
        .andReturn("XYZ");
        expect(encryptionHelperMock.encrypt(form.getCv().getId()))
        .andReturn("XYZ");
        
        
        replay(userServiceMock, encryptionHelperMock, actionsDefinitionMock, applicationsServiceMock);
        Map<String, String> result = service.getSummary("APP");
        verify(userServiceMock, actionsDefinitionMock, encryptionHelperMock, applicationsServiceMock);
        
        assertFalse(result.isEmpty());
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        assertEquals(dateFormat.format(DATE_OF_SUBMISSION), result.get("applicationSubmissionDate"));
        assertEquals(dateFormat.format(DATE_OF_LAST_UPDATE), result.get("applicationUpdateDate"));
        assertEquals("true", result.get("requiresAttention"));
        assertEquals(NUMBER_OF_APPLICATIONS.toString(), result.get("numberOfActiveApplications"));
        assertEquals("XYZ", result.get("personalStatementId"));
        assertEquals(PERSONAL_STATEMENT_FILE_NAME, result.get("personalStatementFilename"));
        assertEquals("true", result.get("cvProvided"));
        assertEquals("XYZ", result.get("cvId"));
        assertEquals(CV_FILE_NAME, result.get("cvFilename"));
        assertEquals("1", result.get("numberOfReferences"));
        
        String expectedApplicantJson = "{\"title\":\""+APPLICANT_TITLE.getDisplayValue()+"\",\"phoneNumber\":\""+APPLICANT_PHONE_NUMBER+"\",\"fundingRequirements\":"+
                "\"[\\\""+FUNDING_DESCRIPTION+"\\\"]\",\"email\":\""+APPLICANT_EMAIL_ADDRESS+"\",\"applicationStatus\":\"Approved\",\"name\":" +
                "\""+APPLICANT_NAME+" "+APPLICANT_SURNAME+"\",\"mostRecentQualification\":\""+MOST_RECENT_QUALIFICATION_TITLE+"\",\"mostRecentEmployment\":" +
                        "\"None provided\"}";
        assertEquals(expectedApplicantJson, result.get("applicant"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnSummaryWithNoFundingsProvided() {
        ApplicationForm form = getSampleApplicationForm();
        
        form.setFundings(Collections.EMPTY_LIST);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        expect(userServiceMock.getCurrentUser())
        .andReturn(currentUser);
        
        expect(applicationsServiceMock.getActionsDefinition(currentUser, form))
        .andReturn(actionsDefinitionMock);
        
        expect(actionsDefinitionMock.isRequiresAttention())
        .andReturn(ATTENTION_IS_REQUIRED);
        
        expect(userServiceMock.getNumberOfActiveApplicationsForApplicant(form.getApplicant()))
        .andReturn(NUMBER_OF_APPLICATIONS);
        
        expect(encryptionHelperMock.encrypt(form.getPersonalStatement().getId()))
        .andReturn("XYZ");
        expect(encryptionHelperMock.encrypt(form.getCv().getId()))
        .andReturn("XYZ");
        
        
        replay(userServiceMock, encryptionHelperMock, actionsDefinitionMock, applicationsServiceMock);
        Map<String, String> result = service.getSummary("APP");
        verify(userServiceMock, actionsDefinitionMock, encryptionHelperMock, applicationsServiceMock);
        
        assertFalse(result.isEmpty());
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        assertEquals(dateFormat.format(DATE_OF_SUBMISSION), result.get("applicationSubmissionDate"));
        assertEquals(dateFormat.format(DATE_OF_LAST_UPDATE), result.get("applicationUpdateDate"));
        assertEquals("true", result.get("requiresAttention"));
        assertEquals(NUMBER_OF_APPLICATIONS.toString(), result.get("numberOfActiveApplications"));
        assertEquals("XYZ", result.get("personalStatementId"));
        assertEquals(PERSONAL_STATEMENT_FILE_NAME, result.get("personalStatementFilename"));
        assertEquals("true", result.get("cvProvided"));
        assertEquals("XYZ", result.get("cvId"));
        assertEquals(CV_FILE_NAME, result.get("cvFilename"));
        assertEquals("1", result.get("numberOfReferences"));
        
        String expectedApplicantJson = "{\"title\":\""+APPLICANT_TITLE.getDisplayValue()+"\",\"phoneNumber\":\""+APPLICANT_PHONE_NUMBER+"\",\"fundingRequirements\":"+
                "\"[\\\"None provided\\\"]\",\"email\":\""+APPLICANT_EMAIL_ADDRESS+"\",\"applicationStatus\":\"Approved\",\"name\":" +
                "\""+APPLICANT_NAME+" "+APPLICANT_SURNAME+"\",\"mostRecentQualification\":\""+MOST_RECENT_QUALIFICATION_TITLE+"\",\"mostRecentEmployment\":" +
                        "\"Shortcuts production\"}";
        assertEquals(expectedApplicantJson, result.get("applicant"));
    }
    
    @Test
    public void shouldReturnSummaryWithNoCvProvided() {
        ApplicationForm form = getSampleApplicationForm();
        
        form.setCv(null);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("APP"))
        .andReturn(form);
        
        expect(userServiceMock.getCurrentUser())
        .andReturn(currentUser);
        
        expect(applicationsServiceMock.getActionsDefinition(currentUser, form))
        .andReturn(actionsDefinitionMock);
        
        expect(actionsDefinitionMock.isRequiresAttention())
        .andReturn(ATTENTION_IS_REQUIRED);
        
        expect(userServiceMock.getNumberOfActiveApplicationsForApplicant(form.getApplicant()))
        .andReturn(NUMBER_OF_APPLICATIONS);
        
        expect(encryptionHelperMock.encrypt(form.getPersonalStatement().getId()))
        .andReturn("XYZ");
        
        
        replay(userServiceMock, encryptionHelperMock, actionsDefinitionMock, applicationsServiceMock);
        Map<String, String> result = service.getSummary("APP");
        verify(userServiceMock, actionsDefinitionMock, encryptionHelperMock, applicationsServiceMock);
        
        assertFalse(result.isEmpty());
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        assertEquals(dateFormat.format(DATE_OF_SUBMISSION), result.get("applicationSubmissionDate"));
        assertEquals(dateFormat.format(DATE_OF_LAST_UPDATE), result.get("applicationUpdateDate"));
        assertEquals("true", result.get("requiresAttention"));
        assertEquals(NUMBER_OF_APPLICATIONS.toString(), result.get("numberOfActiveApplications"));
        assertEquals("XYZ", result.get("personalStatementId"));
        assertEquals(PERSONAL_STATEMENT_FILE_NAME, result.get("personalStatementFilename"));
        assertEquals("false", result.get("cvProvided"));
        assertEquals("1", result.get("numberOfReferences"));
        
        String expectedApplicantJson = "{\"title\":\""+APPLICANT_TITLE.getDisplayValue()+"\",\"phoneNumber\":\""+APPLICANT_PHONE_NUMBER+"\",\"fundingRequirements\":"+
                "\"[\\\""+FUNDING_DESCRIPTION+"\\\"]\",\"email\":\""+APPLICANT_EMAIL_ADDRESS+"\",\"applicationStatus\":\"Approved\",\"name\":" +
                "\""+APPLICANT_NAME+" "+APPLICANT_SURNAME+"\",\"mostRecentQualification\":\""+MOST_RECENT_QUALIFICATION_TITLE+"\",\"mostRecentEmployment\":" +
                        "\"Shortcuts production\"}";
        assertEquals(expectedApplicantJson, result.get("applicant"));
    }
    
    private ApplicationForm getSampleApplicationForm() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(APPLICANT_ID)
                .email(APPLICANT_EMAIL_ADDRESS)
                .firstName(APPLICANT_NAME)
                .lastName(APPLICANT_SURNAME)
                .build();
        
        Referee referee = new RefereeBuilder().build();
        Referee refereeWhoResponded = new RefereeBuilder()
                .declined(true)
                .build();
        
        PersonalDetails details = new PersonalDetailsBuilder().id(321)
                .title(APPLICANT_TITLE)
                .phoneNumber(APPLICANT_PHONE_NUMBER)
                .build();
        
        Qualification qualification = new QualificationBuilder()
                .awardDate(DATE_OF_LAST_UPDATE)
                .build();
        Qualification mostRecentQualification = new QualificationBuilder()
                .awardDate(DATE_OF_SUBMISSION)
                .title(MOST_RECENT_QUALIFICATION_TITLE)
                .build();
        
        EmploymentPosition position = new EmploymentPositionBuilder()
                .endDate(DATE_OF_LAST_UPDATE)
                .toEmploymentPosition();
        EmploymentPosition mostRecentPosition = new EmploymentPositionBuilder()
                .employerName("Shortcuts production")
                .endDate(DATE_OF_SUBMISSION)
                .toEmploymentPosition();
        
        Document personalStatement = new DocumentBuilder().id(369)
                .fileName(PERSONAL_STATEMENT_FILE_NAME)
                .build();
        Document cv = new DocumentBuilder().id(379)
                .fileName(CV_FILE_NAME)
                .build();
        
        Funding funding = new FundingBuilder()
                .description(FUNDING_DESCRIPTION)
                .build();
        
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(6)
                .personalStatement(personalStatement)
                .referees(referee, refereeWhoResponded)
                .personalDetails(details)
                .fundings(funding)
                .qualifications(qualification, mostRecentQualification)
                .employmentPositions(position, mostRecentPosition)
                .status(ApplicationFormStatus.APPROVED)
                .submittedDate(DATE_OF_SUBMISSION)
                .cv(cv)
                .lastUpdated(DATE_OF_LAST_UPDATE)
                .applicant(applicant)
                .applicationNumber(SAMPLE_APPLICATION_NUMBER)
                .build();
        return applicationForm;
        
    }
    
    private RegisteredUser getCurrentUser() {
        return new RegisteredUserBuilder().id(CURRENT_USER_ID)
                .email(CURRENT_USER_EMAIL_ADDRESS)
                .build();
    }

}
