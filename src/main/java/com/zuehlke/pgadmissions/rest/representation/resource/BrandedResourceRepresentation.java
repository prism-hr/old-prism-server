package com.zuehlke.pgadmissions.rest.representation.resource;

public class BrandedResourceRepresentation extends SimpleResourceRepresentation {

    private Integer logoImageId;

    public BrandedResourceRepresentation() {
        return;
    }

    public BrandedResourceRepresentation(Integer id, String title, Integer logoImageId) {
        super(id, title);
        this.logoImageId = logoImageId;
    }

    public Integer getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(Integer logoImageId) {
        this.logoImageId = logoImageId;
    }

}
