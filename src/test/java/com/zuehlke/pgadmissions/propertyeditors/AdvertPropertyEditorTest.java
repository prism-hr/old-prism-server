package com.zuehlke.pgadmissions.propertyeditors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class AdvertPropertyEditorTest {
	
    private AdvertPropertyEditor editor;

	@Before
	public void setup() {
		editor = new AdvertPropertyEditor();
	}

	@Test
	public void parseString() {
		editor.setAsText("line\nline");
		Assert.assertEquals(editor.getValue(), "lineline");
	}
	
	@Test
    public void parseNullString() {
        editor.setAsText(null);
        Assert.assertEquals(editor.getValue(), null);
    }

	@Test
	public void parseEmptyString() {
	    editor.setAsText("");
        Assert.assertEquals(editor.getValue(), "");
	}
	
}