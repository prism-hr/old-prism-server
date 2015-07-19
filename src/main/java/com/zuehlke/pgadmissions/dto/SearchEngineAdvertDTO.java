package com.zuehlke.pgadmissions.dto;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

// TODO add departments
public class SearchEngineAdvertDTO {

    private Integer projectId;

    private String projectName;

    private String projectSummary;

    private String projectDescription;

    private Integer programId;

    private String programName;

    private String programSummary;

    private String programDescription;

    private Integer institutionId;

    private String institutionName;

    private String institutionSummary;

    private String institutionHomepage;

    private List<ResourceSearchEngineDTO> relatedProjects = Lists.newLinkedList();

    private List<ResourceSearchEngineDTO> relatedPrograms = Lists.newLinkedList();

    private List<ResourceSearchEngineDTO> relatedInstitutions = Lists.newLinkedList();

    private List<String> relatedUsers = Lists.newLinkedList();

    public final Integer getProjectId() {
        return projectId;
    }

    public final void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public final String getProjectName() {
        return projectName;
    }

    public final void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public final Integer getProgramId() {
        return programId;
    }

    public final void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public final String getProgramName() {
        return programName;
    }

    public final void setProgramName(String programName) {
        this.programName = programName;
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

    public final Integer getInstitutionId() {
        return institutionId;
    }

    public final void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public final String getInstitutionName() {
        return institutionName;
    }

    public final void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
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
        return Joiner.on(" ").skipNulls().join(projectName, programName, institutionName);
    }

    public String getDescription() throws UnsupportedEncodingException {
        String projectDescriptionCleaned = Joiner.on("").skipNulls()
                .join(wrapString(StringEscapeUtils.escapeHtml(projectSummary)), wrapString(projectDescription));
        String programDescriptionCleaned = Joiner.on("").skipNulls()
                .join(wrapString(StringEscapeUtils.escapeHtml(programSummary)), wrapString(programDescription));
        String institutionDescriptionCleaned = Joiner.on("").skipNulls()
                .join(wrapString(StringEscapeUtils.escapeHtml(institutionSummary)), wrapString(buildHyperLink(institutionHomepage, "External Homepage")));
        return Joiner.on("").skipNulls().join(projectDescriptionCleaned, programDescriptionCleaned, institutionDescriptionCleaned);
    }

    public SearchEngineAdvertDTO withRelatedInstitutions(List<ResourceSearchEngineDTO> relatedInstitutions) {
        setRelatedInstitutions(relatedInstitutions);
        return this;
    }

    private String wrapString(String input) {
        return input == null ? null : ("<p>" + input + "</p>").replace("<p><p>", "<p>").replace("</p></p>", "</p>");
    }

    private String buildHyperLink(String url, String title) throws UnsupportedEncodingException {
        return institutionHomepage == null ? null : "<a href=\"" + url + "\">" + title + "</a>";
    }

}
