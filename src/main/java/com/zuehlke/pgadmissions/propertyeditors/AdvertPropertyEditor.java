package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

import com.google.common.base.CharMatcher;

@Component
public class AdvertPropertyEditor extends PropertyEditorSupport {
    
    @Override
    public void setAsText(String untrimmedHTML) {
        setValue(CharMatcher.BREAKING_WHITESPACE.removeFrom(untrimmedHTML));
    }
    
}