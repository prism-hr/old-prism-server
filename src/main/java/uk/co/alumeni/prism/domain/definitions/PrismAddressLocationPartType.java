package uk.co.alumeni.prism.domain.definitions;

import static org.apache.commons.lang.ArrayUtils.contains;

import java.util.List;

public enum PrismAddressLocationPartType {

    COUNTRY("country"), //
    REGION("administrative_area_level_1", "administrative_area_level_2", "administrative_area_level_3", "administrative_area_level_4", "administrative_area_level_5", "political"), //
    LOCALITY("locality", "sublocality", "sublocality_level_1", "sublocality_level_2", "sublocality_level_3", "sublocality_level_4", "sublocality_level_5", "neighborhood"), //
    COMPLEX("premise", "subpremise", "airport");

    private String[] googleTypes;

    private PrismAddressLocationPartType(String... googleTypes) {
        this.googleTypes = googleTypes;
    }

    public String[] getGoogleTypes() {
        return googleTypes;
    }

    public static PrismAddressLocationPartType getAddressLocationPartType(List<String> googleTypes) {
        for (PrismAddressLocationPartType partType : values()) {
            String[] partGoogleTypes = partType.getGoogleTypes();
            for (String googleType : googleTypes) {
                if (partGoogleTypes != null && contains(partType.getGoogleTypes(), googleType.toLowerCase())) {
                    return partType;
                }
            }
        }
        return null;
    }

}
