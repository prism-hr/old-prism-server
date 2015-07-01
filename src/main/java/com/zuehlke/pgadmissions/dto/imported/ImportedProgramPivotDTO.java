package com.zuehlke.pgadmissions.dto.imported;

import com.google.common.base.Objects;

public class ImportedProgramPivotDTO extends ImportedEntityPivotDTO {

    private Integer institution;

    private String qualification;

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getInstitution() {
        return institution;
    }

    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institution, getName());
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedProgramPivotDTO other = (ImportedProgramPivotDTO) object;
        return Objects.equal(institution, other.getInstitution());
    }

}
