package uk.co.alumeni.prism.domain.address;

import static org.apache.commons.lang3.ObjectUtils.compare;

public abstract class AddressDefinition<T extends Comparable<T>> implements Comparable<AddressDefinition<T>> {

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

    @Override
    public int compareTo(AddressDefinition<T> other) {
        int compare = compare(getDomicile(), other.getDomicile());
        compare = compare == 0 ? compare(getAddressCode(), other.getAddressCode()) : compare;
        compare = compare == 0 ? compare(getAddressRegion(), other.getAddressRegion()) : compare;
        compare = compare == 0 ? compare(getAddressTown(), other.getAddressTown()) : compare;
        compare = compare == 0 ? compare(getAddressLine2(), other.getAddressLine2()) : compare;
        compare = compare == 0 ? compare(getAddressLine1(), other.getAddressLine1()) : compare;
        return compare;
    }

}
