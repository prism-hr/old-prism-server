package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.DISPLAY_PROPERTY;
import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.STRING;

import java.util.Collections;
import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint;

public enum PrismApplicationReportColumn {

    ID(PrismDisplayPropertyDefinition.SYSTEM_ID, "application.id", null, false, "id", STRING), //
    USER_NAME(PrismDisplayPropertyDefinition.SYSTEM_NAME, "user.fullName", null, false, "name", STRING), //
    USER_EMAIL(PrismDisplayPropertyDefinition.SYSTEM_EMAIL, "user.email", null, false, "email", STRING), //
    USER_DATE_OF_BIRTH(PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL, "personalDetail.dateOfBirth", null, false, "dateOfBirth", DATE), //
    USER_GENDER(PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_GENDER_LABEL, "personalDetail.gender.id", null, false, "gender", DISPLAY_PROPERTY), //
    USER_NATIONALITY(PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL, "personalDetail.nationality.id", null, false, "nationality", DISPLAY_PROPERTY), //
    USER_DOMICILE(PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL, "personalDetail.domicile.id", null, false, "countryOfBirth", DISPLAY_PROPERTY), //
    INSTITUTION_NAME(PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION, "institution.name", null, false, "institution", STRING), //
    DEPARTMENT_NAME(PrismDisplayPropertyDefinition.SYSTEM_DEPARTMENT, "department.name", null, false, "department", STRING), //
    PROGRAM_NAME(PrismDisplayPropertyDefinition.SYSTEM_PROGRAM, "program.name", null, false, "program", STRING), //
    PROJECT_NAME(PrismDisplayPropertyDefinition.SYSTEM_PROJECT, "project.name", null, false, "project", STRING), //
    APPLICATION_YEAR(PrismDisplayPropertyDefinition.SYSTEM_ACADEMIC_YEAR, "application.applicationYear", null, false, "applicationYear", STRING), //
    CREATED_DATE(PrismDisplayPropertyDefinition.SYSTEM_CREATED_DATE, "application.createdTimestamp", null, false, "createdDate", DATE), //
    CLOSING_DATE(PrismDisplayPropertyDefinition.SYSTEM_CLOSING_DATE, "application.closingDate", null, false, "closingDate", DATE), //
    SUBMITTED_DATE(PrismDisplayPropertyDefinition.APPLICATION_SUBMISSION_DATE, "application.submittedTimestamp", null, false, "submittedDate", DATE), //
    UPDATED_DATE(PrismDisplayPropertyDefinition.SYSTEM_UPDATED_DATE, "application.updatedTimestamp", null, false, "updatedDate", DATE), //
    RATING_COUNT(PrismDisplayPropertyDefinition.SYSTEM_TOTAL_RATING, "application.applicationRatingCount", null, true, "ratingCount", STRING), //
    RATING_AVERAGE(PrismDisplayPropertyDefinition.SYSTEM_AVERAGE_RATING, "application.applicationRatingAverage", null, true, "ratingAverage", STRING), //
    STATE(PrismDisplayPropertyDefinition.SYSTEM_STATE, "state.stateGroup.id", null, false, "state", DISPLAY_PROPERTY), //
    REFEREE_COUNT(PrismDisplayPropertyDefinition.APPLICATION_REFEREES, "count(distinct referee.id)", null, false, "referees", STRING), //
    PROVIDED_REFERENCE_COUNT(PrismDisplayPropertyDefinition.APPLICATION_PROVIDED_REFERENCES, "count(distinct provideReferenceComment.id)", null, false, "providedReferences", STRING), //
    DECLINED_REFERENCE_COUNT(PrismDisplayPropertyDefinition.APPLICATION_DECLINED_REFERENCES, "count(distinct declineReferenceComment.id)", null, false, "declinedReferences", STRING), //
    START_DATE(PrismDisplayPropertyDefinition.APPLICATION_CONFIRMED_START_DATE, "application.confirmedStartDate", null, false, "confirmedStartDate", DATE);

    private PrismDisplayPropertyDefinition title;

    private String column;

    private List<PrismWorkflowConstraint> definitions;

    private boolean hasRedactions;

    private String accessor;

    private PrismApplicationReportColumnAccessorType accessorType;

    private PrismApplicationReportColumn(PrismDisplayPropertyDefinition title, String column, List<PrismWorkflowConstraint> definitions,
            boolean hasRedactions, String accessor, PrismApplicationReportColumnAccessorType accessorType) {
        this.title = title;
        this.column = column;
        this.definitions = definitions;
        this.hasRedactions = hasRedactions;
        this.accessor = accessor;
        this.accessorType = accessorType;
    }

    public final PrismDisplayPropertyDefinition getTitle() {
        return title;
    }

    public final String getColumn() {
        return column;
    }

    public final List<PrismWorkflowConstraint> getDefinitions() {
        return definitions == null ? Collections.<PrismWorkflowConstraint> emptyList() : definitions;
    }

    public boolean isHasRedactions() {
        return hasRedactions;
    }

    public final String getAccessor() {
        return accessor;
    }

    public final PrismApplicationReportColumnAccessorType getAccessorType() {
        return accessorType;
    }

    public final String getColumnAccessor() {
        return column + " as " + accessor;
    }

}
