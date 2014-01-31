package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;

public class OpportunityRequestBuilder {

    private Integer id;
    private Domicile institutionCountry;
    private String institutionCode;
    private String otherInstitution;
    private String programTitle;
    private String programDescription;
    private RegisteredUser author;
    private OpportunityRequestStatus status = OpportunityRequestStatus.NEW;
    private Date createdDate;
    private Integer studyDuration;
    private Boolean atasRequired;
    private String studyOptions;
    private Date applicationStartDate;
    private Integer advertisingDuration;
    private Integer studyDurationNumber;
    private String studyDurationUnit;

    public OpportunityRequestBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public OpportunityRequestBuilder institutionCountry(Domicile institutionCountry) {
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

    public OpportunityRequestBuilder author(RegisteredUser author) {
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

    public OpportunityRequestBuilder applicationStartDate(Date applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public OpportunityRequestBuilder advertisingDuration(Integer advertisingDuration) {
        this.advertisingDuration = advertisingDuration;
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
        request.setApplicationStartDate(applicationStartDate);
        request.setAdvertisingDuration(advertisingDuration);
        request.setStudyDurationNumber(studyDurationNumber);
        request.setStudyDurationUnit(studyDurationUnit);
        return request;
    }

    public static OpportunityRequestBuilder aOpportunityRequest(RegisteredUser author, Domicile institutionCountry) {
        Date yesterday = new DateTime().minusDays(1).toDate();
        Date inAWeek = new DateTime().plusWeeks(1).toDate();

        return new OpportunityRequestBuilder().author(author).createdDate(yesterday).institutionCode("AGH").institutionCountry(institutionCountry)
                .programDescription("This is really amazing Opportunity!").programTitle("Amazing Opportunity").advertisingDuration(2)
                .applicationStartDate(inAWeek).atasRequired(true).studyDuration(24).studyDurationNumber(2).studyDurationUnit("YEARS").studyOptions("B+++++,F+++++");
    }

}
