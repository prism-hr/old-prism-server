package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

public class DomicileUseDTO {

    private ImportedEntitySimple domicile;

    private Long useCount;

    public ImportedEntitySimple getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
    }

    public final Long getUseCount() {
        return useCount;
    }

    public final void setUseCount(Long useCount) {
        this.useCount = useCount;
    }

}
