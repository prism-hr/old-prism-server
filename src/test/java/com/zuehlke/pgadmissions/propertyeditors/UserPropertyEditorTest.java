package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.UserService;

public class UserPropertyEditorTest {

	private UserService userServiceMock;
	private UserPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelperMock);
		
		editor.setAsText("bob");
		assertEquals(user, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){	
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andThrow(new IllegalArgumentException());
		EasyMock.replay(encryptionHelperMock);
		editor.setAsText("bob");			
	}
	
	@Test	
	public void shouldReturNullIfIdIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturNullIfIdIsBlanl(){			
		editor.setAsText("");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new RegisteredUserBuilder().toUser());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnEncryptedIdAsString(){
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		editor.setValue(new RegisteredUserBuilder().id(5).toUser());
		assertEquals("bob", editor.getAsText());
	}
	
	@Before
	public void setup(){
		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new UserPropertyEditor(userServiceMock,encryptionHelperMock);
	}
}
