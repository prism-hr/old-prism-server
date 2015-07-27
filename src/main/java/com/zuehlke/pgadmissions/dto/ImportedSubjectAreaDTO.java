package com.zuehlke.pgadmissions.dto;

public class ImportedSubjectAreaDTO {

    private Integer id;

    private String jacsCode;

    private String jacsCodeOld;

    private Integer ucasSubject;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ImportedSubjectAreaDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public ImportedSubjectAreaDTO withJacsCode(String jacsCode) {
        this.jacsCode = jacsCode;
        return this;
    }

}
