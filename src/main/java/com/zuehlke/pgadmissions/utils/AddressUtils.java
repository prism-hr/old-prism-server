package com.zuehlke.pgadmissions.utils;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.Address;

public final class AddressUtils {

    private AddressUtils() {
    }

    public static boolean addressesEqual(Address contactAddress, Address currentAddress) {
        return contactAddress != null && currentAddress != null
                && contactAddress.getDomicile().getId().equals(currentAddress.getDomicile().getId())
                && StringUtils.equals(contactAddress.getAddressLine1(), currentAddress.getAddressLine1())
                && StringUtils.equals(contactAddress.getAddressLine2(), currentAddress.getAddressLine2())
                && StringUtils.equals(contactAddress.getAddressTown(), currentAddress.getAddressTown())
                && StringUtils.equals(contactAddress.getAddressRegion(), currentAddress.getAddressRegion())
                && StringUtils.equals(contactAddress.getAddressCode(), currentAddress.getAddressCode());
    }
}
