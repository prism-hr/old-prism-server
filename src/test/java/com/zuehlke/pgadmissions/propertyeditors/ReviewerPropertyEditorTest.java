package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewerPropertyEditorTest {

	private UserService userServiceMock;
	private ReviewerPropertyEditor editor;


	@Test	
	public void shouldCreateNewReviewerWithUserAndSetAsValue(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock);
		
		editor.setAsText("1");
		Reviewer interviewer = (Reviewer) editor.getValue();
		assertNull(interviewer.getId());
		assertEquals(user, interviewer.getUser());
		
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){			
		editor.setAsText("bob");			
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoSuchUser(){			
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(null);
		EasyMock.replay(userServiceMock);		
		editor.setAsText("1");
	}
	
	@Test	
	public void shouldReturNullIfIdIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturNullIfIdIsEmptyString(){			
		editor.setAsText(" ");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullAsText(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	
	@Before
	public void setup(){
		userServiceMock = EasyMock.createMock(UserService.class);
		editor = new ReviewerPropertyEditor(userServiceMock);
	}
}
