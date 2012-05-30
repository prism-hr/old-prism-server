package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.services.RejectService;

public class RejectReasonPropertyEditorTest {
	

	private RejectService rejectServiceMock;
	private RejectReasonPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		RejectReason rejectReason = new RejectReasonBuilder().id(1).toRejectReason();
		EasyMock.expect(rejectServiceMock.getRejectReasonById(1)).andReturn(rejectReason);
		EasyMock.replay(rejectServiceMock);
		
		editor.setAsText("1");
		assertEquals(rejectReason, editor.getValue());
		
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
	public void shouldReturNullIfIdIsEmptyString(){			
		editor.setAsText(" ");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new RejectReasonBuilder().toRejectReason());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new RejectReasonBuilder().id(5).toRejectReason());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		rejectServiceMock = EasyMock.createMock(RejectService.class);
		editor = new RejectReasonPropertyEditor(rejectServiceMock);
	}

}
