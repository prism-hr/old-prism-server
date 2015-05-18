package com.zuehlke.pgadmissions.domain.definitions;

public interface PrismConfigurationCategorizable <T extends Enum<T>> {

	public T getCategory();

}
