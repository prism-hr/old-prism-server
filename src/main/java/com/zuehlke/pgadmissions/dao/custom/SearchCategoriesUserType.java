package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.SearchCategories;

public class SearchCategoriesUserType extends EnumUserType<SearchCategories> {
	public SearchCategoriesUserType() {
		super(SearchCategories.class);

	}
}

