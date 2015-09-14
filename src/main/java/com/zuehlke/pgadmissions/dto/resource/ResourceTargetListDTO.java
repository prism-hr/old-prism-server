package com.zuehlke.pgadmissions.dto.resource;

import static com.zuehlke.pgadmissions.utils.PrismGeocodingUtils.getHaversineDistance;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.address.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.advert.Advert;

public class ResourceTargetListDTO extends TreeMap<ResourceTargetDTO, ResourceTargetDTO> {

    private static final long serialVersionUID = 6848775493494349499L;

    private Map<ResourceTargetDTO, ResourceTargetDTO> index = Maps.newHashMap();

    private BigDecimal bLat;

    private BigDecimal bLon;

    public ResourceTargetListDTO(Advert advert) {
        AddressCoordinates coordinates = advert.getAddress().getAddressCoordinates();
        if (coordinates != null) {
            bLat = coordinates.getLatitude();
            bLon = coordinates.getLongitude();
        }
    }

    public void add(ResourceTargetDTO value) {
        put(value, value);
    }

    @Override
    public ResourceTargetDTO put(ResourceTargetDTO key, ResourceTargetDTO value) {
        ResourceTargetDTO valueOld = index.get(key);
        if (valueOld == null) {
            value.setTargetingDistance(getHaversineDistance(bLat, bLon, value.getAddressCoordinateLatitude(), value.getAddressCoordinateLongitude()));
            ResourceTargetDTO valueParent = index.get(value.getParentResource());
            if (valueParent == null) {
                index.put(key, value);
                return super.put(key, value);
            }
            valueParent.getDepartments().add(value);
            return valueParent;
        }
        return valueOld;
    }

    @Override
    public void putAll(Map<? extends ResourceTargetDTO, ? extends ResourceTargetDTO> map) {
        map.entrySet().forEach(entry -> {
            put(entry.getKey(), entry.getValue());
        });
    }

}
