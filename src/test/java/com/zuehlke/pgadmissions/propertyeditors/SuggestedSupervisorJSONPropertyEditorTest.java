package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class SuggestedSupervisorJSONPropertyEditorTest {

    private SuggestedSupervisorJSONPropertyEditor editor;
    private EncryptionHelper encryptionHelperMock;

    @Test
    public void shouldParseAndSetAsValue() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
        EasyMock.replay(encryptionHelperMock);
        editor.setAsText("{\"id\": \"bob\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" , \"awareSupervisor\": \"YES\"}");
        SuggestedSupervisor expected = new SuggestedSupervisor().withUser(
                new User().withId(1).withFirstName("Mark").withLastName("Johnson").withEmail("test@gmail.com")).withAware(true);
        SuggestedSupervisor suggestedSupervisor = (SuggestedSupervisor) editor.getValue();
        assertEquals(expected.getUser().getFirstName(), suggestedSupervisor.getUser().getFirstName());
        assertEquals(expected.getUser().getLastName(), suggestedSupervisor.getUser().getLastName());
        assertEquals(expected.getUser().getEmail(), suggestedSupervisor.getUser().getEmail());
        assertEquals(expected.isAware(), suggestedSupervisor.isAware());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat() {
        editor.setAsText("{email: 'test@gmail.com' awareSupervisor: 'YES'}");
    }

    @Test
    public void shouldReturNullIfStringIsNull() {
        editor.setAsText(null);
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturNullIfStringIsEmpty() {
        editor.setAsText("");
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturnNullIfValueIsNull() {
        editor.setValue(null);
        assertNull(editor.getAsText());
    }

    @Test
    public void shouldReturnCorrectjsonString() {
        EasyMock.expect(encryptionHelperMock.encrypt(1)).andReturn("bob");
        EasyMock.replay(encryptionHelperMock);
        editor.setValue(new SuggestedSupervisor().withUser(new User().withFirstName("Mark").withId(1).withLastName("Johnson").withEmail("test@gmail.com"))
                .withAware(false));
        assertEquals("{\"id\": \"bob\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\", \"awareSupervisor\": \"NO\"}",
                editor.getAsText());
    }

    @Before
    public void setup() {
        editor = new SuggestedSupervisorJSONPropertyEditor();
    }
}
