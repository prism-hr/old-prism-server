package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismReportColumnAccessorType.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.*;

public enum PrismReportColumn {

    ID(SYSTEM_ID, "application.id", null, null, "id", STRING), //
    USER_NAME(SYSTEM_NAME, "user.fullName", null, null, "name", STRING), //
    USER_EMAIL(SYSTEM_EMAIL, "user.email", null, null, "email", STRING), //
    USER_NATIONALITY(APPLICATION_PERSONAL_DETAIL_NATIONALITY_LABEL, "nationality.name", null, null, "nationality", STRING), //
    USER_DOMICILE(APPLICATION_PERSONAL_DETAIL_DOMICILE_LABEL, "domicile.name", null, null, "countryOfBirth", STRING), //
    USER_DATE_OF_BIRTH(APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL, "personalDetail.dateOfBirth", null, null, "dateOfBirth", DATE), //
    USER_GENDER(APPLICATION_PERSONAL_DETAIL_GENDER_LABEL, "gender.name", null, null, "gender", STRING), //
    INSTITUTION_TITLE(SYSTEM_INSTITUTION, "institution.title", null, null, "institution", STRING), //
    DEPARTMENT_TITLE(SYSTEM_DEPARTMENT, "department.title", null, null, "department", STRING), //
    PROGRAM_TITLE(SYSTEM_PROGRAM, "program.title", null, null, "program", STRING), //
    PROJECT_TITLE(SYSTEM_PROJECT, "project.title", null, null, "project", STRING), //
    STUDY_OPTION(APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL, "studyOption.code", null, null, "studyOption", DISPLAY_PROPERTY), //
    REFERRAL_SOURCE(APPLICATION_REFERRAL_SOURCE, "referralSource.name", null, null, "referralSource", STRING), //
    PRIMARY_THEME(APPLICATION_PROGRAM_DETAIL_PRIMARY_THEME_LABEL, "application.primaryTheme", Arrays.asList(APPLICATION_THEME_PRIMARY), null, "primaryTheme", STRING), //
    SECONDARY_THEME(APPLICATION_PROGRAM_DETAIL_SECONDARY_THEME_LABEL, "application.secondaryTheme", Arrays.asList(APPLICATION_THEME_SECONDARY), null, "secondaryTheme", STRING), //
    STUDY_INSTITUTION(APPLICATION_STUDY_DETAIL_LOCATION_LABEL, "application.studyDetail.studyLocation", Arrays.asList(APPLICATION_STUDY_DETAIL), null, "studyLocation",
            STRING), //
    STUDY_DIVISION(APPLICATION_STUDY_DETAIL_DIVISION_LABEL, "application.studyDetail.studyDivision", Arrays.asList(APPLICATION_STUDY_DETAIL), null, "studyDivision", STRING), //
    STUDY_AREA(APPLICATION_STUDY_DETAIL_AREA_LABEL, "application.studyDetail.studyArea", Arrays.asList(APPLICATION_STUDY_DETAIL), null, "studyArea", STRING), //
    STUDY_APPLICATION(APPLICATION_STUDY_DETAIL_APPLICATION_ID_LABEL, "application.studyDetail.studyApplicationId", Arrays.asList(APPLICATION_STUDY_DETAIL), null,
            "studyApplicationId", STRING), //
    STUDY_START_DATE(APPLICATION_STUDY_DETAIL_START_DATE_LABEL, "application.studyDetail.studyStartDate", Arrays.asList(APPLICATION_STUDY_DETAIL), null, "studyStartDate",
            DATE), //
    CREATED_DATE(SYSTEM_CREATED_DATE, "application.createdTimestamp", null, null, "createdDate", DATE), //
    CLOSING_DATE(SYSTEM_CLOSING_DATE, "application.closingDate", null, null, "closingDate", DATE), //
    SUBMITTED_DATE(APPLICATION_SUBMISSION_DATE, "application.submittedTimestamp", null, null, "submittedDate", DATE), //
    UPDATED_DATE(SYSTEM_UPDATED_DATE, "application.updatedTimestamp", null, null, "updatedDate", DATE), //
    RATING_COUNT(SYSTEM_TOTAL_RATING, "application.applicationRatingCount", null, Arrays.asList(ALL_ASSESSMENT_CONTENT, ALL_CONTENT), "ratingCount", STRING), //
    RATING_AVERAGE(SYSTEM_AVERAGE_RATING, "application.applicationRatingAverage", null, Arrays.asList(ALL_ASSESSMENT_CONTENT, ALL_CONTENT), "ratingAverage",
            STRING), //
    STATE(SYSTEM_STATE, "state.stateGroup.id", null, null, "state", DISPLAY_PROPERTY), //
    REFEREE_COUNT(APPLICATION_REFEREES, "count(distinct referee.id)", null, null, "referees", STRING), //
    PROVIDED_REFERENCE_COUNT(APPLICATION_PROVIDED_REFERENCES, "count(distinct provideReferenceComment.id)", null, null, "providedReferences", STRING), //
    DECLINED_REFERENCE_COUNT(APPLICATION_DECLINED_REFERENCES, "count(distinct declineReferenceComment.id)", null, null, "declinedReferences", STRING), //
    VERIFICATION_INSTANCE_COUNT(APPLICATION_VERIFICATION_INSTANCE_COUNT, "verificationProcessing.instanceCount", null, null, "verificationInstanceCount",
            STRING), //
    VERIFICATION_DURATION_AVERAGE(APPLICATION_VERIFICATION_INSTANCE_DURATION_AVERAGE, "verificationProcessing.dayDurationAverage", null, null,
            "verificationInstanceDurationAverage", STRING), //
    REFERENCE_INSTANCE_COUNT(APPLICATION_REFERENCE_INSTANCE_COUNT, "referenceProcessing.instanceCount", null, null, "referenceInstanceCount", STRING), //
    REFERENCE_DURATION_AVERAGE(APPLICATION_REFERENCE_INSTANCE_DURATION_AVERAGE, "referenceProcessing.dayDurationAverage", null, null,
            "referenceInstanceDurationAverage", STRING), //
    REVIEW_INSTANCE_COUNT(APPLICATION_REVIEW_INSTANCE_COUNT, "reviewProcessing.instanceCount", null, null, "reviewInstanceCount", STRING), //
    REVIEW_DURATION_AVERAGE(APPLICATION_REVIEW_INSTANCE_DURATION_AVERAGE, "reviewProcessing.dayDurationAverage", null, null, "reviewInstanceDurationAverage",
            STRING), //
    INTERVIEW_INSTANCE_COUNT(APPLICATION_INTERVIEW_INSTANCE_COUNT, "interviewProcessing.instanceCount", null, null, "interviewInstanceCount", STRING), //
    INTERVIEW_DURATION_AVERAGE(APPLICATION_INTERVIEW_INSTANCE_DURATION_AVERAGE, "interviewProcessing.dayDurationAverage", null, null,
            "interviewInstanceDurationAverage", STRING), //
    APPROVAL_INSTANCE_COUNT(APPLICATION_APPROVAL_INSTANCE_COUNT, "approvalProcessing.instanceCount", null, null, "approvalInstanceCount", STRING), //
    APPROVAL_DURATION_AVERAGE(APPLICATION_APPROVAL_INSTANCE_DURATION_AVERAGE, "approvalProcessing.dayDurationAverage", null, null,
            "approvalInstanceDurationAverage", STRING), //
    START_DATE(APPLICATION_CONFIRMED_START_DATE, "application.confirmedStartDate", null, null, "confirmedStartDate", DATE), //
    OFFER_TYPE(APPLICATION_CONFIRMED_OFFER_TYPE, "application.confirmedOfferType", null, null, "confirmedOfferType", DISPLAY_PROPERTY);

