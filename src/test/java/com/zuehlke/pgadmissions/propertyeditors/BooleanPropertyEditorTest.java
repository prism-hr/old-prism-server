package com.zuehlke.pgadmissions.propertyeditors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class BooleanPropertyEditorTest {
	private BooleanPropertyEditor editor;

	@Before
	public void setup() {
		editor = new BooleanPropertyEditor();
	}

	@Test
	public void parseTrue() {
		editor.setAsText("tRuE");
		Assert.assertEquals(Boolean.class, editor.getValue().getClass());
		Assert.assertTrue((Boolean)editor.getValue());
	}

	@Test
	public void parseFalse() {
		editor.setAsText("FaLsE");
		Assert.assertEquals(Boolean.class, editor.getValue().getClass());
		Assert.assertFalse((Boolean)editor.getValue());
	}

	@Test
	public void parseNo() {
		editor.setAsText("no");
		Assert.assertEquals(Boolean.class, editor.getValue().getClass());
		Assert.assertFalse((Boolean)editor.getValue());
	}
	
	@Test
	public void parseNoText() {
		editor.setAsText("  ");
		Assert.assertEquals(null, editor.getValue());
	}
	
	@Test
	public void parseNullAsString() {
		editor.setAsText("null");
		Assert.assertEquals(null, editor.getValue());
	}
	
	@Test
	public void parseNull() {
		editor.setAsText(null);
		Assert.assertEquals(null, editor.getValue());
	}
}
