package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class InstitutionRepresentationSimple extends ResourceRepresentationSimple {

    private AddressAdvertRepresentation address;

    public AddressAdvertRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertRepresentation address) {
        this.address = address;
    }

    public InstitutionRepresentationSimple withAddress(final AddressAdvertRepresentation address) {
        this.address = address;
        return this;
    }

    public InstitutionRepresentationSimple withScope(final com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope scope) {
        setScope(scope);
        return this;
    }

    public InstitutionRepresentationSimple withId(final Integer id) {
        setId(id);
        return this;
    }

    public InstitutionRepresentationSimple withCode(final String code) {
        setCode(code);
        return this;
    }

    public InstitutionRepresentationSimple withImportedCode(final String importedCode) {
        setImportedCode(importedCode);
        return this;
    }

    public InstitutionRepresentationSimple withTitle(final String title) {
        setTitle(title);
        return this;
    }

    public InstitutionRepresentationSimple withLogoImage(final com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation logoImage) {
        setLogoImage(logoImage);
        return this;
    }


}
