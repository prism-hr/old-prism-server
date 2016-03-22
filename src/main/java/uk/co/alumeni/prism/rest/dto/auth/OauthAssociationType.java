package uk.co.alumeni.prism.rest.dto.auth;

public enum OauthAssociationType {

    ASSOCIATE_CURRENT_USER, // in account settings
    ASSOCIATE_SPECIFIED_USER, // in invitation registration dialog
    ASSOCIATE_NEW_USER, // when applying or creating new resource
    AUTHENTICATE // regular login

}
