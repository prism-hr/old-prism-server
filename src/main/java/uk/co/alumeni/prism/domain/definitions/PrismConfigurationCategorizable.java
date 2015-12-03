package uk.co.alumeni.prism.domain.definitions;

public interface PrismConfigurationCategorizable <T extends Enum<T>> {

	T getCategory();

}
