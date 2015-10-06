package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

public class ResourceRepresentationConnection extends ResourceRepresentationIdentity {

    private String institutionName;

    private String departmentName;

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public ResourceRepresentationConnection withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationConnection withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationConnection withInstitutionName(String institutionName) {
        this.institutionName = institutionName;
        return this;
    }
    
    public ResourceRepresentationConnection withDepartmentName(String departmentName) {
        this.departmentName = departmentName;
        return this;
    }
    
    public ResourceRepresentationConnection withLogoImage(DocumentRepresentation logoImage) {
        setLogoImage(logoImage);
        return this;
    }
    
}
