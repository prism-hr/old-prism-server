package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class AddressRepresentation {
    
    private ImportedEntitySimpleRepresentation domicileMapping;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    public Integer getDomicile() {
        return domicileMapping.getId();
    }

    public void setDomicile(Integer domicile) {
        this.domicileMapping = new ImportedEntitySimpleRepresentation().withId(domicile);
    }

    public ImportedEntitySimpleRepresentation getDomicileMapping() {
        return domicileMapping;
    }

    public void setDomicileMapping(ImportedEntitySimpleRepresentation domicileMapping) {
        this.domicileMapping = domicileMapping;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }
}
