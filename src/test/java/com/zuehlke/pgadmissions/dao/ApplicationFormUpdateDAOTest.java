package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormLastAccessBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUpdateBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;

public class ApplicationFormUpdateDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormUpdateDAO dao;
    
    private RegisteredUser user;
    
    private ApplicationForm form;
    
    @Before
    public void prepare() {
        dao = new ApplicationFormUpdateDAO(sessionFactory);
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
    public void shouldSaveUpdate() {
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(new Date())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
            
        dao.saveUpdate(update);
        
        assertNotNull(update.getId());
    }
    
    @Test
    public void shouldDeleteUpdate() {
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
        .applicationForm(form)
        .updateTimestamp(new Date())
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .build();
        
        save(update);
        flushAndClearSession();
        
        dao.deleteUpdate(update);
    }
    
    @Test
    public void shouldReturnUpdatesSinceSpecificDate() {
        DateTime sinceDate = new DateTime(2013, 2, 3, 00, 00);

        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
            .lastAccessTimestamp(sinceDate.toDate())
            .user(user)
            .applicationForm(form)
            .build();
        
        ApplicationFormUpdate update1 = new ApplicationFormUpdateBuilder()
        .applicationForm(form)
        .updateTimestamp(sinceDate.plusHours(1).toDate())
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .build();
        ApplicationFormUpdate update2 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.plusHours(2).toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        
        save(update1, update2, lastAccess);
        flushAndClearSession();
        
        Integer id1 = update1.getId();
        Integer id2 = update2.getId();
        
        List<ApplicationFormUpdate> result = dao.getUpdatesForUser(form, user);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
    }
    
    @Test
    public void shouldReturnAllUpdatesIfUserHasNeverAccessedTheApplication() {
        DateTime sinceDate = new DateTime(2013, 2, 3, 00, 00);
        
        
        ApplicationFormUpdate update1 = new ApplicationFormUpdateBuilder()
        .applicationForm(form)
        .updateTimestamp(sinceDate.minusHours(1).toDate())
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .build();
        ApplicationFormUpdate update2 = new ApplicationFormUpdateBuilder()
        .applicationForm(form)
        .updateTimestamp(sinceDate.plusHours(2).toDate())
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .build();
        
        save(update1, update2);
        flushAndClearSession();
        
        Integer id1 = update1.getId();
        Integer id2 = update2.getId();
        
        List<ApplicationFormUpdate> result = dao.getUpdatesForUser(form, user);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
    }
    
    @Test
    public void shouldReturnNoUpdates() {
        DateTime sinceDate = new DateTime(2013, 2, 3, 00, 00);
        
        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
            .lastAccessTimestamp(sinceDate.toDate())
            .user(user)
            .applicationForm(form)
            .build();
    
        
        ApplicationFormUpdate update1 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.minusHours(1).toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        ApplicationFormUpdate update2 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        
        save(update1, update2, lastAccess);
        flushAndClearSession();
        
        List<ApplicationFormUpdate> result = dao.getUpdatesForUser(form, user);
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void shouldReturnUpdatesSinceSpecificDate2() {
        DateTime sinceDate = new DateTime(2013, 2, 3, 00, 00);
        
        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
            .lastAccessTimestamp(sinceDate.toDate())
            .user(user)
            .applicationForm(form)
            .build();
    
        
        ApplicationFormUpdate update1 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.plusHours(1).toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        ApplicationFormUpdate update2 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.plusHours(2).toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        ApplicationFormUpdate update3 = new ApplicationFormUpdateBuilder()
            .applicationForm(form)
            .updateTimestamp(sinceDate.minusHours(1).toDate())
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .build();
        
        save(update1, update2, update3, lastAccess);
        flushAndClearSession();
        
        Integer id1 = update1.getId();
        Integer id2 = update2.getId();
        
        List<ApplicationFormUpdate> result = dao.getUpdatesForUser(form, user);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
    }
    
}
