package com.zuehlke.pgadmissions.rest.dto;

public class AdvertImageDTO {

    private FileDTO logoImage;

    private FileDTO backgroundImage;

    public FileDTO getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileDTO logoImage) {
        this.logoImage = logoImage;
    }

    public FileDTO getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(FileDTO backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
}
