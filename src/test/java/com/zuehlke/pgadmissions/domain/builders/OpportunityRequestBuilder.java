package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;

public class OpportunityRequestBuilder {

    private Integer id;
    private InstitutionDomicile institutionCountry;
    private String institutionCode;
    private String otherInstitution;
    private String programTitle;
    private String programDescription;
    private User author;
    private OpportunityRequestStatus status = OpportunityRequestStatus.NEW;
    private Date createdDate;
    private Integer studyDuration;
    private Boolean atasRequired;
    private String studyOptions;
    private ProgramType programType;
    private Integer advertisingDeadlineYear;
    private OpportunityRequestType type = OpportunityRequestType.CREATE;
    private Program sourceProgram;
    private List<OpportunityRequestComment> comments = Lists.newArrayList();
    private String funding;
    private Boolean acceptingApplications = true;
    private Integer studyDurationNumber;
    private String studyDurationUnit;

    public OpportunityRequestBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public OpportunityRequestBuilder institutionCountry(InstitutionDomicile institutionCountry) {
        this.institutionCountry = institutionCountry;
        return this;
    }

    public OpportunityRequestBuilder institutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
        return this;
    }

    public OpportunityRequestBuilder otherInstitution(String otherInstitution) {
        this.otherInstitution = otherInstitution;
        return this;
    }

    public OpportunityRequestBuilder programTitle(String programTitle) {
        this.programTitle = programTitle;
        return this;
    }

    public OpportunityRequestBuilder programDescription(String programDescription) {
        this.programDescription = programDescription;
        return this;
    }

    public OpportunityRequestBuilder author(User author) {
        this.author = author;
        return this;
    }

    public OpportunityRequestBuilder status(OpportunityRequestStatus status) {
        this.status = status;
        return this;
    }

    public OpportunityRequestBuilder createdDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public OpportunityRequestBuilder studyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
        return this;
    }

    public OpportunityRequestBuilder atasRequired(Boolean atasRequired) {
        this.atasRequired = atasRequired;
        return this;
    }

    public OpportunityRequestBuilder studyOptions(String studyOptions) {
        this.studyOptions = studyOptions;
        return this;
    }

    public OpportunityRequestBuilder programType(ProgramType programType) {
        this.programType = programType;
        return this;
    }

    public OpportunityRequestBuilder advertisingDeadlineYear(Integer advertisingDeadlineYear) {
        this.advertisingDeadlineYear = advertisingDeadlineYear;
        return this;
    }

    public OpportunityRequestBuilder type(OpportunityRequestType type) {
        this.type = type;
        return this;
    }

    public OpportunityRequestBuilder sourceProgram(Program sourceProgram) {
        this.sourceProgram = sourceProgram;
        return this;
    }

    public OpportunityRequestBuilder comments(List<OpportunityRequestComment> comments) {
        this.comments.addAll(comments);
        return this;
    }

    public OpportunityRequestBuilder funding(String funding) {
        this.funding = funding;
        return this;
    }

    public OpportunityRequestBuilder acceptingApplications(Boolean acceptingApplications) {
        this.acceptingApplications = acceptingApplications;
        return this;
    }

    public OpportunityRequestBuilder studyDurationNumber(Integer studyDurationNumber) {
        this.studyDurationNumber = studyDurationNumber;
        return this;
    }

    public OpportunityRequestBuilder studyDurationUnit(String studyDurationUnit) {
        this.studyDurationUnit = studyDurationUnit;
        return this;
    }

    public OpportunityRequest build() {
        OpportunityRequest request = new OpportunityRequest();
        request.setId(id);
        request.setInstitutionCountry(institutionCountry);
        request.setInstitutionCode(institutionCode);
        request.setOtherInstitution(otherInstitution);
        request.setProgramTitle(programTitle);
        request.setProgramDescription(programDescription);
        request.setAuthor(author);
        request.setStatus(status);
        request.setCreatedDate(createdDate);
        request.setStudyDuration(studyDuration);
        request.setAtasRequired(atasRequired);
        request.setStudyOptions(studyOptions);
        request.setProgramType(programType);
        request.setAdvertisingDeadlineYear(advertisingDeadlineYear);
        request.setType(type);
        request.setSourceProgram(sourceProgram);
        request.getComments().addAll(comments);
        request.setFunding(funding);
        request.setAcceptingApplications(acceptingApplications);
        request.setStudyDurationNumber(studyDurationNumber);
        request.setStudyDurationUnit(studyDurationUnit);
        return request;
    }

    public static OpportunityRequestBuilder aOpportunityRequest(User author, InstitutionDomicile institutionCountry) {
        DateTime date = new DateTime(2014, 3, 14, 0, 0);

        return new OpportunityRequestBuilder().author(author).createdDate(date.toDate()).institutionCode("AGH").institutionCountry(institutionCountry)
                .programDescription("This is really amazing Opportunity!").programTitle("Amazing Opportunity").advertisingDeadlineYear(2014).atasRequired(true)
                .studyDuration(24).studyDurationNumber(2).studyDurationUnit("YEARS").studyOptions("B+++++,F+++++");
    }

}
