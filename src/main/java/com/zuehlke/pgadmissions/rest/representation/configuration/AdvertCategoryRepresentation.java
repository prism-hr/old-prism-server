package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;

public class AdvertCategoryRepresentation {

    private PrismAdvertCategory id;

    private boolean hasFee;

    private boolean hasPay;

    private List<PrismAdvertType> advertTypes;

    public PrismAdvertCategory getId() {
        return id;
    }

    public void setId(PrismAdvertCategory id) {
        this.id = id;
    }

    public boolean isHasFee() {
        return hasFee;
    }

    public void setHasFee(boolean hasFee) {
        this.hasFee = hasFee;
    }

    public boolean isHasPay() {
        return hasPay;
    }

    public void setHasPay(boolean hasPay) {
        this.hasPay = hasPay;
    }

    public List<PrismAdvertType> getAdvertTypes() {
        return advertTypes;
    }

    public void setAdvertTypes(List<PrismAdvertType> advertTypes) {
        this.advertTypes = advertTypes;
    }

}
