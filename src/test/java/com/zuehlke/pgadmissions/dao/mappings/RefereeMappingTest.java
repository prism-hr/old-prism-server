package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class RefereeMappingTest extends AutomaticRollbackTestCase {

    private ApplicationForm applicationForm;
    private RegisteredUser refereeUser;
    private RegisteredUser applicant;

    @Test
    public void shouldSaveAndLoadReferee() throws Exception {
        Date lastNotified = new SimpleDateFormat("dd MM yyyy").parse("01 05 2012");

        Domicile addressDomicile = new DomicileBuilder().code("FF").enabled(true).name("FF").build();

        save(addressDomicile);
        flushAndClearSession();

        Referee referee = new RefereeBuilder().application(applicationForm).addressDomicile(addressDomicile).address1("loc").email("email").firstname("name")
                        .jobEmployer("emplo").jobTitle("titl").lastname("lastname").phoneNumber("phoneNumber").declined(true).lastNotified(lastNotified)
                        .build();

        save(referee);
        flushAndClearSession();

        assertNotNull(referee.getId());
        Integer id = referee.getId();
        Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);

        assertNotSame(referee, reloadedReferee);
        assertEquals(referee.getId(), reloadedReferee.getId());

        assertEquals(referee.getAddressLocation().getDomicile().getId(), reloadedReferee.getAddressLocation().getDomicile().getId());
        assertEquals(referee.getAddressLocation().getId(), reloadedReferee.getAddressLocation().getId());

        assertEquals(referee.getApplication().getId(), reloadedReferee.getApplication().getId());
        assertEquals(referee.getEmail(), reloadedReferee.getEmail());
        assertEquals(referee.getFirstname(), reloadedReferee.getFirstname());
        assertEquals(referee.getJobEmployer(), reloadedReferee.getJobEmployer());
        assertEquals(referee.getJobTitle(), reloadedReferee.getJobTitle());
        assertEquals(referee.getLastname(), reloadedReferee.getLastname());

        assertEquals(referee.getPhoneNumber(), reloadedReferee.getPhoneNumber());
        assertTrue(reloadedReferee.isDeclined());
        assertEquals(lastNotified, reloadedReferee.getLastNotified());
    }

    @Test
    public void shouldSaveAndLoadRefereeWithUser() throws Exception {
        RegisteredUser user = new RegisteredUserBuilder().email("testnewemail@onetwo.com").username("testnewemail@onetwo.com").firstName("jane").lastName("u")
                        .password("123").build();

        sessionFactory.getCurrentSession().save(user);

        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Referee referee = new RefereeBuilder().application(applicationForm).addressDomicile(domicileDAO.getDomicileById(1)).address1("loc").email("email")
                        .user(user).firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname").phoneNumber("phoneNumber").build();

        sessionFactory.getCurrentSession().save(referee);
        flushAndClearSession();

        Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());

        reloadedReferee.setReference(null);
        sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
        flushAndClearSession();

    }

    @Before
    public void prepare() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a50").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        save(institution, program);

        applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                        .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        refereeUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1").password("password")
                        .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(applicant, refereeUser);
        save(applicant);

        applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
        save(applicationForm);
        flushAndClearSession();
    }
}
