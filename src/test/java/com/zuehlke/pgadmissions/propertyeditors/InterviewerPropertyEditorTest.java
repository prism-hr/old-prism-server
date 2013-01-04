package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.UserService;

public class InterviewerPropertyEditorTest {

	private UserService userServiceMock;
	private InterviewerPropertyEditor editor;
	private EncryptionHelper encryptionHelper;

	@Test
	public void shouldCreateNewInterviewerWithUserAndSetAsValue() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelper);

		editor.setAsText("enc");
		Interviewer interviewer = (Interviewer) editor.getValue();

		EasyMock.verify(userServiceMock, encryptionHelper);
		assertNull(interviewer.getId());
		assertEquals(user, interviewer.getUser());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotDecryptable() {
		EasyMock.expect(encryptionHelper.decryptToInteger("bob")).andThrow(new IllegalAccessException("intentional..."));
		editor.setAsText("bob");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoSuchUser() {
		EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(null);
		EasyMock.replay(userServiceMock, encryptionHelper);
		editor.setAsText("enc");
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
	public void shouldReturnNullAsText() {
		editor.setValue(null);
		assertNull(editor.getAsText());
	}

	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
		editor = new InterviewerPropertyEditor(userServiceMock, encryptionHelper);
	}
}
