package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

    private RefereeDAO refereeDAO;

    private User user;

    private Application application;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        RefereeDAO refereeDAO = new RefereeDAO();
        refereeDAO.getRefereeById(1);
    }

    @Test
    public void shouldDeleteReferee() {
        Referee referee = new Referee().withApplication(application).withAddress(TestData.anAddress(testObjectProvider.getDomicile())).withUser(user)
                .withJobEmployer("sdfsdf").withJobEmployer("fsdsd").withPhoneNumber("hallihallo");
        save(referee);
        flushAndClearSession();

        Integer id = referee.getId();

        refereeDAO.delete(referee);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
    }

    @Test
    public void shouldSaveReferee() throws ParseException {
        Referee referee = new Referee().withApplication(application).withAddress(TestData.anAddress(testObjectProvider.getDomicile())).withUser(user)
                .withPhoneNumber("hallihallo");
        flushAndClearSession();

        refereeDAO.save(referee);
        Assert.assertNotNull(referee.getId());
    }

    @Test
    public void shouldGetRefereeById() {
        Referee referee = new Referee().withApplication(application).withAddress(TestData.anAddress(testObjectProvider.getDomicile())).withUser(user)
                .withPhoneNumber("hallihallo");
        sessionFactory.getCurrentSession().save(referee);
        flushAndClearSession();
        assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
    }

    @Before
    public void prepare() {

        user = new User().withFirstName("Jane").withLastName("Doe").withEmail("email2@test.com").withActivationCode("kod_aktywacyjny")
                .withAccount(new UserAccount().withEnabled(false).withPassword("dupa"));
        application = testObjectProvider.getApplication(PrismState.APPLICATION_UNSUBMITTED);

        save(user);
        flushAndClearSession();

        refereeDAO = new RefereeDAO(sessionFactory);
    }

}