package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class JsonPropertyEditor extends PropertyEditorSupport {

    private Class<?> targetClass;

    @Override
    public void setAsText(String jsonString) throws IllegalArgumentException {
        Gson gson = new Gson();
        setValue(gson.fromJson(jsonString, targetClass));
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.toJson(getValue());
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

}