package com.zuehlke.pgadmissions.dto.imported;

import com.google.common.base.Objects;

public class ImportedInstitutionPivotDTO extends ImportedEntityPivotDTO {

    private Integer domicile;

    public Integer getDomicile() {
        return domicile;
    }

    public void setDomicile(Integer domicile) {
        this.domicile = domicile;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(domicile, getName());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedInstitutionPivotDTO other = (ImportedInstitutionPivotDTO) object;
        return Objects.equal(domicile, other.getDomicile());
    }
    
    
}
