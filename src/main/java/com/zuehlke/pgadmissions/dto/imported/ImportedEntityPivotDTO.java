package com.zuehlke.pgadmissions.dto.imported;

import com.google.common.base.Objects;

public class ImportedEntityPivotDTO {
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ImportedEntityPivotDTO other = (ImportedEntityPivotDTO) object;
        return Objects.equal(getName(), other.getName());
    }

}
