package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

public class PlainTextUserPropertyEditorTest {

	private UserService userServiceMock;
	private PlainTextUserPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock);
		
		editor.setAsText("1");
		assertEquals(user, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){			
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
	public void shouldReturnIsAsString(){			
		editor.setValue(new RegisteredUserBuilder().id(5).toUser());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		userServiceMock = EasyMock.createMock(UserService.class);
		editor = new PlainTextUserPropertyEditor(userServiceMock);
	}
}
