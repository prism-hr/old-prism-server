package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

    
    private RefereeDAO refereeDAO;
    
    private User user;
    
    private ApplicationForm application;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        RefereeDAO refereeDAO = new RefereeDAO();
        refereeDAO.getRefereeById(1);
    }

    @Test
    public void shouldDeleteReferee() {
        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(testObjectProvider.getDomicile())).user(user)
                .jobEmployer("sdfsdf").jobTitle("fsdsd").phoneNumber("hallihallo").build();
        save(referee);
        flushAndClearSession();

        Integer id = referee.getId();

        refereeDAO.delete(referee);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
    }

    @Test
    public void shouldSaveReferee() throws ParseException {
        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(testObjectProvider.getDomicile())).user(user).phoneNumber("hallihallo").build();
        flushAndClearSession();

        refereeDAO.save(referee);
        Assert.assertNotNull(referee.getId());
    }

    @Test
    public void shouldGetRefereeById() {
        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(testObjectProvider.getDomicile())).user(user).phoneNumber("hallihallo").build();
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