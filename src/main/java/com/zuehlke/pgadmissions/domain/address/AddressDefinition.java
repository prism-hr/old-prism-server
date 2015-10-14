package com.zuehlke.pgadmissions.domain.address;

public abstract class AddressDefinition<T> {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getAddressLine1();

    public abstract void setAddressLine1(String addressLine1);

    public abstract String getAddressLine2();

    public abstract void setAddressLine2(String addressLine2);

    public abstract String getAddressTown();

    public abstract void setAddressTown(String addressTown);

    public abstract String getAddressRegion();

    public abstract void setAddressRegion(String addressRegion);

    public abstract String getAddressCode();

    public abstract void setAddressCode(String addressCode);

    public abstract T getDomicile();

    public abstract void setDomicile(T domicile);

}
