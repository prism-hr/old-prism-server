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

public class PlainTextUserPropertyEditorTest {

	private UserService userServiceMock;
	private PlainTextUserPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();

		EasyMock.expect(encryptionHelperMock.decryptToInteger("encd")).andReturn(121);
		EasyMock.expect(userServiceMock.getUser(121)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelperMock);
		
		editor.setAsText("encd");
		assertEquals(user, editor.getValue());
		
		EasyMock.verify(userServiceMock, encryptionHelperMock);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andThrow(new IllegalArgumentException("intentional..."));
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
		editor.setValue(new RegisteredUserBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIsAsString(){
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("encd");
		EasyMock.replay(encryptionHelperMock);
	
		editor.setValue(new RegisteredUserBuilder().id(5).build());
		assertEquals("encd", editor.getAsText());
		
		EasyMock.verify(encryptionHelperMock);
	}
	
	@Before
	public void setup(){
		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new PlainTextUserPropertyEditor(userServiceMock, encryptionHelperMock);
	}
}
