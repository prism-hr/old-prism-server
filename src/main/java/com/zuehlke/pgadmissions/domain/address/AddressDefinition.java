package com.zuehlke.pgadmissions.domain.address;

public abstract class AddressDefinition<T> implements uk.co.alumeni.prism.api.model.resource.AddressDefinition<T> {

    public abstract Integer getId();

    public abstract void setId(Integer id);

}
