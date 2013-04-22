package com.zuehlke.pgadmissions.scoring;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;

public class ScoresPropertyEditorTest {

    private ScoresPropertyEditor editor;

    @Test
    public void shouldReturnScoresForJsonResponse() {
        String jsonResponse = "[{\"questionType\":\"TEXT\",\"question\":\"question1\",\"textResponse\":\"answer1\"}, {\"questionType\":\"DATE_RANGE\",\"question\":\"question2\",\"dateResponse\":\"01 Apr 2012\",\"secondDateResponse\":\"01 Apr 2013\"}]";
        editor.setAsText(jsonResponse);

        Object value = editor.getValue();
        value.getClass();
    }

    @Before
    public void setup() {
        editor = new ScoresPropertyEditor();
    }

}
