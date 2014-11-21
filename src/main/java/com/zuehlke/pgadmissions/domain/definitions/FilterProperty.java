package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.BETWEEN;
import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.CONTAIN;
import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.EQUAL;
import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.GREATER;
import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.LESSER;
import static com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType.DATE;
import static com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType.NUMBER;
import static com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType.STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType.STRING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum FilterProperty {

    USER("user.id", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    CODE("code", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    INSTITUTION_TITLE("institution.id", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PROGRAM_TITLE("program.id", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION, PROJECT)), //
    PROJECT_TITLE("project.id", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    TITLE("title", Arrays.asList(CONTAIN), STRING, Arrays.asList(PROJECT, PROGRAM, INSTITUTION)), //
    STATE_GROUP_TITLE("state.id", Arrays.asList(EQUAL), STATE_GROUP, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    CREATED_TIMESTAMP("createdTimestamp", Arrays.asList(BETWEEN, GREATER, LESSER), DATE, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUBMITTED_TIMESTAMP("submittedTimestamp", Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), DATE, Arrays.asList(APPLICATION)), //
    UPDATED_TIMESTAMP("updatedTimestamp", Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), DATE, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    DUE_DATE("dueDate", Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), DATE, Arrays.asList(APPLICATION)), //
    CLOSING_DATE("closingDate", Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), DATE, Arrays.asList(APPLICATION)), //
    STUDY_LOCATION("studyLocation", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    STUDY_DIVISION("studyDivision", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    STUDY_AREA("studyAreao", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    CONFIRMED_START_DATE("confirmed_start_date", Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), DATE, Arrays.asList(APPLICATION)), //
    RATING("applicationRatingAverage", Arrays.asList(BETWEEN, GREATER, LESSER), NUMBER, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    REFERRER("referrer", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUPERVISOR("id", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(APPLICATION)), //
    THEME("theme", Arrays.asList(CONTAIN), STRING, Arrays.asList(APPLICATION));

    private String propertyName;

    private FilterPropertyType propertyType;

    private List<FilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;

    private static HashMultimap<PrismScope, FilterProperty> permittedFilterProperties = HashMultimap.create();

    static {
        for (FilterProperty property : FilterProperty.values()) {
            for (PrismScope scope : property.getPermittedScopes()) {
                permittedFilterProperties.put(scope, property);
            }
        }
    }

    private FilterProperty(String propertyName, List<FilterExpression> permittedExpressions, FilterPropertyType valueType, List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.propertyType = valueType;
        this.permittedScopes = permittedScopes;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public FilterPropertyType getPropertyType() {
        return propertyType;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

    public static Set<FilterProperty> getPermittedFilterProperties(PrismScope scope) {
        return permittedFilterProperties.get(scope);
    }

    public static boolean isPermittedFilterProperty(PrismScope scope, FilterProperty property) {
        return FilterProperty.getPermittedFilterProperties(scope).contains(property);
    }

}
