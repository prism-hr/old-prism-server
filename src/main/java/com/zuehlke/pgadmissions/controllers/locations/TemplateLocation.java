package com.zuehlke.pgadmissions.controllers.locations;

public class TemplateLocation {
    
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private/";
    
    public static final String REGISTER = "register/";
    public static final String FORM = "main_application_page";
    public static final String EMBEDDED_FORM = "main_application_page_without_headers";
    
    public static final String REGISTRATION_FORM = "register_applicant";
    public static final String REGISTRATION_FAILURE_CONFIRMATION = "activation_failed";
    public static final String REGISTRATION_SUCCESS_CONFIRMATION = "registration_completed";
    
    public static final String APPLICATION_APPLICANT = PRIVATE + "pgStudents/form/";
    public static final String APPLICATION_APPLICANT_COMPONENT = APPLICATION_APPLICANT + "components/";
    
    public static final String APPLICATION_APPLICANT_FORM = APPLICATION_APPLICANT + FORM;
    public static final String APPLICATION_APPLICANT_TERMS_AND_CONDITIONS = APPLICATION_APPLICANT_COMPONENT + "terms_and_conditions";
    public static final String APPLICATION_APPLICANT_ADDITIONAL_INFORMATION = APPLICATION_APPLICANT_COMPONENT +  "additional_information";
    public static final String APPLICATION_APPLICANT_ADDRESS = APPLICATION_APPLICANT_COMPONENT + "address_details";
    public static final String APPLICATION_APPLICANT_DOCUMENT = APPLICATION_APPLICANT_COMPONENT + "documents";
    public static final String APPLCIATION_APPLICANT_EMPLOYMENT_POSITION = APPLICATION_APPLICANT_COMPONENT + "employment_position_details";
    public static final String APPLICATION_APPLICANT_QUALIFICATION = APPLICATION_APPLICANT_COMPONENT + "qualification_details";
    public static final String APPLICATION_APPLICANT_FUNDING = APPLICATION_APPLICANT_COMPONENT + "funding_details";
    public static final String APPLICATION_APPLICANT_PERSONAL_DETAIL = APPLICATION_APPLICANT_COMPONENT + "personal_details";
    
    public static final String APPLICATION_STAFF = PRIVATE + "staff/application";
    public static final String APPLICATION_STAFF_FORM = APPLICATION_STAFF + FORM;
    public static final String APPLICATION_STAFF_EMBEDDED_FORM = APPLICATION_STAFF + EMBEDDED_FORM;
    
    public static final String MY_ACCOUNT_PAGE = PRIVATE + "/my_account";
    public static final String MY_ACCOUNT_SECTION = PRIVATE + "/my_account_section";

}
