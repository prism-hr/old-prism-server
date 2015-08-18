package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.institution.ResourceRepresentationTargeting;

public class AdvertTargetsRepresentation {

    private List<AdvertCompetenceRepresentation> competences;

    private List<AdvertSubjectAreaRepresentation> subjectAreas;

    private List<ResourceRepresentationTargeting> resources;

    private List<ResourceRepresentationTargeting> selectedResources;

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public List<AdvertSubjectAreaRepresentation> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(List<AdvertSubjectAreaRepresentation> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public List<ResourceRepresentationTargeting> getResources() {
        return resources;
    }

    public void setResources(List<ResourceRepresentationTargeting> resources) {
        this.resources = resources;
    }

    public List<ResourceRepresentationTargeting> getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(List<ResourceRepresentationTargeting> selectedResources) {
        this.selectedResources = selectedResources;
    }

    public AdvertTargetsRepresentation withCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
        return this;
    }

    public AdvertTargetsRepresentation withSubjectAreas(List<AdvertSubjectAreaRepresentation> subjectAreas) {
        this.subjectAreas = subjectAreas;
        return this;
    }
    
    public AdvertTargetsRepresentation withResources(List<ResourceRepresentationTargeting> resources) {
        this.resources = resources;
        return this;
    }

    public AdvertTargetsRepresentation withSelectedResources(List<ResourceRepresentationTargeting> selectedResources) {
        this.selectedResources = selectedResources;
        return this;
    }
    
}
