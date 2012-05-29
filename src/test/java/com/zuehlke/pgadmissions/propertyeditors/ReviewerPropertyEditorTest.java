package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewerPropertyEditorTest {

	private UserService userServiceMock;
	private ReviewerPropertyEditor editor;
	private ApplicationsService applicationsServiceMock;


	@Test	
	public void shouldCreateNewReviewerWithUserAndSetAsValueIfUserNotReviewerInLatestRoundOfApplication(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock);
		
		editor.setAsText("2|1");
		Reviewer reviewer = (Reviewer) editor.getValue();
		assertNull(reviewer.getId());
		assertEquals(user, reviewer.getUser());
		
	}
	
	@Test	
	public void shouldReturnExistingReviewerIfUserReviewerInLatestRoundOfApplication(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(3).user(user).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).latestReviewRound(reviewRound).toApplicationForm();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock);
		
		editor.setAsText("2|1");
		Reviewer returnedReviewer = (Reviewer) editor.getValue();
		assertEquals(reviewer, returnedReviewer);
		
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNotCorrectFormat(){			
		editor.setAsText("1");			
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAppFormIdNotCorretFormat(){			
		editor.setAsText("bob|1");			
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfUseerIddNotCorretFormat(){			
		editor.setAsText("2|b");			
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoSuchUser(){			
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(null);
		EasyMock.replay(userServiceMock);		
		editor.setAsText("2|1");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoApplicationForm(){			
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);		
		editor.setAsText("2|1");
	}
	
	@Test	
	public void shouldReturNullIfStringIsNull(){			
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
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		editor = new ReviewerPropertyEditor(userServiceMock, applicationsServiceMock);
	}
}
