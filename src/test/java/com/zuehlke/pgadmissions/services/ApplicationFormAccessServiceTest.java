package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.easymock.Capture;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormLastAccessDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormLastAccessBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationFormAccessServiceTest {
    
    @Mock
    @InjectIntoByType
    ApplicationFormLastAccessDAO daoMock;

    @TestedObject
    ApplicationFormAccessService service;
    
    
    
    @Test
    public void userShouldNeedToSeeApplicationBecauseItHasNeverSeenIt() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).build();
        expect(
               daoMock.getLastAccess(form, user))
        .andReturn(null);
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void userShouldNeedToSeeApplicationBecuaseItHasBeenUpdated() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Date lastAccessTimestamp = new DateTime(2013, 5, 3, 0, 0).toDate();
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).lastUpdated(lastUpdatedTimestamp).build();
        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
            .id(3)
            .lastAccessTimestamp(lastAccessTimestamp )
            .user(user)
            .applicationForm(form)
            .build();
            
        expect(
                daoMock.getLastAccess(form, user))
                .andReturn(lastAccess);
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void userShouldNotNeedToSeeApplicationBecuaseItHasBeenUpdated() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Date lastAccessTimestamp = new DateTime(2013, 5, 5, 0, 0).toDate();
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).lastUpdated(lastUpdatedTimestamp).build();
        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
        .id(3)
        .lastAccessTimestamp(lastAccessTimestamp )
        .user(user)
        .applicationForm(form)
        .build();
        
        expect(
                daoMock.getLastAccess(form, user))
                .andReturn(lastAccess);
        
        replay();
        assertFalse(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void shouldCreateANewLastAccessEntry() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Date timestamp = new DateTime(2013, 5, 5, 0, 0).toDate();
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).lastUpdated(lastUpdatedTimestamp).build();

        expect(
                daoMock.getLastAccess(form, user))
                .andReturn(null);

        Capture<ApplicationFormLastAccess> lastAccessCaptor = new Capture<ApplicationFormLastAccess>();
      
        daoMock.saveAccess(and(isA(ApplicationFormLastAccess.class), capture(lastAccessCaptor)));
        
        replay();
        service.updateAccessTimestamp(form, user, timestamp);
        verify();
        
        ApplicationFormLastAccess newRecord = lastAccessCaptor.getValue();
        assertEquals(form.getId(), newRecord.getApplicationForm().getId());
        assertEquals(user.getId(), newRecord.getUser().getId());
        assertEquals(0, timestamp.compareTo(newRecord.getLastAccessTimestamp()));
    }
    
    @Test
    public void shouldUpdateExistingLastAccessEntry() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Date timestamp = new DateTime(2013, 5, 5, 0, 0).toDate();
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).lastUpdated(lastUpdatedTimestamp).build();
        ApplicationFormLastAccess lastAccess = new ApplicationFormLastAccessBuilder()
        .id(3)
        .lastAccessTimestamp(timestamp )
        .user(user)
        .applicationForm(form)
        .build();
        
        expect(
                daoMock.getLastAccess(form, user))
                .andReturn(lastAccess);
        
        Capture<ApplicationFormLastAccess> lastAccessCaptor = new Capture<ApplicationFormLastAccess>();
        
        daoMock.saveAccess(and(isA(ApplicationFormLastAccess.class), capture(lastAccessCaptor)));
        
        replay();
        service.updateAccessTimestamp(form, user, timestamp);
        verify();
        
        ApplicationFormLastAccess newRecord = lastAccessCaptor.getValue();
        assertEquals(form.getId(), newRecord.getApplicationForm().getId());
        assertEquals(user.getId(), newRecord.getUser().getId());
        assertEquals(0, timestamp.compareTo(newRecord.getLastAccessTimestamp()));
    }

}
