package com.zuehlke.pgadmissions.rest.representation.resource;

public class InstitutionAdvertRepresentation {

    private Integer id;

    private String title;

    private Integer logoImage;

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

    public Integer getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
    }

}
