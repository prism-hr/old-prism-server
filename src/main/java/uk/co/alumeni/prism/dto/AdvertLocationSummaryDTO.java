package uk.co.alumeni.prism.dto;

public class AdvertLocationSummaryDTO extends AdvertThemeSummaryDTO {

    private Integer parentId;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
