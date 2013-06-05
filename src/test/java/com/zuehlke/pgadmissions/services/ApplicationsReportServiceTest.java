package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class ApplicationsReportServiceTest {

    private RegisteredUser user;

    private ApplicationsReportService service;

    private ApplicationsService applicationsServiceMock;

    @Test
    public void testGetEmptyApplicationsReport() {
        // GIVEN
        List<ApplicationForm> applications = Lists.newArrayList();

        ApplicationsFiltering filtering = new ApplicationsFiltering();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(applications);

        // WHEN
        EasyMock.replay(applicationsServiceMock);
        DataTable dataTable = service.getApplicationsReport(user, filtering);
        EasyMock.verify(applicationsServiceMock);

        // THEN
        assertTrue(dataTable.getRows().isEmpty());
    }

    @Test
    public void testGetMinimalisticApplicationReport() {
        // GIVEN
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("Genowefa").lastName("Pigwa").email("gienia@pigwa.pl").build();
        Program program1 = new ProgramBuilder().code("ABC").title("BBC").build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().dateOfBirth(new Date()).gender(Gender.MALE).firstNationality(new LanguageBuilder().name("British").build()).build();
        ProgrammeDetails programmeDetails1 = new ProgrammeDetailsBuilder().sourcesOfInterest(new SourcesOfInterestBuilder().name("fooBar").build()).build();
        ApplicationForm app1 = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).personalDetails(personalDetails).applicant(applicant1).applicationNumber("07").program(program1).programmeDetails(programmeDetails1).build();
        List<ApplicationForm> applications = Lists.newArrayList(app1);

        ApplicationsFiltering filtering = new ApplicationsFiltering();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(applications);
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(new ArrayList<ApplicationForm>());

        // WHEN
        EasyMock.replay(applicationsServiceMock);
        DataTable table = service.getApplicationsReport(user, filtering);
        EasyMock.verify(applicationsServiceMock);

        // THEN
        assertEquals(1, table.getRows().size());

        TableRow row = table.getRow(0);
        assertEquals("07", getTextValue(table, row, "applicationId"));
        assertEquals("Genowefa", getTextValue(table, row, "firstNames"));
        assertEquals("Pigwa", getTextValue(table, row, "lastName"));
        assertEquals("gienia@pigwa.pl", getTextValue(table, row, "email"));
        assertEquals("ABC", getTextValue(table, row, "programmeId"));
        assertEquals("BBC", getTextValue(table, row, "programmeName"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "projectTitle"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "studyOption"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "provisionalSupervisors"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "academicYear"));
        assertEquals("null", getDateValue(table, row, "submittedDate"));
        assertEquals("null", getDateValue(table, row, "lastEditedDate"));
        assertEquals("Validation", getTextValue(table, row, "status"));
        assertEquals(0, getNumberValue(table, row, "validationTime"), 0);
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "feeStatus"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "academicallyQualified"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "adequateEnglish"));
        assertEquals(0, getNumberValue(table, row, "receivedReferences"), 0);
        assertEquals(0, getNumberValue(table, row, "positiveReviewEndorsements"), 0);
        assertEquals(0, getNumberValue(table, row, "negativeReviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "declinedReferences"),0);
        assertEquals(0, getNumberValue(table, row, "interviewTime"),0);
        assertEquals(0, getNumberValue(table, row, "interviewReports"),0);
        assertEquals(0, getNumberValue(table, row, "positiveInterviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "negativeInterviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "approvalTime"),0);
        assertEquals(0, getNumberValue(table, row, "approvalStages"),0);
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "primarySupervisor"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "secondarySupervisor"));
        assertEquals("Not approved", getTextValue(table, row, "outcome"));
        assertEquals("null", getDateValue(table, row, "outcomedate"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "outcomeType"));
        assertEquals(StringUtils.EMPTY, getTextValue(table, row, "outcomeNote"));
    }
    
    @Test
    public void testGetSampleApplicationReport() {
        // GIVEN
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(1939, 8, 1);
        Date today = calendar.getTime();
        Date yesterday = DateUtils.addDays(today, -1);
        Date tomorrow = DateUtils.addDays(today, 1);
        Date dayAfterTomorrow = DateUtils.addDays(today, 2);
        
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("Genowefa").lastName("Pigwa").email("gienia@pigwa.pl").build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().applicationStartDate(tomorrow).applicationDeadline(dayAfterTomorrow).academicYear("1939").build();
        Program program1 = new ProgramBuilder().code("ABC").title("BBC").instances(programInstance).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().dateOfBirth(new Date()).gender(Gender.MALE).firstNationality(new LanguageBuilder().name("British").build()).build();
        
        SuggestedSupervisor suggestedSupervisor1 = new SuggestedSupervisorBuilder().firstname("suggested").lastname("supervisor1").build();
        SuggestedSupervisor suggestedSupervisor2 = new SuggestedSupervisorBuilder().firstname("suggested").lastname("supervisor2").build();
        ProgrammeDetails programmeDetails1 = new ProgrammeDetailsBuilder().sourcesOfInterest(new SourcesOfInterestBuilder().name("fooBar").build()).studyOption("Part-time").suggestedSupervisors(suggestedSupervisor1, suggestedSupervisor2).startDate(tomorrow).build();

        StateChangeEvent validationEvent = new StateChangeEventBuilder().date(DateUtils.addDays(today, -10)).newStatus(ApplicationFormStatus.VALIDATION).build();
        StateChangeEvent reviewEvent = new StateChangeEventBuilder().date(DateUtils.addDays(today, -9)).newStatus(ApplicationFormStatus.REVIEW).build();
        StateChangeEvent interviewEvent1 = new StateChangeEventBuilder().date(DateUtils.addDays(today, -8)).newStatus(ApplicationFormStatus.INTERVIEW).build();
        StateChangeEvent interviewEvent2 = new StateChangeEventBuilder().date(DateUtils.addDays(today, -4)).newStatus(ApplicationFormStatus.INTERVIEW).build();
        StateChangeEvent approveEvent = new StateChangeEventBuilder().date(today).newStatus(ApplicationFormStatus.APPROVED).build();
        ValidationComment validationComment = new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.OVERSEAS).qualifiedForPhd(ValidationQuestionOptions.UNSURE).englishCompentencyOk(ValidationQuestionOptions.UNSURE).build();
        
        Referee referee1 = new RefereeBuilder().reference(new ReferenceCommentBuilder().suitableForProgramme(true).suitableForUcl(true).build()).build();
        Referee referee2 = new RefereeBuilder().reference(new ReferenceCommentBuilder().suitableForProgramme(true).suitableForUcl(false).build()).build();
        Referee referee3 = new RefereeBuilder().declined(true).build();
        
        Interviewer interviewer1 = new InterviewerBuilder().interviewComment(new InterviewCommentBuilder().suitableCandidateForProgramme(true).suitableCandidateForUcl(true).build()).build();
        Interviewer interviewer2 = new InterviewerBuilder().interviewComment(new InterviewCommentBuilder().suitableCandidateForProgramme(true).suitableCandidateForUcl(false).build()).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer1, interviewer2).build();
        
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).user(new RegisteredUserBuilder().firstName("Primary").lastName("Supervisor").build()).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(new RegisteredUserBuilder().firstName("Secondary").lastName("Supervisor").build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().projectTitle("title").supervisors(primarySupervisor, secondarySupervisor).recommendedConditionsAvailable(true).recommendedConditions("Conditions").build();
        
        ApplicationForm app1 = new ApplicationFormBuilder().applicant(applicant1).applicationNumber("07").program(program1).programmeDetails(programmeDetails1)
                .approvalRounds(approvalRound).personalDetails(personalDetails).latestApprovalRound(approvalRound).submittedDate(yesterday).lastUpdated(today).status(ApplicationFormStatus.APPROVED).events(validationEvent, reviewEvent, interviewEvent1, interviewEvent2, approveEvent).comments(validationComment).referees(referee1, referee2, referee3).latestInterview(interview).build();
        List<ApplicationForm> applications = Lists.newArrayList(app1);

        ApplicationsFiltering filtering = new ApplicationsFiltering();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(applications);
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(new ArrayList<ApplicationForm>());

        // WHEN
        EasyMock.replay(applicationsServiceMock);
        DataTable table = service.getApplicationsReport(user, filtering);
        EasyMock.verify(applicationsServiceMock);

        // THEN
        assertEquals(1, table.getRows().size());

        TableRow row = table.getRow(0);
        assertEquals("07", getTextValue(table, row, "applicationId"));
        assertEquals("Genowefa", getTextValue(table, row, "firstNames"));
        assertEquals("Pigwa", getTextValue(table, row, "lastName"));
        assertEquals("gienia@pigwa.pl", getTextValue(table, row, "email"));
        assertEquals("ABC", getTextValue(table, row, "programmeId"));
        assertEquals("BBC", getTextValue(table, row, "programmeName"));
        assertEquals("title", getTextValue(table, row, "projectTitle"));
        assertEquals("Part-time", getTextValue(table, row, "studyOption"));
        assertEquals("suggested supervisor1, suggested supervisor2", getTextValue(table, row, "provisionalSupervisors"));
        assertEquals("1939", getTextValue(table, row, "academicYear"));
        assertEquals("1939-08-31", getDateValue(table, row, "submittedDate"));
        assertEquals("1939-09-01", getDateValue(table, row, "lastEditedDate"));
        assertEquals("Offer Recommended", getTextValue(table, row, "status"));
        assertEquals(24, getNumberValue(table, row, "validationTime"), 0);
        assertEquals("Overseas", getTextValue(table, row, "feeStatus"));
        assertEquals("Unsure", getTextValue(table, row, "academicallyQualified"));
        assertEquals("Unsure", getTextValue(table, row, "adequateEnglish"));
        assertEquals(2, getNumberValue(table, row, "receivedReferences"), 0);
        assertEquals(3, getNumberValue(table, row, "positiveReviewEndorsements"), 0);
        assertEquals(1, getNumberValue(table, row, "negativeReviewEndorsements"),0);
        assertEquals(1, getNumberValue(table, row, "declinedReferences"),0);
        assertEquals(192, getNumberValue(table, row, "interviewTime"),0);
        assertEquals(2, getNumberValue(table, row, "interviewReports"),0);
        assertEquals(3, getNumberValue(table, row, "positiveInterviewEndorsements"),0);
        assertEquals(1, getNumberValue(table, row, "negativeInterviewEndorsements"),0);
        assertEquals(0, getNumberValue(table, row, "approvalTime"),0);
        assertEquals(1, getNumberValue(table, row, "approvalStages"),0);
        assertEquals("Primary Supervisor", getTextValue(table, row, "primarySupervisor"));
        assertEquals("Secondary Supervisor", getTextValue(table, row, "secondarySupervisor"));
        assertEquals("Approved", getTextValue(table, row, "outcome"));
        assertEquals("1939-09-01", getDateValue(table, row, "outcomedate"));
        assertEquals("Conditional", getTextValue(table, row, "outcomeType"));
        assertEquals("Conditions", getTextValue(table, row, "outcomeNote"));
    }

    @Before
    public void setUp() {
        user = new RegisteredUser();
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        service = new ApplicationsReportService(applicationsServiceMock);
    }

    public String getTextValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        TextValue value = (TextValue) row.getCell(columnIndex).getValue();
        return value.getValue();
    }
    
    public String getDateValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        DateValue value = (DateValue) row.getCell(columnIndex).getValue();
        return value.toString();
    }

    public double getNumberValue(DataTable table, TableRow row, String columnId) {
        int columnIndex = table.getColumnIndex(columnId);
        NumberValue value = (NumberValue) row.getCell(columnIndex).getValue();
        return value.getValue();
    }
    
}
