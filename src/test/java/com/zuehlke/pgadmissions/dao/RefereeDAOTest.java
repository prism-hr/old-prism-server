package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReminderIntervalBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

    private User user;
    private Program program;
    private RefereeDAO refereeDAO;
    private ReminderInterval reminderInterval;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        RefereeDAO refereeDAO = new RefereeDAO();
        refereeDAO.getRefereeById(1);
    }

    @Test
    public void shouldDeleteReferee() {

        ApplicationForm application = new ApplicationForm();
        application.setAdvert(program);
        application.setApplicant(user);

        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf").email("errwe.fsd")
                .firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();
        save(application, referee);
        flushAndClearSession();

        Integer id = referee.getId();

        refereeDAO.delete(referee);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
    }

    @Test
    public void shouldSaveReferee() throws ParseException {
        Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").address1("UK").phoneNumber("hallihallo").build();
        flushAndClearSession();

        refereeDAO.save(referee);
        Assert.assertNotNull(referee.getId());
    }

    @Test
    public void shouldGetRefereeById() {
        Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").address1("UK").phoneNumber("hallihallo").build();
        sessionFactory.getCurrentSession().save(referee);
        flushAndClearSession();
        assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
    }

    @Test
    public void shouldNotReturnRefereesForInactiveApplicationForms() {
        ApplicationForm unsubmittedApplication = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).build();
        ApplicationForm approvedApplication = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_APPROVED)).build();
        ApplicationForm rejectedApplication = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_REJECTED)).build();
        ApplicationForm withdrawnApplicationForm = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_WITHDRAWN)).build();
        save(unsubmittedApplication, approvedApplication, rejectedApplication, withdrawnApplicationForm);

        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee refereeOne = new RefereeBuilder().id(1).declined(false).application(unsubmittedApplication).addressDomicile(domicileDAO.getDomicileById(1))
                .address1("sdfsdf").email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .user(user).build();

        Referee refereeTwo = new RefereeBuilder().id(2).declined(false).application(rejectedApplication).addressDomicile(domicileDAO.getDomicileById(1))
                .address1("sdfsdf").email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .user(user).build();

        Referee refereeThree = new RefereeBuilder().id(3).declined(false).application(approvedApplication).addressDomicile(domicileDAO.getDomicileById(1))
                .address1("sdfsdf").email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .user(user).build();

        Referee refereeFour = new RefereeBuilder().id(4).declined(false).application(withdrawnApplicationForm).addressDomicile(domicileDAO.getDomicileById(1))
                .address1("sdfsdf").email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .user(user).build();

        save(refereeOne, refereeTwo, refereeThree, refereeFour);
        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(refereeOne.getId()));
        assertFalse(referees.contains(refereeTwo.getId()));
        assertFalse(referees.contains(refereeThree.getId()));
        assertFalse(referees.contains(refereeFour.getId()));

    }

    @Test
    public void shouldNotReturnRefereesWhoHaveDeclined() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);

        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).declined(true).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
        save(referee);
        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereesWhoHaveProvidedReference() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
        ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).document(document).build();
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .reference(reference).user(user).build();

        save(document, reference, referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereesWhoHaveBeenRemindedInLastWeek() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date threeDaysAgo = DateUtils.addDays(now, -3);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(threeDaysAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").user(user).build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereesDueReminders() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date eightDaysAgo = DateUtils.addDays(now, -8);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(eightDaysAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertTrue(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereesWithNoReminders() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);

        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereeForWhichReminderWasSendOneWeekMinus5minAgoForSixDaysInternal() {
        reminderInterval = new ReminderIntervalBuilder().id(1).reminderType(ReminderType.REFERENCE).duration(6).unit(DurationUnitEnum.DAYS).build();

        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date oneWeekAgo = DateUtils.addDays(now, -7);
        Date oneWeekMinusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, 5);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(oneWeekMinusFiveMinAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertTrue(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereeReminded1DayAgoFor6DaysReminderInterval() {
        reminderInterval = new ReminderIntervalBuilder().id(1).reminderType(ReminderType.REFERENCE).duration(6).unit(DurationUnitEnum.DAYS).build();

        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date oneMinuteAgo = DateUtils.addDays(now, -1);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(oneMinuteAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereeReminded2DaysAgoForOneDayReminderInterval() {
        reminderInterval = new ReminderIntervalBuilder().id(1).reminderType(ReminderType.REFERENCE).duration(1).unit(DurationUnitEnum.DAYS).build();

        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date twoMinutesAgo = DateUtils.addDays(now, -2);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(twoMinutesAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertTrue(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereeForWhichReminderWasSendOne6DaysAnd5minAgo() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date sixDaysAgo = DateUtils.addDays(now, -6);
        Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(sixDaysAgo, -5);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(oneWeekPlusFiveMinAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereeForWhichReminderWasSendOneWeekPlus5minAgo() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date oneWeekAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
        Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().user(user).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(oneWeekPlusFiveMinAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertTrue(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereesForWhichThereIsNoRegisteredUserMapped() {
        ApplicationForm application = new ApplicationFormBuilder().advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        save(application);
        Date now = Calendar.getInstance().getTime();
        Date oneWeekAgo = DateUtils.addDays(now, -7);
        Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .lastNotified(oneWeekPlusFiveMinAgo).email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf")
                .phoneNumber("hallihallo").build();

        save(referee);

        flushAndClearSession();
        List<Integer> referees = refereeDAO.getRefereesDueReminder();
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldReturnRefereesWhoHavenNotProvidedReference() {
        ApplicationForm application = new ApplicationFormBuilder().id(20).advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        ApplicationForm application2 = new ApplicationFormBuilder().id(21).advert(program).applicant(user).status(new State().withId(PrismState.APPLICATION_REJECTED)).build();
        save(application, application2);

        flushAndClearSession();

        Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee hasRefInApp = new RefereeBuilder().application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fs").firstname("sdsd").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("halliallo").build();

        Referee noRefInApp = new RefereeBuilder().application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf").email("rrwe.fsd")
                .firstname("df").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();

        Referee noRefNoApp = new RefereeBuilder().application(application2).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("erwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdf").phoneNumber("halliho").build();

        Referee hasRefInApp1 = new RefereeBuilder().application(application).addressDomicile(domicileDAO.getDomicileById(2)).address1("sdfsdf")
                .email("rrwe.fsd").firstname("ssdf").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();

        Referee noRefInApp2 = new RefereeBuilder().application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("rrwe.fsd").firstname("dfe").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();

        Referee hasRefButNotInApp = new RefereeBuilder().application(application2).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwesd").firstname("sdf").jobEmployer("sdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();

        save(hasRefButNotInApp, hasRefInApp, hasRefInApp1, noRefInApp, noRefInApp2, noRefNoApp);

        save(document);

        flushAndClearSession();
        List<Referee> referees = refereeDAO.getRefereesWhoDidntProvideReferenceYet(application);
        assertNotNull(referees);
        assertEquals(2, referees.size());
        assertFalse(listContainsId(hasRefInApp.getId(), referees));
        assertTrue(listContainsId(noRefInApp.getId(), referees));
        assertTrue(listContainsId(noRefInApp2.getId(), referees));
        assertFalse(listContainsId(noRefNoApp.getId(), referees));
        assertFalse(listContainsId(hasRefButNotInApp.getId(), referees));
        assertFalse(listContainsId(hasRefInApp1.getId(), referees));
    }

    @Before
    public void prepare() {
        user = new UserBuilder().firstName("Jane").lastName("Doe").email("email2@test.com").activationCode("kod_aktywacyjny").userAccount(new UserAccount().withEnabled(false).withPassword("dupa"))
                .build();
        reminderInterval = new ReminderIntervalBuilder().id(1).reminderType(ReminderType.REFERENCE).duration(1).unit(DurationUnitEnum.WEEKS).build();
        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
        save(user);
        flushAndClearSession();
        refereeDAO = new RefereeDAO(sessionFactory);
        program = testObjectProvider.getEnabledProgram();
    }

    private boolean listContainsId(Integer id, List<Referee> referees) {
        for (Referee ref : referees) {
            if (ref.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

}