package uk.co.alumeni.prism.rest.representation.advert;

import uk.co.alumeni.prism.domain.definitions.PrismConnectionState;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationResource;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class AdvertRepresentationExtended extends AdvertRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceOpportunityRepresentationSimple program;

    private ResourceOpportunityRepresentationSimple project;

    private PrismOpportunityType opportunityType;

    private List<PrismOpportunityCategory> opportunityCategories;

    private List<PrismStudyOption> studyOptions;

    private Boolean recommended;

    private String name;

    private Integer applicationCount;

    private Integer applicationRatingCount;

    private BigDecimal applicationRatingAverage;

    private List<AdvertTargetConnectionRepresentation> targets;

    private ActionRepresentationResource action;

    private PrismConnectionState joinStateStaff;

    private PrismConnectionState joinStateStudent;

    private PrismConnectionState connectState;

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

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<AdvertTargetConnectionRepresentation> getTargets() {
        return targets;
    }

    public void setTargets(List<AdvertTargetConnectionRepresentation> targets) {
        this.targets = targets;
    }

    public ActionRepresentationResource getAction() {
        return action;
    }

    public void setAction(ActionRepresentationResource action) {
        this.action = action;
    }

    public PrismConnectionState getJoinStateStaff() {
        return joinStateStaff;
    }

    public void setJoinStateStaff(PrismConnectionState joinStateStaff) {
        this.joinStateStaff = joinStateStaff;
    }

    public PrismConnectionState getJoinStateStudent() {
        return joinStateStudent;
    }

    public void setJoinStateStudent(PrismConnectionState joinStateStudent) {
        this.joinStateStudent = joinStateStudent;
    }

    public PrismConnectionState getConnectState() {
        return connectState;
    }

    public void setConnectState(PrismConnectionState connectState) {
        this.connectState = connectState;
    }

    public ResourceRepresentationSimple getResource() {
        return firstNonNull(project, program, department, institution);
    }

}
