package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationAddressRepresentationApplication extends AddressRepresentation {
    
    private ImportedEntitySimpleRepresentation domicileMapping;

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

}
