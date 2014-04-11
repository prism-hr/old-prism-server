package com.zuehlke.pgadmissions.controllers.locations;

public class RedirectLocation {

    public static final String REDIRECT = "redirect:/";
    public static final String UPDATE_APPLICATION_SECTION = REDIRECT + "update/";
    public static final String UPDATE_APPLICATION = REDIRECT + "application/";
    public static final String LOGIN = REDIRECT + "login";
    public static final String APPLICATIONS = "applications/";
    public static final String APPLICATION_LIST = REDIRECT + APPLICATIONS;
    public static final String ACTIVATION_CODE = "activationCode=";
    public static final String APPLICATION_ID = "applicationId=";

    public static final String UPDATE_APPLICATION_ADDITIONAL_INFORMATION = UPDATE_APPLICATION_SECTION + "getAdditionalInformation?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_ADDRESS = UPDATE_APPLICATION_SECTION + "getAddress?applicationId=";
    public static final String UPDATE_APPLICATION_EMPLOYMENT_POSITION = UPDATE_APPLICATION_SECTION + "getEmploymentPosition?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_QUALIFICATION = UPDATE_APPLICATION_SECTION + "getQualification?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_FUNDING = UPDATE_APPLICATION_SECTION + "getFunding?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_ACCEPTED_TERMS = UPDATE_APPLICATION + "?view=view&" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_DOCUMENT = UPDATE_APPLICATION_SECTION + "getDocuments?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_PERSONAL_DETAIL = UPDATE_APPLICATION_SECTION + "getPersonalDetails?" + APPLICATION_ID;
    public static final String UPDATE_APPLICATION_PROGRAM_DETAIL = UPDATE_APPLICATION_SECTION + "getProgramDetails?" + APPLICATION_ID;

    public static final String CREATE_APPLICATION = UPDATE_APPLICATION + "?applicationId=";
    public static final String UPDATE_APPLICATION_AS_STAFF = "editApplicationFormAsProgrammeAdmin?" + APPLICATION_ID;

}
