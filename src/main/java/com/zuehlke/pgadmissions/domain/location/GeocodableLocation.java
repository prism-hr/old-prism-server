package com.zuehlke.pgadmissions.domain.location;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.Address;

public abstract class GeocodableLocation extends Address {

    public abstract GeographicLocation getLocation();

    public abstract void setLocation(GeographicLocation location);

    public List<String> getLocationTokens() {
        List<String> tokens = Lists.newLinkedList();

        tokens.add(getAddressLine1());

        String addressLine2 = getAddressLine2();
        if (addressLine2 != null) {
            tokens.add(addressLine2);
        }

        tokens.add(getAddressTown());

        String addressRegion = getAddressRegion();
        if (addressRegion != null) {
            tokens.add(addressRegion);
        }

        String addressCode = getAddressCode();
        if (addressCode != null) {
            tokens.add(addressCode);
        }

        return tokens;
    }

}
