package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class ResourceParentDTO extends ResourceDTO {

    @NotNull
    private AdvertDTO advert;

    private LocalDate endDate;

    private ResourceParentAttributesDTO attributes;

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ResourceParentAttributesDTO getAttributes() {
        return attributes;
    }

    public void setAttributes(ResourceParentAttributesDTO attributes) {
        this.attributes = attributes;
    }

    public static class ResourceParentAttributesDTO {

        private List<PrismActionCondition> conditions;

        private List<PrismStudyOption> studyOptions;

        private List<String> studyLocations;

        public List<PrismActionCondition> getConditions() {
            return conditions;
        }

        public void setConditions(List<PrismActionCondition> conditions) {
            this.conditions = conditions;
        }

        public List<PrismStudyOption> getStudyOptions() {
            return studyOptions;
        }

        public void setStudyOptions(List<PrismStudyOption> studyOptions) {
            this.studyOptions = studyOptions;
        }

        public List<String> getStudyLocations() {
            return studyLocations;
        }

        public void setStudyLocations(List<String> studyLocations) {
            this.studyLocations = studyLocations;
        }

    }

}
