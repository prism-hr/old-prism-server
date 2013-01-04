package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.RejectService;

public class RejectReasonPropertyEditorTest {
	

	private RejectService rejectServiceMock;
	private RejectReasonPropertyEditor editor;
	private EncryptionHelper encryptionHelperMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		RejectReason rejectReason = new RejectReasonBuilder().id(1).build();
		EasyMock.expect(rejectServiceMock.getRejectReasonById(1)).andReturn(rejectReason);
		EasyMock.replay(rejectServiceMock,encryptionHelperMock);
		
		editor.setAsText("bob");
		assertEquals(rejectReason, editor.getValue());
		
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
		editor.setValue(new RejectReasonBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnEncryptedIdAsString(){
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.replay(encryptionHelperMock);
		editor.setValue(new RejectReasonBuilder().id(5).build());
		assertEquals("bob", editor.getAsText());
	}
	
	@Before
	public void setup(){
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		rejectServiceMock = EasyMock.createMock(RejectService.class);
		editor = new RejectReasonPropertyEditor(rejectServiceMock,encryptionHelperMock);
	}

}
