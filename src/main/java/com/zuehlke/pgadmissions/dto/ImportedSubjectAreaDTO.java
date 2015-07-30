package com.zuehlke.pgadmissions.dto;

public class ImportedSubjectAreaDTO {

    private Integer id;

    private String name;

    private String jacsCode;

    private String jacsCodeOld;

    private Integer ucasSubject;

    private Integer parent;

    private String[] jacsCodes;

    private String[] jacsCodesOld;

    private Integer specificity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJacsCode() {
        return jacsCode;
    }

    public void setJacsCode(String jacsCode) {
        this.jacsCode = jacsCode;
    }

    public String getJacsCodeOld() {
        return jacsCodeOld;
    }

    public void setJacsCodeOld(String jacsCodeOld) {
        this.jacsCodeOld = jacsCodeOld;
    }

    public Integer getUcasSubject() {
        return ucasSubject;
    }

    public void setUcasSubject(Integer ucasSubject) {
        this.ucasSubject = ucasSubject;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String[] getJacsCodes() {
        return jacsCodes;
    }

    public String[] getJacsCodesOld() {
        return jacsCodesOld;
    }

    public Integer getSpecificity() {
        return specificity;
    }

    public ImportedSubjectAreaDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public ImportedSubjectAreaDTO withJacsCode(String jacsCode) {
        this.jacsCode = jacsCode;
        return this;
    }

}
