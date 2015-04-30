package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.List;

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

        private List<PrismActionCondition> resourceConditions;

        private List<PrismStudyOption> studyOptions;

        private List<String> studyLocations;

        public List<PrismActionCondition> getResourceConditions() {
            return resourceConditions;
        }

        public void setResourceConditions(List<PrismActionCondition> resourceConditions) {
            this.resourceConditions = resourceConditions;
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
