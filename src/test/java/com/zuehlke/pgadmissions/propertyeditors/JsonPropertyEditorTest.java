package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;


public class JsonPropertyEditorTest {
    
	private JsonPropertyEditor editor;

	@Test	
	public void shouldParseAndSetAsValue(){
		Integer duration = 1;
		editor.setAsText("{\"stage\": \"VALIDATION\",\"duration\": \""+ duration +"\",\"unit\": \"DAYS\"}");
//		StageDuration expected = new StageDurationBuilder().duration(1).unit(DurationUnitEnum.DAYS).stage(ApplicationFormStatus.VALIDATION).build();
//		StageDuration stageDuration = (StageDuration) editor.getValue();
//		assertEquals(expected.getStage(), stageDuration.getStage());
//		assertEquals(expected.getDuration(), stageDuration.getDuration());
//		assertEquals(expected.getUnit(), stageDuration.getUnit());
	}
	
	
	@Test(expected=JsonSyntaxException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{stage: 'stage' duration: 'string' unit: 'years'}");		
	}
	
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
	
	@Test	
	public void shouldReturnCorrectjsonString(){			
//		editor.setValue(new StageDurationBuilder().duration(1).unit(DurationUnitEnum.WEEKS).stage(ApplicationFormStatus.VALIDATION).build());
		assertEquals("{\"stage\":\"VALIDATION\",\"duration\":1,\"unit\":\"WEEKS\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		editor = new JsonPropertyEditor();
//		editor.setTargetClass(StageDuration.class);
	}
}
