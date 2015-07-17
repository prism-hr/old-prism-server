package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertTargetRepresentation {

    private Integer id;

    private String title;

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

    public AdvertTargetRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertTargetRepresentation withTitle(String title) {
        this.title = title;
        return this;
    }

}
