package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class UserIndexDAOTest extends AutomaticRollbackTestCase {

    private FullTextSearchDAO userIndexDAO;
    
    @Before
    public void prepare() {
        userIndexDAO = new FullTextSearchDAO(sessionFactory);
    }
    
    @Test
    public void shouldReturnAMatchingUserBasedOnHisFirstname() {
        RegisteredUser user1 = new RegisteredUserBuilder()
            .firstName("Tyler")
            .lastName("Durden")
            .email("tyler@durden.com")
            .username("tyler@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        RegisteredUser similiarToUser1 = new RegisteredUserBuilder()
            .firstName("Taylor")
            .lastName("Dordeen")
            .email("taylor@dordeen.com")
            .username("taylor@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        save(user1, similiarToUser1);
        flushAndClearSession();
        
        List<RegisteredUser> matchingUsersWithFirstnameLike = userIndexDAO.getMatchingUsersWithFirstnameLike("kevin");
        
        assertTrue(listContainsId(user1, matchingUsersWithFirstnameLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithFirstnameLike));
    }
    
    @Test
    @Ignore
    public void shouldReturnAMatchingUserBasedOnHisLastname() {
        RegisteredUser user1 = new RegisteredUserBuilder()
            .firstName("Tyler")
            .lastName("Durden")
            .email("tyler@durden.com")
            .username("tyler@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        RegisteredUser similiarToUser1 = new RegisteredUserBuilder()
            .firstName("Taylor")
            .lastName("Dordeen")
            .email("taylor@dordeen.com")
            .username("taylor@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        save(user1, similiarToUser1);
        flushAndClearSession();
        
        List<RegisteredUser> matchingUsersWithLastnameLike = userIndexDAO.getMatchingUsersWithLastnameLike("durden");
        
        assertTrue(listContainsId(user1, matchingUsersWithLastnameLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithLastnameLike));
    }

    @Test
    @Ignore
    public void shouldReturnAMatchingUserBasedOnHisEmail() {
        RegisteredUser user1 = new RegisteredUserBuilder()
            .firstName("Tyler")
            .lastName("Durden")
            .email("tyler@durden.com")
            .username("tyler@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        RegisteredUser similiarToUser1 = new RegisteredUserBuilder()
            .firstName("Taylor")
            .lastName("Dordeen")
            .email("taylor@dordeen.com")
            .username("taylor@durden.com")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(false).build();
        
        save(user1, similiarToUser1);
        flushAndClearSession();
        
        List<RegisteredUser> matchingUsersWithEmailLike = userIndexDAO.getMatchingUsersWithEmailLike("tulo@");
        
        assertTrue(listContainsId(user1, matchingUsersWithEmailLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithEmailLike));
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
