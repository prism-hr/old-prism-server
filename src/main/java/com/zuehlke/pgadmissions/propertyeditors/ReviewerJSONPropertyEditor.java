package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.services.ReviewerService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ReviewerJSONPropertyEditor extends PropertyEditorSupport {
	
	private final ReviewerService reviewerService;
	private final UserService userService;
	

	ReviewerJSONPropertyEditor() {
		this(null, null);
	
	}
	@Autowired
	public ReviewerJSONPropertyEditor(ReviewerService reviewerService, UserService userService) {
		this.reviewerService = reviewerService;
		this.userService = userService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setAsText(String jsonStirng) throws IllegalArgumentException {
		try {
			if (jsonStirng == null || StringUtils.isBlank(jsonStirng)) {
				setValue(null);
				return;
			}
			ObjectMapper objectMapper = new ObjectMapper();

			Map<String, Object> properties = objectMapper.readValue(jsonStirng,
					Map.class);
			Reviewer reviewer = new Reviewer();
			if (StringUtils.isNotBlank((String) properties.get("id"))) {
						reviewer = reviewerService.getReviewerByUser(userService.getUser(Integer.parseInt((String) properties.get("id"))));
			}
			setValue(reviewer);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return null;
		}
		Reviewer reviewer = (Reviewer) getValue();
		return "{\"id\": \""
				+ reviewer.getId() + "\"}";
	}
}
