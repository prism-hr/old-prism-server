package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DisabilityService;

public class DisabilityPropertyEditorTest {

	private DisabilityService disabilityServiceMock;
	private DisabilityPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;

	@Before
	public void setup() {
		disabilityServiceMock = EasyMock.createMock(DisabilityService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new DisabilityPropertyEditor(disabilityServiceMock, encryptionHelperMock);
	}

	@Test
	public void shouldLoadByIdAndSetAsValue() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		Disability disability = new DisabilityBuilder().id(1).build();
		EasyMock.expect(disabilityServiceMock.getDisabilityById(1)).andReturn(disability);
		EasyMock.replay(disabilityServiceMock, encryptionHelperMock);

		editor.setAsText("bob");
		assertEquals(disability, editor.getValue());
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
		editor.setValue(new DisabilityBuilder().build());
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnEncryptedIdAsString() {
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		editor.setValue(new DisabilityBuilder().id(5).build());
		assertEquals("bob", editor.getAsText());
	}
}
