package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedSubjectAreaRepresentation extends ImportedEntitySimpleRepresentation {

    private String code;

    private Integer parentSubjectArea;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getParentSubjectArea() {
        return parentSubjectArea;
    }

    public void setParentSubjectArea(Integer parentSubjectArea) {
        this.parentSubjectArea = parentSubjectArea;
    }

}
