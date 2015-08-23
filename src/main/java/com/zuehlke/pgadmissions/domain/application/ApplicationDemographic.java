package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Embeddable
public class ApplicationDemographic {

    @ManyToOne
    @JoinColumn(name = "imported_ethnicity_id")
    private ImportedEntitySimple ethnicity;

    @ManyToOne
    @JoinColumn(name = "imported_disability_id")
    private ImportedEntitySimple disability;

    public ImportedEntitySimple getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntitySimple ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntitySimple getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntitySimple disability) {
        this.disability = disability;
    }
    
    public ApplicationDemographic withEthnicity(ImportedEntitySimple ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }
    
    public ApplicationDemographic withDisability(ImportedEntitySimple disability) {
        this.disability = disability;
        return this;
    }
    
}
