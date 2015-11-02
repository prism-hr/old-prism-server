package com.zuehlke.pgadmissions.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import java.math.BigDecimal;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismJoinState;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class AdvertRepresentationExtended extends AdvertRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceOpportunityRepresentationSimple program;

    private ResourceOpportunityRepresentationSimple project;

    private PrismOpportunityType opportunityType;

    private List<PrismOpportunityCategory> opportunityCategories;

    private List<PrismOpportunityType> targetOpportunityTypes;

    private String name;

    private List<PrismStudyOption> studyOptions;

    private Integer applicationCount;

    private Integer applicationRatingCount;

    private BigDecimal applicationRatingAverage;

    private PrismJoinState joinStateStaff;

    private PrismJoinState joinStateStudent;

    private PrismJoinState connectState;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimple getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimple department) {
        this.department = department;
    }

    public ResourceOpportunityRepresentationSimple getProgram() {
        return program;
    }

    public void setProgram(ResourceOpportunityRepresentationSimple program) {
        this.program = program;
    }

    public ResourceOpportunityRepresentationSimple getProject() {
        return project;
    }

    public void setProject(ResourceOpportunityRepresentationSimple project) {
        this.project = project;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public List<PrismOpportunityCategory> getOpportunityCategories() {
        return opportunityCategories;
    }

    public List<PrismOpportunityType> getTargetOpportunityTypes() {
        return targetOpportunityTypes;
    }

    public void setTargetOpportunityTypes(List<PrismOpportunityType> targetOpportunityTypes) {
        this.targetOpportunityTypes = targetOpportunityTypes;
    }

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public PrismJoinState getJoinStateStaff() {
        return joinStateStaff;
    }

    public void setJoinStateStaff(PrismJoinState joinStateStaff) {
        this.joinStateStaff = joinStateStaff;
    }

    public PrismJoinState getJoinStateStudent() {
        return joinStateStudent;
    }

    public void setJoinStateStudent(PrismJoinState joinStateStudent) {
        this.joinStateStudent = joinStateStudent;
    }

    public PrismJoinState getConnectState() {
        return connectState;
    }

    public void setConnectState(PrismJoinState connectState) {
        this.connectState = connectState;
    }

    public ResourceRepresentationSimple getResource() {
        return firstNonNull(project, program, department, institution);
    }

}
