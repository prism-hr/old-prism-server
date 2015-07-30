package com.zuehlke.pgadmissions.domain.location;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.Address;

import java.util.List;

public abstract class GeocodableLocation extends Address {

    public abstract Coordinates getCoordinates();

    public abstract void setCoordinates(Coordinates coordinates);

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
