package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testFullTextSearchContext.xml")
public class FullTextSearchServiceTest {

    @Autowired
    private FullTextSearchService fullTextService;

    @Autowired
    private SessionFactory sessionFactory;

    private RegisteredUser user1;

    private RegisteredUser similiarToUser1;

    private Transaction transaction;
    
    @Before
    public void prepare() {
        transaction = sessionFactory.getCurrentSession().beginTransaction();
        
        user1 = new RegisteredUserBuilder().firstName("Tyler").lastName("Durden")
                .email("tyler@durden.com").username("tyler@durden.com").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        similiarToUser1 = new RegisteredUserBuilder().firstName("Taylor").lastName("Dordeen")
                .email("taylor@dordeen.com").username("taylor@durden.com").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        save(user1, similiarToUser1);
        
        transaction.commit();
    }
    
    @Test
    public void shouldReturnAMatchingUserBasedOnHisFirstname() {
        List<RegisteredUser> matchingUsersWithFirstnameLike = fullTextService.getMatchingUsersWithFirstnameLike("kevin");
        assertTrue(listContainsId(user1, matchingUsersWithFirstnameLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithFirstnameLike));
    }
    
    @Test
    @Ignore
    public void shouldReturnAMatchingUserBasedOnHisLastname() {
        List<RegisteredUser> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durden");
        assertTrue(listContainsId(user1, matchingUsersWithLastnameLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithLastnameLike));
    }

    @Test
    @Ignore
    public void shouldReturnAMatchingUserBasedOnHisEmail() {
        List<RegisteredUser> matchingUsersWithEmailLike = fullTextService.getMatchingUsersWithEmailLike("tulo@");
        assertTrue(listContainsId(user1, matchingUsersWithEmailLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithEmailLike));
    }
    
    protected void save(List<? extends Object> domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }
    
    protected void save(Object... domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }
    
    protected void flushAndClearSession() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (user.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
