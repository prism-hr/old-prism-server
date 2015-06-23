package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private ImportedEntitySimpleRepresentation referralSourceMapping;

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getReferralSource() {
        return referralSourceMapping.getId();
    }

    public void setReferralSource(Integer referralSource) {
        this.referralSourceMapping = new ImportedEntitySimpleRepresentation().withId(referralSource);
    }

    public ImportedEntitySimpleRepresentation getReferralSourceMapping() {
        return referralSourceMapping;
    }

    public void setReferralSource(ImportedEntitySimpleRepresentation referralSourceMapping) {
        this.referralSourceMapping = referralSourceMapping;
    }

}
