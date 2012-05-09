package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.services.ReviewerService;
import com.zuehlke.pgadmissions.services.UserService;

public class ReviewerJSONPropertyEditorTest {
	private ReviewerJSONPropertyEditor editor;
	private ReviewerService reviewerServiceMock;
	private UserService userServiceMock;

	
	
	@Test	
	public void shouldReturNullIfStringIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturNullIfStringIsEmpty(){			
		editor.setAsText("");
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
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
	public void shouldReturnCorrectjsonString(){			
		editor.setValue(new ReviewerBuilder().id(1).toReviewer());
		assertEquals("{\"id\": \"1\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		reviewerServiceMock = EasyMock.createMock(ReviewerService.class);
		userServiceMock  = EasyMock.createMock(UserService.class);
		editor = new ReviewerJSONPropertyEditor(reviewerServiceMock, userServiceMock);
	}
}
