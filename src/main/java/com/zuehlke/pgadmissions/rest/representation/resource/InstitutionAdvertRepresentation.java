package com.zuehlke.pgadmissions.rest.representation.resource;

public class InstitutionAdvertRepresentation {

    private Integer id;

    private String title;

    private Integer logoImageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(Integer logoImageId) {
        this.logoImageId = logoImageId;
    }

}
