package com.zuehlke.pgadmissions.dto;

import java.util.List;
import java.util.Map;

import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;

public class InstitutionDomicileImportDTO {

    private InstitutionDomicile domicile;
    
    private List<SubdivisionType> subdivisions;
    
    private Map<Short, String> categories;

    public final InstitutionDomicile getDomicile() {
        return domicile;
    }

    public final List<SubdivisionType> getSubdivisions() {
        return subdivisions;
    }

    public final Map<Short, String> getCategories() {
        return categories;
    }
    
    public InstitutionDomicileImportDTO withDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }
    
    public InstitutionDomicileImportDTO withSubdivisions(List<SubdivisionType> subdivisions) {
        this.subdivisions = subdivisions;
        return this;
    }
    
    public InstitutionDomicileImportDTO withCategories(Map<Short, String> categories) {
        this.categories = categories;
        return this;
    }
    
}
