package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.EthnicityService;

public class EthnicityPropertyEditorTest {

	private EthnicityService ethnicityServiceMock;
	private EthnicityPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;

	@Before
	public void setup() {
		ethnicityServiceMock = EasyMock.createMock(EthnicityService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new EthnicityPropertyEditor(ethnicityServiceMock, encryptionHelperMock);
	}

	@Test
	public void shouldLoadByIdAndSetAsValue() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		
		Ethnicity ethnicity = new EthnicityBuilder().id(1).build();
		EasyMock.expect(ethnicityServiceMock.getEthnicityById(1)).andReturn(ethnicity);
		EasyMock.replay(ethnicityServiceMock,encryptionHelperMock);

		editor.setAsText("bob");
		assertEquals(ethnicity, editor.getValue());

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
		editor.setValue(new EthnicityBuilder().build());
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnEncryptedIdAsString() {
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		editor.setValue(new EthnicityBuilder().id(5).build());
		assertEquals("bob", editor.getAsText());
	}
}
