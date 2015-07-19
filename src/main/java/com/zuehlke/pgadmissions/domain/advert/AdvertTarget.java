package com.zuehlke.pgadmissions.domain.advert;

public abstract class AdvertTarget<T> extends AdvertAttribute<T> {

    public abstract Integer getValueId();

    public abstract String getTitle();

}
