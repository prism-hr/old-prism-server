package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class AssignReviewersReviewerPropertyEditorTest {

	private UserService userServiceMock;
	private AssignReviewersReviewerPropertyEditor editor;
	private ApplicationsService applicationsServiceMock;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldCreateNewReviewerWithUserAndSetAsValueIfUserNotReviewerInLatestRoundOfApplication(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock, encryptionHelperMock);
		
		editor.setAsText("2|bob");
		Reviewer reviewer = (Reviewer) editor.getValue();
		assertNull(reviewer.getId());
		assertEquals(user, reviewer.getUser());
		
	}
	
	@Test	
	public void shouldReturnExistingReviewerIfUserReviewerInLatestRoundOfApplication(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		Reviewer reviewer = new ReviewerBuilder().id(3).user(user).build();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).latestReviewRound(reviewRound).build();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock,encryptionHelperMock);
		
		editor.setAsText("2|bob");
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
	public void shouldThrowIllegalArgumentExceptionIfUserIdNotCorrectFormat(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("b")).andThrow(new IllegalArgumentException());
		editor.setAsText("2|b");			
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfNoSuchUser(){			
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(null);
		EasyMock.replay(userServiceMock, encryptionHelperMock);		
		editor.setAsText("2|bob");
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
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		editor = new AssignReviewersReviewerPropertyEditor(userServiceMock, applicationsServiceMock,encryptionHelperMock);
	}
}
