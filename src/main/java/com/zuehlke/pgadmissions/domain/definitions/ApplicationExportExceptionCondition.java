package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.ApplicationExportExceptionHandlingStrategy.GIVE_UP;
import static com.zuehlke.pgadmissions.domain.definitions.ApplicationExportExceptionHandlingStrategy.RETRY_SYSTEM_INVOCATION_DELAYED;
import static com.zuehlke.pgadmissions.domain.definitions.ApplicationExportExceptionHandlingStrategy.RETRY_SYSTEM_INVOCATION_IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.ApplicationExportExceptionHandlingStrategy.RETRY_USER_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_DATE_OF_BIRTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_NATIONALITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_POSITION_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_PROGRAM_INSTANCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_QUALIFICATION_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_REFEREE_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_REFEREE_PHONE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_UNCLASSIFED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_MOCK_ADDRESS_LINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_EXPORT_MOCK_PHONE;

import java.util.List;

import com.google.common.collect.Lists;

public enum ApplicationExportExceptionCondition {

    REFEREE_ADDRESS_LINE_1(Lists.newArrayList("Referee address line1 validation failed"), RETRY_SYSTEM_INVOCATION_IMMEDIATE,
            APPLICATION_EXPORT_EXCEPTION_REFEREE_ADDRESS, APPLICATION_EXPORT_MOCK_ADDRESS_LINE), //
    REFEREE_PHONE(Lists.newArrayList("Referee landline  validation failed"), RETRY_SYSTEM_INVOCATION_IMMEDIATE, APPLICATION_EXPORT_EXCEPTION_REFEREE_PHONE,
            APPLICATION_EXPORT_MOCK_PHONE), //
    POSITION_DESCRIPTION(Lists.newArrayList("ATAS statement validation failed"), RETRY_SYSTEM_INVOCATION_IMMEDIATE,
            APPLICATION_EXPORT_EXCEPTION_POSITION_DESCRIPTION, PrismDisplayPropertyDefinition.APPLICATION_EXPORT_MOCK_POSITION_DESCRIPTION), //
    PROGRAM(Lists.newArrayList("Programme validation failed"), RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_PROGRAM, null), //
    PROGRAM_INSTANCE(Lists.newArrayList("No export program instance"), GIVE_UP, APPLICATION_EXPORT_EXCEPTION_PROGRAM_INSTANCE, null), //
    QUALIFICATION_DATE(Lists.newArrayList("Qualification dates  validation failed"), RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_QUALIFICATION_DATE,
            null), //
    DOCUMENT(Lists.newArrayList("PdfReader not opened with owner password"), RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_DOCUMENT, null), //
    NATIONALITY(Lists.newArrayList("Nationality validation failed"), RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_NATIONALITY, null), //
    DATE_OF_BIRTH(Lists.newArrayList("Date of birth validation failed"), RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_DATE_OF_BIRTH, null), //
    UNAVAILABLE(Lists.newArrayList("Service Temporarily Unavailable", "Connection timed out"), RETRY_SYSTEM_INVOCATION_DELAYED,
            PrismDisplayPropertyDefinition.APPLICATION_EXPORT_EXCEPTION_UNAVAILABLE, null), //
    UNCLASSIFIED(null, RETRY_USER_INVOCATION, APPLICATION_EXPORT_EXCEPTION_UNCLASSIFED, null);

    private List<String> searchPhrases;

    private ApplicationExportExceptionHandlingStrategy handlingStrategy;

    private PrismDisplayPropertyDefinition classification;

    private PrismDisplayPropertyDefinition substituteValue;

    private ApplicationExportExceptionCondition(List<String> searchPhrases, ApplicationExportExceptionHandlingStrategy handlingStrategy,
            PrismDisplayPropertyDefinition classification, PrismDisplayPropertyDefinition substituteValue) {
        this.searchPhrases = searchPhrases;
        this.handlingStrategy = handlingStrategy;
        this.classification = classification;
        this.substituteValue = substituteValue;
    }

    public final List<String> getSearchPhrases() {
        return searchPhrases == null ? Lists.<String> newArrayList() : searchPhrases;
    }

    public final ApplicationExportExceptionHandlingStrategy getHandlingStrategy() {
        return handlingStrategy;
    }

    public final void setHandlingStrategy(ApplicationExportExceptionHandlingStrategy handlingStrategy) {
        this.handlingStrategy = handlingStrategy;
    }

    public final PrismDisplayPropertyDefinition getClassification() {
        return classification;
    }

    public final PrismDisplayPropertyDefinition getSubstituteValue() {
        return substituteValue;
    }

    public static ApplicationExportExceptionCondition getCondition(String exception) {
        for (ApplicationExportExceptionCondition condition : values()) {
            if (condition.getSearchPhrases().contains(exception)) {
                return condition;
            }
        }
        return UNCLASSIFIED;
    }

}
