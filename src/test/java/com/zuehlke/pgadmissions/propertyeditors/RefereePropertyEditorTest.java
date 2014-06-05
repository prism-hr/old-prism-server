package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereePropertyEditorTest {

    private RefereeService refereeServiceMock;
    private RefereePropertyEditor editor;
    private EncryptionHelper encryptionHelper;

    @Test
    public void shouldLoadByIdAndSetAsValue() {
        ApplicationReferee referee = new ApplicationReferee();
        EasyMock.expect(encryptionHelper.decrypt("1")).andReturn("1");
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);
        EasyMock.replay(refereeServiceMock, encryptionHelper);
        editor.setAsText("1");
        assertEquals(referee, editor.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfIdNotInteger() {
        editor.setAsText("bob");
    }

    @Test
    public void shouldReturNullIfIdIsNull() {
        editor.setAsText(null);
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturnNullIfValueIsNull() {
        editor.setValue(null);
        assertNull(editor.getAsText());
    }

    @Test
    public void shouldReturnNullIfValueIdIsNull() {
        editor.setValue(new ApplicationReferee());
        assertNull(editor.getAsText());
    }

    @Test
    public void shouldReturnIsAsString() {
        editor.setValue(new ApplicationReferee().withId(5));
        assertEquals("5", editor.getAsText());
    }

    @Before
    public void setup() {
        encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        editor = new RefereePropertyEditor(refereeServiceMock, encryptionHelper);
    }
}
