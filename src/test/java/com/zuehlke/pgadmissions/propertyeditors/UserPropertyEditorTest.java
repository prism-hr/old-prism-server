package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class UserPropertyEditorTest {
    
	private UserPropertyEditor editor;
	
	private EncryptionHelper encryptionHelperMock;
	

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat() {
		editor.setAsText("{email: 'test@gmail.com' }");
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
		EasyMock.expect(encryptionHelperMock.encrypt(121)).andReturn("encrypted");
		EasyMock.replay(encryptionHelperMock);

		editor.setValue(new UserBuilder().firstName("Mark").id(121).lastName("Johnson").email("test@gmail.com").build());

		assertEquals("{\"id\": \"encrypted\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\"}", editor.getAsText());
		EasyMock.verify(encryptionHelperMock);
	}

}
