package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertTargetRepresentation {

    private Integer id;

    private String name;

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

    public AdvertTargetRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertTargetRepresentation withName(String title) {
        this.name = title;
        return this;
    }

}
