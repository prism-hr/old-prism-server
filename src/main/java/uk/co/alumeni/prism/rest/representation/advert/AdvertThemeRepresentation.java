package uk.co.alumeni.prism.rest.representation.advert;

public class AdvertThemeRepresentation {

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

    public AdvertThemeRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertThemeRepresentation withName(String name) {
        this.name = name;
        return this;
    }

}
