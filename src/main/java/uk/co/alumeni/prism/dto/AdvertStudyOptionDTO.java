package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

public class AdvertStudyOptionDTO {

    private Integer advertId;

    private PrismStudyOption studyOption;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

}
