package com.zuehlke.pgadmissions.dto;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class AdvertSearchEngineDTO {

    private String projectTitle;

    private String projectSummary;

    private String projectDescription;

    private String programTitle;

    private String programSummary;

    private String programDescription;

    private String institutionTitle;

    private String institutionSummary;

    private String institutionHomepage;
    
    private Integer parentProgram;
    
    private Integer parentInstitution;

    private List<ResourceSearchEngineDTO> relatedProjects = Lists.newLinkedList();

    private List<ResourceSearchEngineDTO> relatedPrograms = Lists.newLinkedList();

    private List<ResourceSearchEngineDTO> relatedInstitutions = Lists.newLinkedList();

    private List<String> relatedUsers = Lists.newLinkedList();

    public final String getProjectTitle() {
        return projectTitle;
    }

    public final void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public final String getProjectSummary() {
        return projectSummary;
    }

    public final void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public final String getProjectDescription() {
        return projectDescription;
    }

    public final void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public final String getProgramTitle() {
        return programTitle;
    }

    public final void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public final String getProgramSummary() {
        return programSummary;
    }

    public final void setProgramSummary(String programSummary) {
        this.programSummary = programSummary;
    }

    public final String getProgramDescription() {
        return programDescription;
    }

    public final void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public final String getInstitutionTitle() {
        return institutionTitle;
    }

    public final void setInstitutionTitle(String institutionTitle) {
        this.institutionTitle = institutionTitle;
    }

    public final String getInstitutionSummary() {
        return institutionSummary;
    }

    public final void setInstitutionSummary(String institutionSummary) {
        this.institutionSummary = institutionSummary;
    }

    public final String getInstitutionHomepage() {
        return institutionHomepage;
    }

    public final void setInstitutionHomepage(String institutionHomepage) {
        this.institutionHomepage = institutionHomepage;
    }

    public final Integer getParentProgram() {
        return parentProgram;
    }

    public final void setParentProgram(Integer parentProgram) {
        this.parentProgram = parentProgram;
    }

    public final Integer getParentInstitution() {
        return parentInstitution;
    }

    public final void setParentInstitution(Integer parentInstitution) {
        this.parentInstitution = parentInstitution;
    }

    public final List<ResourceSearchEngineDTO> getRelatedProjects() {
        return relatedProjects;
    }

    public final void setRelatedProjects(List<ResourceSearchEngineDTO> relatedProjects) {
        this.relatedProjects.addAll(relatedProjects);
    }

    public final List<ResourceSearchEngineDTO> getRelatedPrograms() {
        return relatedPrograms;
    }

    public final void setRelatedPrograms(List<ResourceSearchEngineDTO> relatedPrograms) {
        this.relatedPrograms.addAll(relatedPrograms);
    }

    public final List<ResourceSearchEngineDTO> getRelatedInstitutions() {
        return relatedInstitutions;
    }

    public final void setRelatedInstitutions(List<ResourceSearchEngineDTO> relatedInstitutions) {
        this.relatedInstitutions.addAll(relatedInstitutions);
    }

    public final List<String> getRelatedUsers() {
        return relatedUsers;
    }

    public final void setRelatedUsers(List<String> relatedUsers) {
        this.relatedUsers.addAll(relatedUsers);
    }

    public String getTitle() {
        return Joiner.on(" ").skipNulls().join(projectTitle, programTitle, institutionTitle);
    }

    public String getDescription() throws UnsupportedEncodingException {
        String projectDescriptionCleaned = Joiner.on("").skipNulls().join(wrapString(projectSummary), wrapString(projectDescription));
        String programDescriptionCleaned = Joiner.on("").skipNulls().join(wrapString(programSummary), wrapString(programDescription));
        String institutionDescriptionCleaned = Joiner.on("").skipNulls()
                .join(wrapString(institutionSummary), wrapString(buildHyperLink(institutionHomepage, "Institution Homepage")));
        return Joiner.on("").skipNulls().join(projectDescriptionCleaned, programDescriptionCleaned, institutionDescriptionCleaned);
    }

    public AdvertSearchEngineDTO withRelatedInstitutions(List<ResourceSearchEngineDTO> relatedInstitutions) {
        setRelatedInstitutions(relatedInstitutions);
        return this;
    }

    private String wrapString(String input) {
        return input == null ? null : ("<p>" + input + "</p>").replace("<p><p>", "<p>").replace("</p></p>", "</p>");
    }

    private String buildHyperLink(String url, String title) throws UnsupportedEncodingException {
        return institutionHomepage == null ? null : "<a href=\"" + URLEncoder.encode(url, "UTF-8") + "\">" + title + "</a>";
    }

}
