package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class SupervisorPropertyEditorTest {

    private UserService userServiceMock;
    private SupervisorPropertyEditor editor;
    private ApplicationsService applicationsServiceMock;
    private EncryptionHelper encryptionHelper;

    @Test
    public void shouldCreateNewSupervisorWithUserAndSetAsValue() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
        EasyMock.replay(userServiceMock, applicationsServiceMock, encryptionHelper);

        editor.setAsText("2|enc");

        EasyMock.verify(userServiceMock, applicationsServiceMock, encryptionHelper);
        Supervisor supervisor = (Supervisor) editor.getValue();
        assertNull(supervisor.getId());
        assertEquals(user, supervisor.getUser());
        assertFalse(supervisor.getIsPrimary());
    }

    @Test
    public void shouldCreateNewPrimarySupervisorWithUserAndSetAsValue() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
        EasyMock.replay(userServiceMock, applicationsServiceMock, encryptionHelper);

        editor.setAsText("2|enc|primary");

        EasyMock.verify(userServiceMock, applicationsServiceMock, encryptionHelper);
        Supervisor supervisor = (Supervisor) editor.getValue();
        assertNull(supervisor.getId());
        assertEquals(user, supervisor.getUser());
        assertTrue(supervisor.getIsPrimary());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfNotCorrectFormat() {
        editor.setAsText("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAppFormIdNotCorretFormat() {
        editor.setAsText("bob|1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUseerIddNotCorretFormat() {
        editor.setAsText("2|b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfNoSuchUser() {
        EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(null);
        EasyMock.replay(userServiceMock, encryptionHelper);
        editor.setAsText("2|enc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfNoApplicationForm() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(null);
        EasyMock.replay(userServiceMock, applicationsServiceMock, encryptionHelper);
        editor.setAsText("2|enc");
    }

    @Test
    public void shouldReturNullIfStringIsNull() {
        editor.setAsText(null);
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturNullIfIdIsEmptyString() {
        editor.setAsText(" ");
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturnNullAsText() {
        editor.setValue(null);
        assertNull(editor.getAsText());
    }

    @Before
    public void setup() {
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
        editor = new SupervisorPropertyEditor(userServiceMock, applicationsServiceMock, encryptionHelper);
    }
}
