package com.zuehlke.pgadmissions.rest.representation.configuration;

public interface PrismConfigurationRepresentationCategorizable <T extends Enum<T>> {

	public T getCategory();
	
	public void setCategory(T category);

}