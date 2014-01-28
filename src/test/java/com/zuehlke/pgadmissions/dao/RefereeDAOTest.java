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
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReminderIntervalBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;
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
        application.setProgram(program);
        application.setApplicant(user);

        DomicileDAO domicileDAO= new DomicileDAO(sessionFactory);
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
        ApplicationForm unsubmittedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED)
                .build();
        ApplicationForm approvedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED).build();
        ApplicationForm rejectedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REJECTED).build();
        ApplicationForm withdrawnApplicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.WITHDRAWN)
                .build();
        save(unsubmittedApplication, approvedApplication, rejectedApplication, withdrawnApplicationForm);

        DomicileDAO domicileDAO= new DomicileDAO(sessionFactory);
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
        List<Referee> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(refereeOne));
        assertFalse(referees.contains(refereeTwo));
        assertFalse(referees.contains(refereeThree));
        assertFalse(referees.contains(refereeFour));

    }

    @Test
    public void shouldNotReturnRefereesWhoHaveDeclined() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);

        DomicileDAO domicileDAO= new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).declined(true).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
        save(referee);
        flushAndClearSession();
        List<Referee> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }

    @Test
    public void shouldNotReturnRefereesWhoHaveProvidedReference() {
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(application);
        Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
        ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).document(document).build();
        DomicileDAO domicileDAO= new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo")
                .reference(reference).user(user).build();

        save(document, reference, referee);

        flushAndClearSession();
        List<Referee> referees = refereeDAO.getRefereesDueReminder();
        assertNotNull(referees);
        assertFalse(referees.contains(referee.getId()));
    }
    
    @Test
    public void shouldReturnRefereesWhoHavenNotProvidedReference() {
        ApplicationForm application = new ApplicationFormBuilder().id(20).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        ApplicationForm application2 = new ApplicationFormBuilder().id(21).applicant(user).status(ApplicationFormStatus.REJECTED).build();
        save(application, application2);

        flushAndClearSession();

        Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
        DomicileDAO domicileDAO= new DomicileDAO(sessionFactory);
        Referee hasRefInApp = new RefereeBuilder().id(1).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwe.fs").firstname("sdsd").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("halliallo").build();

        Referee noRefInApp = new RefereeBuilder().id(2).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("rrwe.fsd").firstname("df").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();

        Referee noRefNoApp = new RefereeBuilder().id(3).application(application2).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("erwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdf").phoneNumber("halliho").build();

        Referee hasRefInApp1 = new RefereeBuilder().id(4).application(application).addressDomicile(domicileDAO.getDomicileById(2)).address1("sdfsdf")
                .email("rrwe.fsd").firstname("ssdf").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();

        Referee noRefInApp2 = new RefereeBuilder().id(6).application(application).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("rrwe.fsd").firstname("dfe").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();

        Referee hasRefButNotInApp = new RefereeBuilder().id(5).application(application2).addressDomicile(domicileDAO.getDomicileById(1)).address1("sdfsdf")
                .email("errwesd").firstname("sdf").jobEmployer("sdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();

        save(hasRefButNotInApp, hasRefInApp, hasRefInApp1, noRefInApp, noRefInApp2, noRefNoApp);

        ReferenceComment reference = new ReferenceCommentBuilder().document(document).comment("This is a reference comment").suitableForProgramme(false)
                .referee(hasRefInApp1).user(user).application(application).build();

        ReferenceComment referenceOne = new ReferenceCommentBuilder().document(document).comment("This is a reference comment").suitableForProgramme(false)
                .referee(hasRefInApp).user(user).application(application).build();

        ReferenceComment referenceTwo = new ReferenceCommentBuilder().document(document).comment("This is a reference comment").suitableForProgramme(false)
                .referee(hasRefButNotInApp).user(user).application(application2).build();
        save(document, reference, referenceOne, referenceTwo);

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
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        reminderInterval = new ReminderIntervalBuilder().id(1).reminderType(ReminderType.REFERENCE).duration(1).unit(DurationUnitEnum.WEEKS).build();

        sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

        save(user, institution, program);

        flushAndClearSession();
        refereeDAO = new RefereeDAO(sessionFactory);
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