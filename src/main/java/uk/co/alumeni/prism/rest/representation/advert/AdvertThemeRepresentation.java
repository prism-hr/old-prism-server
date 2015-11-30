package uk.co.alumeni.prism.rest.representation.advert;

public class AdvertThemeRepresentation {

    private Integer themeId;

    private String name;

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdvertThemeRepresentation withThemeId(Integer themeId) {
        this.themeId = themeId;
        return this;
    }

    public AdvertThemeRepresentation withName(String name) {
        this.name = name;
        return this;
    }

}
