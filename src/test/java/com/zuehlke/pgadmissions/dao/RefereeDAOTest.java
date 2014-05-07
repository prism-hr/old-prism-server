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
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

    
    private RefereeDAO refereeDAO;
    
    private DomicileDAO domicileDAO;
    
    private User user;
    
    private ApplicationForm application;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        RefereeDAO refereeDAO = new RefereeDAO();
        refereeDAO.getRefereeById(1);
    }

    @Test
    public void shouldDeleteReferee() {

        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(domicileDAO.getDomicileById(1))).user(user)
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
        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(domicileDAO.getDomicileById(1))).user(user).phoneNumber("hallihallo").build();
        flushAndClearSession();

        refereeDAO.save(referee);
        Assert.assertNotNull(referee.getId());
    }

    @Test
    public void shouldGetRefereeById() {
        Referee referee = new RefereeBuilder().application(application).address(TestData.anAddress(domicileDAO.getDomicileById(1))).user(user).phoneNumber("hallihallo").build();
        sessionFactory.getCurrentSession().save(referee);
        flushAndClearSession();
        assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
    }

    @Before
    public void prepare() {
        
        user = new UserBuilder().firstName("Jane").lastName("Doe").email("email2@test.com").activationCode("kod_aktywacyjny")
                .userAccount(new UserAccount().withEnabled(false).withPassword("dupa")).build();
        application = testObjectProvider.getApplication(PrismState.APPLICATION_UNSUBMITTED);
        
        save(user);
        flushAndClearSession();
        
        refereeDAO = new RefereeDAO(sessionFactory);
        domicileDAO = new DomicileDAO(sessionFactory);
    }

}