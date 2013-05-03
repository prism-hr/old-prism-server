package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zuehlke.pgadmissions.domain.Score;

@Component
public class ScoresPropertyEditor extends PropertyEditorSupport{

	public static final String DATE_FORMAT_ON_CLIENT_SIDE = "dd MMM yyyy";
	private Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT_ON_CLIENT_SIDE).create();

    @Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		final Type scoresListType = new TypeToken<List<Score>>(){}.getType();
		
		final List<Score> scores = gson.fromJson(strId, scoresListType);
        setValue(scores);
		
	}
}
