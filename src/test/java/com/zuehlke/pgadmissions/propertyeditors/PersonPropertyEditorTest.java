package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class PersonPropertyEditorTest {
	private PersonPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;

	@Test
	public void shouldParseAndSetAsValue() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(234);
		EasyMock.replay(encryptionHelperMock);

		editor.setAsText("{\"id\": \"enc\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" }");
		
		EasyMock.verify(encryptionHelperMock);
		Person expected = new PersonBuilder().id(234).firstname("Mark").lastname("Johnson").email("test@gmail.com").toPerson();
		Person registryUser = (Person) editor.getValue();

		assertEquals(expected.getFirstname(), registryUser.getFirstname());
		assertEquals(expected.getLastname(), registryUser.getLastname());
		assertEquals(expected.getEmail(), registryUser.getEmail());
	}

	@Test
	public void shouldParseEmptyIdAsNull() {
		editor.setAsText("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\"}");
		Person registryUser = (Person) editor.getValue();
		assertNull(registryUser.getId());
	}

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

		editor.setValue(new PersonBuilder().firstname("Mark").id(121).lastname("Johnson").email("test@gmail.com").toPerson());

		assertEquals("{\"id\": \"encrypted\",\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\"}", editor.getAsText());
		EasyMock.verify(encryptionHelperMock);
	}

	@Before
	public void setup() {
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new PersonPropertyEditor(encryptionHelperMock);
	}
}
