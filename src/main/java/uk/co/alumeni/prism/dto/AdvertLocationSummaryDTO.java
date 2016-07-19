package uk.co.alumeni.prism.dto;

public class AdvertLocationSummaryDTO extends AdvertThemeSummaryDTO {

    private Integer parentId;
    
    private String nameIndex;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(String nameIndex) {
        this.nameIndex = nameIndex;
    }

}
