package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class ApplicationFormMappingTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;
    private Program program;
    private Project project;
    private RegisteredUser reviewerUser;
    private RegisteredUser interviewerUser;
    private RegisteredUser applicationAdmin;
    private RegisteredUser approver;

    @Test
    public void shouldSaveAndLoadApplicationForm() throws ParseException {

        Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
        ApplicationForm application = new ApplicationForm();
        application.setApplicant(user);
        application.setLastUpdated(lastUpdatedDate);
        application.setProgram(program);
        application.setProject(project);
        application.setStatus(ApplicationFormStatus.APPROVED);
        application.setApplicationAdministrator(applicationAdmin);
        application.setApplicationNumber("ABC");
        assertNull(application.getId());

        sessionFactory.getCurrentSession().save(application);

        assertNotNull(application.getId());
        Integer id = application.getId();
        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertSame(application, reloadedApplication);

        flushAndClearSession();

        reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertNotSame(application, reloadedApplication);
        assertEquals(application.getId(), reloadedApplication.getId());

        assertEquals(user.getId(), reloadedApplication.getApplicant().getId());

        assertEquals(program.getId(), reloadedApplication.getProgram().getId());
        assertEquals(project.getId(), reloadedApplication.getProject().getId());
        assertEquals(ApplicationFormStatus.APPROVED, reloadedApplication.getStatus());
        assertEquals("title", reloadedApplication.getProjectTitle());
        assertEquals(lastUpdatedDate, application.getLastUpdated());
        assertEquals(applicationAdmin, application.getApplicationAdministrator());
        assertEquals("ABC", application.getApplicationNumber());
    }

    @Test
    public void shouldLoadApplicationFormWithPersonalDetails() throws ParseException {
        Country country1 = new CountryBuilder().name("AA").code("AA").enabled(true).build();
        Country country2 = new CountryBuilder().name("CC").code("CC").enabled(true).build();
        Domicile country3 = new DomicileBuilder().name("DD").code("DD").enabled(true).build();
        Language nationality1 = new LanguageBuilder().name("aaaaa").code("aa").enabled(true).build();
        save(country1, country2, country3, nationality1);

        ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).firstNationality(nationality1)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).title(Title.MR).gender(Gender.MALE).residenceDomicile(country3)
                .requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc").applicationForm(application).build();
        application.setPersonalDetails(personalDetails);

        sessionFactory.getCurrentSession().save(application);
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(personalDetails.getId(), reloadedApplication.getPersonalDetails().getId());
    }

    @Test
    public void shouldLoadApplicationFormWithInterview() throws ParseException {

        ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).build();

        sessionFactory.getCurrentSession().save(application);
        flushAndClearSession();
        Interview interview = new InterviewBuilder().application(application).lastNotified(new Date()).furtherDetails("tba").locationURL("pgadmissions")
                .build();

        sessionFactory.getCurrentSession().save(interview);
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(interview.getId(), reloadedApplication.getInterviews().get(0).getId());
    }

    @Test
    public void shouldSaveAndLoadApplicationFormWithAddress() {
        ApplicationForm application = new ApplicationForm();
        application.setProgram(program);
        application.setApplicant(user);

        Domicile domicile = new DomicileBuilder().name("AA").code("AA").enabled(true).build();
        save(domicile);

        Address addressOne = new AddressBuilder().domicile(domicile).address1("london").build();
        Address addressTwo = new AddressBuilder().domicile(domicile).address1("london").build();

        application.setCurrentAddress(addressOne);
        application.setContactAddress(addressTwo);

        save(application);
        assertNotNull(addressOne.getId());
        assertNotNull(addressTwo.getId());
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(addressOne.getId(), reloadedApplication.getCurrentAddress().getId());
        assertEquals(addressTwo.getId(), reloadedApplication.getContactAddress().getId());
    }

    @Test
    public void shouldLoadApplicationFormWithCVAndPersonalStatement() {

        ApplicationForm application = new ApplicationForm();
        application.setProgram(program);
        application.setApplicant(user);
        Document cv = new DocumentBuilder().fileName("bob").type(DocumentType.CV).content("aaa!".getBytes()).build();
        Document personalStatement = new DocumentBuilder().fileName("bob").type(DocumentType.PERSONAL_STATEMENT).content("aaa!".getBytes()).build();
        save(cv, personalStatement);
        flushAndClearSession();
        application.setCv(cv);
        application.setPersonalStatement(personalStatement);

        sessionFactory.getCurrentSession().save(application);
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());

        assertEquals(cv.getId(), reloadedApplication.getCv().getId());
        assertEquals(personalStatement.getId(), application.getPersonalStatement().getId());
    }

    @Test
    public void shouldLoadApplicationFormWithComments() {

        ApplicationForm application = new ApplicationForm();
        application.setProgram(program);
        application.setApplicant(user);

        sessionFactory.getCurrentSession().save(application);
        Integer id = application.getId();
        flushAndClearSession();

        Comment commentOne = new CommentBuilder().application(application).comment("comment1").user(user).build();
        Comment commentTwo = new CommentBuilder().application(application).comment("comment2").user(user).build();
        save(commentOne, commentTwo);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertEquals(2, reloadedApplication.getApplicationComments().size());

        assertTrue(listContainsId(commentOne, reloadedApplication.getApplicationComments()));
        assertTrue(listContainsId(commentTwo, reloadedApplication.getApplicationComments()));
    }

    @Test
    public void shouldSaveQualificationsWithApplication() throws ParseException {

        ApplicationForm application = new ApplicationForm();
        application.setProgram(program);
        application.setApplicant(user);

        QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification1 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("")
                .title("").languageOfStudy("Abkhazian").subject("").isCompleted(true).institutionCode("AS009Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();
        Qualification qualification2 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").title("")
                .isCompleted(true).institution("").languageOfStudy("Achinese").subject("").institutionCode("AS008Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();

        application.getQualifications().addAll(Arrays.asList(qualification1, qualification2));

        sessionFactory.getCurrentSession().saveOrUpdate(application);
        flushAndClearSession();

        Integer id = application.getId();
        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertEquals(2, reloadedApplication.getQualifications().size());
    }

    @Test
    public void shouldSaveAndLoadNotificationRecordsWithApplication() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
        NotificationRecord recordOne = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("01 12 2011 14:09:26"))
                .notificationType(NotificationType.UPDATED_NOTIFICATION).build();
        NotificationRecord recordTwo = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("03 12 2011 14:09:26"))
                .notificationType(NotificationType.VALIDATION_REMINDER).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).notificationRecords(recordOne, recordTwo).build();

        save(application);
        Integer recordOneId = recordOne.getId();
        assertNotNull(recordOneId);
        assertNotNull(recordTwo.getId());
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(2, reloadedApplication.getNotificationRecords().size());

        assertTrue(listContainsId(recordOne, reloadedApplication.getNotificationRecords()));
        assertTrue(listContainsId(recordTwo, reloadedApplication.getNotificationRecords()));

        recordOne = (NotificationRecord) sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId);
        reloadedApplication.removeNotificationRecord(recordOne);
        save(reloadedApplication);
        flushAndClearSession();

        reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(1, reloadedApplication.getNotificationRecords().size());
        assertTrue(listContainsId(recordTwo, reloadedApplication.getNotificationRecords()));

        assertNull(sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId));
    }

    @Test
    public void shouldSaveAndLoadEventsWithApplication() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
        StateChangeEvent eventOne = new StateChangeEventBuilder().date(simpleDateFormat.parse("01 12 2011 14:09:26")).newStatus(ApplicationFormStatus.REJECTED)
                .build();
        StateChangeEvent eventTwo = new StateChangeEventBuilder().date(simpleDateFormat.parse("03 12 2011 14:09:26"))
                .newStatus(ApplicationFormStatus.UNSUBMITTED).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).events(eventOne, eventTwo).build();

        save(application);
        Integer eventOneId = eventOne.getId();
        assertNotNull(eventOneId);
        assertNotNull(eventTwo.getId());
        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(2, reloadedApplication.getEvents().size());

        assertTrue(listContainsId(eventOne, reloadedApplication.getEvents()));
        assertTrue(listContainsId(eventTwo, reloadedApplication.getEvents()));

        eventOne = (StateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, eventOneId);
        reloadedApplication.getEvents().remove(eventOne);
        save(reloadedApplication);
        flushAndClearSession();

        reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(1, reloadedApplication.getEvents().size());
        assertTrue(listContainsId(eventTwo, reloadedApplication.getEvents()));

        assertNull(sessionFactory.getCurrentSession().get(StateChangeEvent.class, eventOneId));
    }

    @Test
    public void shouldLoadInterviewsForApplicationForm() throws ParseException, InterruptedException {

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        Interview interviewOne = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).build()).application(application).build();
        save(interviewOne);

        Interview interviewTwo = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).build()).application(application).build();
        save(interviewTwo);

        Interview interviewTrhee = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).build()).application(application).build();
        save(interviewTrhee);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(3, reloadedApplication.getInterviews().size());
        assertTrue(listContainsId(interviewOne, reloadedApplication.getInterviews()));
        assertTrue(listContainsId(interviewTwo, reloadedApplication.getInterviews()));
        assertTrue(listContainsId(interviewTrhee, reloadedApplication.getInterviews()));
    }

    @Test
    public void shouldSaveAndLoadLatestInterview() throws ParseException, InterruptedException {

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).build()).application(application).build();
        save(interview);

        application.setLatestInterview(interview);
        save(application);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(interview.getId(), reloadedApplication.getLatestInterview().getId());
    }

    @Test
    public void shouldLoadReveiwRoundForApplicationForm() throws ParseException, InterruptedException {

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        ReviewRound reviewRoundOne = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).application(application).build();
        save(reviewRoundOne);

        ReviewRound reviewRoundTwo = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).application(application).build();
        save(reviewRoundTwo);

        ReviewRound reviewRoundTrhee = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).application(application).build();
        save(reviewRoundTrhee);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
        assertEquals(3, reloadedApplication.getReviewRounds().size());

        assertTrue(listContainsId(reviewRoundOne, reloadedApplication.getReviewRounds()));
        assertTrue(listContainsId(reviewRoundTwo, reloadedApplication.getReviewRounds()));
        assertTrue(listContainsId(reviewRoundTrhee, reloadedApplication.getReviewRounds()));
    }

    @Test
    public void shouldSaveAndLoadLatestReviewRound() throws ParseException, InterruptedException {

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).application(application).build();
        save(reviewRound);

        application.setLatestReviewRound(reviewRound);
        save(application);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());

        assertEquals(reviewRound.getId(), reloadedApplication.getLatestReviewRound().getId());
    }

    @Test
    public void shouldSaveAndLoadRejection() throws ParseException, InterruptedException {

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        RejectReasonDAO rejectReasonDAO = new RejectReasonDAO(sessionFactory);
        RejectReason rejectReason = rejectReasonDAO.getAllReasons().get(0);
        Rejection rejection = new RejectionBuilder().includeProspectusLink(true).rejectionReason(rejectReason).build();

        application.setRejection(rejection);
        save(application);

        flushAndClearSession();

        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());

        assertEquals(rejection.getId(), reloadedApplication.getRejection().getId());
    }

    @Test
    public void shouldLoadApplicationFormWithADisabledProject() throws ParseException {

        Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
        ApplicationForm application = new ApplicationForm();
        application.setApplicant(user);
        application.setLastUpdated(lastUpdatedDate);
        application.setProgram(program);
        application.setProject(project);
        application.setStatus(ApplicationFormStatus.APPROVED);
        application.setApplicationAdministrator(applicationAdmin);
        application.setApplicationNumber("ABC");
        assertNull(application.getId());

        sessionFactory.getCurrentSession().save(application);
        project.setDisabled(true);
        project.getAdvert().setActive(false);
        sessionFactory.getCurrentSession().update(project);

        assertNotNull(application.getId());
        Integer id = application.getId();
        ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertSame(application, reloadedApplication);

        flushAndClearSession();

        reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
        assertNotSame(application, reloadedApplication);
        assertEquals(application.getId(), reloadedApplication.getId());

        assertEquals(user.getId(), reloadedApplication.getApplicant().getId());

        assertEquals(program.getId(), reloadedApplication.getProgram().getId());
        assertEquals(project.getId(), reloadedApplication.getProject().getId());
        assertTrue(project.isDisabled());
        assertEquals(ApplicationFormStatus.APPROVED, reloadedApplication.getStatus());
        assertEquals("title", reloadedApplication.getProjectTitle());
        assertEquals(lastUpdatedDate, application.getLastUpdated());
        assertEquals(applicationAdmin, application.getApplicationAdministrator());
        assertEquals("ABC", application.getApplicationNumber());
    }

    @Before
    public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        reviewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("hoopla").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        interviewerUser = new RegisteredUserBuilder().firstName("brad").lastName("brady").email("brady@test.com").username("brady").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        applicationAdmin = new RegisteredUserBuilder().firstName("joan").lastName("arc").email("act@test.com").username("arc").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        approver = new RegisteredUserBuilder().firstName("het").lastName("get").email("het@test.com").username("hed").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        Advert advert = new AdvertBuilder().title("title").description("description").funding("funding").studyDuration(6).build();
        project = new ProjectBuilder().advert(advert).author(applicationAdmin).primarySupervisor(applicationAdmin).program(program).build();

        save(user, reviewerUser, institution, program, interviewerUser, applicationAdmin, approver, project);

        flushAndClearSession();
    }

    private boolean listContainsId(Comment comment, List<Comment> comments) {
        for (Comment entry : comments) {
            if (entry.getId().equals(comment.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(NotificationRecord record, List<NotificationRecord> notificationRecords) {
        for (NotificationRecord entry : notificationRecords) {
            if (entry.getId().equals(record.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(StateChangeEvent event, List<Event> events) {
        for (Event entry : events) {
            if (entry.getId().equals(event.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(Interview interview, List<Interview> interviews) {
        for (Interview entry : interviews) {
            if (entry.getId().equals(interview.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(ReviewRound review, List<ReviewRound> reviewRounds) {
        for (ReviewRound entry : reviewRounds) {
            if (entry.getId().equals(review.getId())) {
                return true;
            }
        }
        return false;
    }

}
