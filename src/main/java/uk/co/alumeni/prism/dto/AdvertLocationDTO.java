package uk.co.alumeni.prism.dto;

public class AdvertLocationDTO {

    private Integer advertId;

    private Integer locationAdvertId;

    private String location;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public Integer getLocationAdvertId() {
        return locationAdvertId;
    }

    public void setLocationAdvertId(Integer locationAdvertId) {
        this.locationAdvertId = locationAdvertId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
