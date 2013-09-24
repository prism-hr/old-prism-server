package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormLastAccessBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationFormLastAccessDAOTest extends AutomaticRollbackTestCase {
    
    private ApplicationFormLastAccessDAO dao;
    
    private RegisteredUser user;
    
    private ApplicationForm form;
    
    @Override
    public void setup() {
        super.setup();
        dao = new ApplicationFormLastAccessDAO(sessionFactory);  
        user = new RegisteredUserBuilder()                       
            .firstName("Ozio")                                   
            .lastName("Primitivo")                               
            .email("ozio@mail.com")                              
            .username("ozio@mail.com")                           
            .build();                                            
        form = new ApplicationFormBuilder()                      
            .applicant(user)                                     
            .build();                                            
        save(user, form);                                        
        flushAndClearSession();                                  
    }

    @Test
    public void shouldSaveNewAccess() {
        ApplicationFormLastAccess access = new ApplicationFormLastAccessBuilder()
            .user(user)
            .applicationForm(form)
            .lastAccessTimestamp(new Date())
            .build();
        
        
        dao.saveAccess(access);
        
        assertNotNull(access.getId());
    }
    
    @Test
    public void shouldUpdatedAccess() {
        DateTime date = new DateTime(2010, 1, 1,0, 0);
        ApplicationFormLastAccess access = new ApplicationFormLastAccessBuilder()
        .user(user)
        .applicationForm(form)
        .lastAccessTimestamp(new Date())
        .build();
        
        
        dao.saveAccess(access);
        assertNotNull(access.getId());
        
        access.setLastAccessTimestamp(date.toDate());
        dao.saveAccess(access);
        
        ApplicationFormLastAccess result = dao.getLastAccess(form, user);
        assertNotNull(result);
        assertAccessesEqual(access, result);
    }
    
    @Test
    public void shouldRemoveAccess() {
        ApplicationFormLastAccess access = new ApplicationFormLastAccessBuilder()
        .user(user)
        .applicationForm(form)
        .lastAccessTimestamp(new Date())
        .build();
        
        save(access);
        flushAndClearSession();
        
        dao.deleteAccess(access);
        
        assertNull(dao.getLastAccess(form, user));
        
        assertNotNull(access.getId());
    }
    
    @Test
    public void shouldReturnLastAccess() {
        DateTime date = new DateTime(2010, 1, 1,0, 0);
        ApplicationFormLastAccess access = new ApplicationFormLastAccessBuilder()
        .user(user)
        .applicationForm(form)
        .lastAccessTimestamp(date.toDate())
        .build();
        
        save(access);
        flushAndClearSession();
        
        ApplicationFormLastAccess returnedAccess = dao.getLastAccess(form, user);
        
        assertNotNull(returnedAccess);
        assertAccessesEqual(access, returnedAccess);
    }
    
    public void assertAccessesEqual(ApplicationFormLastAccess expected, ApplicationFormLastAccess actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(expected.getApplicationForm().getId(), actual.getApplicationForm().getId());
        assertEquals((Integer) 0, (Integer)expected.getLastAccessTimestamp().compareTo(actual.getLastAccessTimestamp()));
        assertNotNull(expected.getId());
    }

}
