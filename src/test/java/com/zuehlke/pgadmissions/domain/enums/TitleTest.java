package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class TitleTest {

    @Test
    public void shouldOutputCorrectDisplayValues(){
        Assert.assertEquals("Mr", Title.MR.getDisplayValue());
        Assert.assertEquals("Mrs", Title.MRS.getDisplayValue());
        Assert.assertEquals("European Engineer", Title.EUROPEAN_ENGINEER.getDisplayValue());
    }
}
