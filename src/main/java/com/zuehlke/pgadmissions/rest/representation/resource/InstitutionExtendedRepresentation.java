package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class InstitutionExtendedRepresentation extends AbstractResourceRepresentation {

    private String title;

    private String currency;

    private FileRepresentation logoImage;

    private AdvertRepresentation advert;

    private ResourceSummaryRepresentation resourceSummary;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public FileRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileRepresentation logoImage) {
        this.logoImage = logoImage;
    }

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

    public ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }
}