    private PrismDisplayPropertyDefinition title;

    private String column;

    private List<PrismWorkflowPropertyDefinition> definitions;

    private List<PrismActionRedactionType> redactions;

    private String accessor;

    private PrismReportColumnAccessorType accessorType;

    private PrismReportColumn(PrismDisplayPropertyDefinition title, String column, List<PrismWorkflowPropertyDefinition> definitions,
            List<PrismActionRedactionType> redactions, String accessor, PrismReportColumnAccessorType accessorType) {
        this.title = title;
        this.column = column;
        this.definitions = definitions;
        this.redactions = redactions;
        this.accessor = accessor;
        this.accessorType = accessorType;
    }

    public final PrismDisplayPropertyDefinition getTitle() {
        return title;
    }

    public final String getColumn() {
        return column;
    }

    public final List<PrismWorkflowPropertyDefinition> getDefinitions() {
        return definitions == null ? Collections.<PrismWorkflowPropertyDefinition> emptyList() : definitions;
    }

    public final List<PrismActionRedactionType> getRedactions() {
        return redactions == null ? Collections.<PrismActionRedactionType> emptyList() : redactions;
    }

    public final String getAccessor() {
        return accessor;
    }

    public final PrismReportColumnAccessorType getAccessorType() {
        return accessorType;
    }

    public final String getColumnAccessor() {
        return column + " as " + accessor;
    }

}
