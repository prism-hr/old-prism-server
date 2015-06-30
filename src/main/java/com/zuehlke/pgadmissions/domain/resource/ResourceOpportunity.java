package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

public abstract class ResourceOpportunity extends ResourceParent {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

    public abstract void setOpportunityType(ImportedEntitySimple opportunityType);

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    public abstract Set<ResourceStudyOption> getStudyOptions();

    public abstract Set<ResourceStudyLocation> getStudyLocations();

    public void addStudyOption(ResourceStudyOption studyOption) {
        getStudyOptions().add(studyOption);
    }

    public void addStudyLocation(ResourceStudyLocation studyLocation) {
        getStudyLocations().add(studyLocation);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("institution", getInstitution()).addProperty("opportunityType", getOpportunityType());
    }

}
