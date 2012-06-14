package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereePropertyEditorTest {


		private RefereeService refereeServiceMock;
		private RefereePropertyEditor editor;


		@Test	
		public void shouldLoadByIdAndSetAsValue(){
			Referee referee = new RefereeBuilder().id(1).toReferee();
			EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
			EasyMock.replay(refereeServiceMock);
			
			editor.setAsText("1");
			assertEquals(referee, editor.getValue());
			
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
		public void shouldReturnNullIfValueIsNull(){			
			editor.setValue(null);
			assertNull(editor.getAsText());
		}
		
		@Test	
		public void shouldReturnNullIfValueIdIsNull(){			
			editor.setValue(new RefereeBuilder().toReferee());
			assertNull(editor.getAsText());
		}
		
		@Test	
		public void shouldReturnIsAsString(){			
			editor.setValue(new RefereeBuilder().id(5).toReferee());
			assertEquals("5", editor.getAsText());
		}
		
		@Before
		public void setup(){
			refereeServiceMock = EasyMock.createMock(RefereeService.class);
			editor = new RefereePropertyEditor(refereeServiceMock);
		}
	}
