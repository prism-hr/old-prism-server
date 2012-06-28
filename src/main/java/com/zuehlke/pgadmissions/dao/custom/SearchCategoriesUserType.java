package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.SearchCategory;

public class SearchCategoriesUserType extends EnumUserType<SearchCategory> {
	public SearchCategoriesUserType() {
		super(SearchCategory.class);

	}
}

