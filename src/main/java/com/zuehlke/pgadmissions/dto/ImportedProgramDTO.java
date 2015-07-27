package com.zuehlke.pgadmissions.dto;


public class ImportedProgramDTO {

    private Integer id;

    private Integer institution;

    private String qualification;

    private String name;

    private Integer ucasId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInstitution() {
        return institution;
    }

    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUcasId() {
        return ucasId;
    }

    public void setUcasId(Integer ucasId) {
        this.ucasId = ucasId;
    }

    public String index() {
        return ucasId.toString() + qualification + name;
    }

}
