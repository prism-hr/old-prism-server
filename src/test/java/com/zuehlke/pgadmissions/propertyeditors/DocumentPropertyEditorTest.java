package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DocumentService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DocumentPropertyEditorTest {

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelperMock;

    @TestedObject
    private DocumentPropertyEditor editor;

    @Test
    public void shouldLoadByIdAndSetAsValue() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
        Document document = new Document().withId(1);
        EasyMock.expect(documentServiceMock.getByid(1)).andReturn(document);
        EasyMock.replay(encryptionHelperMock, documentServiceMock);

        editor.setAsText("bob");
        assertEquals(document, editor.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfIdNotInteger() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andThrow(new IllegalArgumentException());
        EasyMock.replay(encryptionHelperMock);
        editor.setAsText("bob");
    }

    @Test
    public void shouldReturNullIfIdIsNull() {
        editor.setAsText(null);
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturNullIfIdIsEmptyString() {
        editor.setAsText(" ");
        assertNull(editor.getValue());
    }

    @Test
    public void shouldReturnNullIfValueIsNull() {
        editor.setValue(null);
        assertNull(editor.getAsText());
    }

    @Test
    public void shouldReturnNullIfValueIdIsNull() {
        editor.setValue(new Document());
        assertNull(editor.getAsText());
    }

    @Test
    public void shouldReturnEncryptedIdAsString() {
        editor.setValue(new Document().withId(5));
        EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
        EasyMock.replay(encryptionHelperMock);
        assertEquals("bob", editor.getAsText());
    }

}
