package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;

public class ResourceRepresentationRobot {

    private String homepageLabel;

    private String applicationUrl;

    private ResourceRepresentationRobotMetadata project;

    private ResourceRepresentationRobotMetadata program;

    private ResourceRepresentationRobotMetadata department;

    private ResourceRepresentationRobotMetadata institution;

    private ResourceRepresentationRobotMetadata system;

    private ResourceRepresentationRobotMetadataRelated relatedProjects;

    private ResourceRepresentationRobotMetadataRelated relatedPrograms;

    private ResourceRepresentationRobotMetadataRelated relatedDepartments;

    private ResourceRepresentationRobotMetadataRelated relatedInstitutions;

    private ResourceRepresentationMetadataUserRelated relatedUsers;

    public ResourceRepresentationRobot(String homepageLabel, String applicationUrl) {
        this.homepageLabel = homepageLabel;
        this.applicationUrl = applicationUrl;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getHomepageLabel() {
        return homepageLabel;
    }

    public void setHomepageLabel(String homepageLabel) {
        this.homepageLabel = homepageLabel;
    }

    public ResourceRepresentationRobotMetadata getProject() {
        return project;
    }

    public void setProject(ResourceRepresentationRobotMetadata project) {
        this.project = project;
    }

    public ResourceRepresentationRobotMetadata getProgram() {
        return program;
    }

    public void setProgram(ResourceRepresentationRobotMetadata program) {
        this.program = program;
    }

    public ResourceRepresentationRobotMetadata getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationRobotMetadata department) {
        this.department = department;
    }

    public ResourceRepresentationRobotMetadata getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationRobotMetadata institution) {
        this.institution = institution;
    }

    public ResourceRepresentationRobotMetadata getSystem() {
        return system;
    }

    public void setSystem(ResourceRepresentationRobotMetadata system) {
        this.system = system;
    }

    public ResourceRepresentationRobotMetadataRelated getRelatedProjects() {
        return relatedProjects;
    }

    public void setRelatedProjects(ResourceRepresentationRobotMetadataRelated relatedProjects) {
        this.relatedProjects = relatedProjects;
    }

    public ResourceRepresentationRobotMetadataRelated getRelatedPrograms() {
        return relatedPrograms;
    }

    public void setRelatedPrograms(ResourceRepresentationRobotMetadataRelated relatedPrograms) {
        this.relatedPrograms = relatedPrograms;
    }

    public ResourceRepresentationRobotMetadataRelated getRelatedDepartments() {
        return relatedDepartments;
    }

    public void setRelatedDepartments(ResourceRepresentationRobotMetadataRelated relatedDepartments) {
        this.relatedDepartments = relatedDepartments;
    }

    public ResourceRepresentationRobotMetadataRelated getRelatedInstitutions() {
        return relatedInstitutions;
    }

    public void setRelatedInstitutions(ResourceRepresentationRobotMetadataRelated relatedInstitutions) {
        this.relatedInstitutions = relatedInstitutions;
    }

    public ResourceRepresentationMetadataUserRelated getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(ResourceRepresentationMetadataUserRelated relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public ResourceRepresentationRobot withSystem(ResourceRepresentationRobotMetadata system) {
        this.system = system;
        return this;
    }

    public ResourceRepresentationRobotMetadata getResource() {
        return ObjectUtils.firstNonNull(project, program, department, institution, system);
    }

    public String getName() {
        return getResource().getName();
    }

    public String getDescription() {
        return getResource().getDescriptionDisplay(homepageLabel);
    }

    public List<ResourceRepresentationRobotMetadata> getParentResources() {
        boolean isResource = true;
        List<ResourceRepresentationRobotMetadata> parentResources = Lists.newLinkedList();
        for (ResourceRepresentationRobotMetadata parentResource : new ResourceRepresentationRobotMetadata[] { project, program, department, institution }) {
            if (!isResource) {
                parentResources.add(parentResource);
            }
            isResource = (isResource && parentResource != null) ? false : isResource;
        }
        return parentResources;
    }

    public ResourceRepresentationRobot withRelatedInstitutions(ResourceRepresentationRobotMetadataRelated relatedInstitutions) {
        this.relatedInstitutions = relatedInstitutions;
        return this;
    }

}
