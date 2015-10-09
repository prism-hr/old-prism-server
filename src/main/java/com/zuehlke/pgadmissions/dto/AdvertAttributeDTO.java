package com.zuehlke.pgadmissions.dto;

public abstract class AdvertAttributeDTO<T extends Enum<?>> {

    public abstract Integer getAdvertId();

    public abstract void setAdvertId(Integer integer);

    public abstract T getAttribute();

    public abstract void setAttribute(T attribute);

}
