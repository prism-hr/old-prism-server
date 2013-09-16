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

import java.util.Arrays;
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
import com.zuehlke.pgadmissions.dao.ApplicationFormUpdateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormLastAccessBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUpdateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationFormAccessServiceTest {
    
    @Mock
    @InjectIntoByType
    ApplicationFormLastAccessDAO applicationFormLastAccessDaoMock;
    
    @Mock
    @InjectIntoByType
    ApplicationFormUpdateDAO applicationFormUpdateDaoMock;

    @TestedObject
    ApplicationFormAccessService service;
    
    
    @Test
    public void administratorShouldNeedToSeeApplicationBecuaseItHasBeenUpdated() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Program program = new ProgramBuilder().id(34).build();
        Role adminRole = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
            .programsOfWhichAdministrator(program)
            .role(adminRole)
            .build();
        ApplicationForm form = new ApplicationFormBuilder().id(2)
            .lastUpdated(lastUpdatedTimestamp)
            .program(program).build();
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
            .id(23)
            .updateVisibility(ApplicationUpdateScope.INTERNAL)
            .updateTimestamp(lastUpdatedTimestamp)
            .applicationForm(form)
            .build();
            
        expect(
                applicationFormUpdateDaoMock.getUpdatesForUser(form, user))
                .andReturn(Arrays.asList(update));
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void applicantShouldNotNeedToSeeApplicationBecuaseTheUpdateIsForInternalGroup() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Program program = new ProgramBuilder().id(34).build();
        Role applicantRole = new RoleBuilder().id(Authority.APPLICANT).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
                .role(applicantRole)
                .build();
        ApplicationForm form = new ApplicationFormBuilder().id(2)
                .lastUpdated(lastUpdatedTimestamp)
                .program(program).build();
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
        .id(23)
        .updateVisibility(ApplicationUpdateScope.INTERNAL)
        .updateTimestamp(lastUpdatedTimestamp)
        .applicationForm(form)
        .build();
        
        expect(
                applicationFormUpdateDaoMock.getUpdatesForUser(form, user))
                .andReturn(Arrays.asList(update));
        
        replay();
        assertFalse(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void applicantShouldNeedToSeeApplicationBecuaseTheUpdateIsForAllUsers() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Program program = new ProgramBuilder().id(34).build();
        Role applicantRole = new RoleBuilder().id(Authority.APPLICANT).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
                .role(applicantRole)
                .build();
        ApplicationForm form = new ApplicationFormBuilder().id(2)
                .lastUpdated(lastUpdatedTimestamp)
                .program(program).build();
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
        .id(23)
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .updateTimestamp(lastUpdatedTimestamp)
        .applicationForm(form)
        .build();
        
        expect(
                applicationFormUpdateDaoMock.getUpdatesForUser(form, user))
                .andReturn(Arrays.asList(update));
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void reviewerShouldNeedToSeeApplicationBecuaseTheUpdateIsForAllUsers() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Program program = new ProgramBuilder().id(34).build();
        Role reviewerRole = new RoleBuilder().id(Authority.REVIEWER).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
                .programsOfWhichReviewer(program)
                .role(reviewerRole)
                .build();
        ApplicationForm form = new ApplicationFormBuilder().id(2)
                .lastUpdated(lastUpdatedTimestamp)
                .program(program).build();
        ApplicationFormUpdate update = new ApplicationFormUpdateBuilder()
        .id(23)
        .updateVisibility(ApplicationUpdateScope.ALL_USERS)
        .updateTimestamp(lastUpdatedTimestamp)
        .applicationForm(form)
        .build();
        
        expect(
                applicationFormUpdateDaoMock.getUpdatesForUser(form, user))
                .andReturn(Arrays.asList(update));
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void applicantShouldNeedToSeeApplicationBecuaseOneOfTheUpdatesIsForAllUsers() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Program program = new ProgramBuilder().id(34).build();
        Role applicantRole = new RoleBuilder().id(Authority.APPLICANT).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
                .role(applicantRole)
                .build();
        ApplicationForm form = new ApplicationFormBuilder().id(2)
                .lastUpdated(lastUpdatedTimestamp)
                .program(program).build();
        ApplicationFormUpdate update1 = new ApplicationFormUpdateBuilder()
            .id(23)
            .updateVisibility(ApplicationUpdateScope.INTERNAL)
            .updateTimestamp(lastUpdatedTimestamp)
            .applicationForm(form)
            .build();
        ApplicationFormUpdate update2 = new ApplicationFormUpdateBuilder()
            .id(23)
            .updateVisibility(ApplicationUpdateScope.ALL_USERS)
            .updateTimestamp(lastUpdatedTimestamp)
            .applicationForm(form)
            .build();
        
        expect(
                applicationFormUpdateDaoMock.getUpdatesForUser(form, user))
                .andReturn(Arrays.asList(update1, update2));
        
        replay();
        assertTrue(service.userNeedsToSeeApplicationUpdates(form, user));
        verify();
    }
    
    @Test
    public void shouldCreateANewLastAccessEntry() {
        Date lastUpdatedTimestamp = new DateTime(2013, 5, 4, 0, 0).toDate();
        Date timestamp = new DateTime(2013, 5, 5, 0, 0).toDate();
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().id(2).lastUpdated(lastUpdatedTimestamp).build();

        expect(
                applicationFormLastAccessDaoMock.getLastAccess(form, user))
                .andReturn(null);

        Capture<ApplicationFormLastAccess> lastAccessCaptor = new Capture<ApplicationFormLastAccess>();
      
        applicationFormLastAccessDaoMock.saveAccess(and(isA(ApplicationFormLastAccess.class), capture(lastAccessCaptor)));
        
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
                applicationFormLastAccessDaoMock.getLastAccess(form, user))
                .andReturn(lastAccess);
        
        Capture<ApplicationFormLastAccess> lastAccessCaptor = new Capture<ApplicationFormLastAccess>();
        
        applicationFormLastAccessDaoMock.saveAccess(and(isA(ApplicationFormLastAccess.class), capture(lastAccessCaptor)));
        
        replay();
        service.updateAccessTimestamp(form, user, timestamp);
        verify();
        
        ApplicationFormLastAccess newRecord = lastAccessCaptor.getValue();
        assertEquals(form.getId(), newRecord.getApplicationForm().getId());
        assertEquals(user.getId(), newRecord.getUser().getId());
        assertEquals(0, timestamp.compareTo(newRecord.getLastAccessTimestamp()));
    }

}
