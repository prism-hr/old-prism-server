package com.zuehlke.pgadmissions.rest.dto.auth;

public enum OauthAssociationType {
    ASSOCIATE_WITH_CURRENT_USER, // in account settings
    ASSOCIATE_WITH_PROVIDED_USER, // in invitation registration dialog
    CREATE_NEW_USER, // when applying or creating new resource
    ONLY_AUTHENTICATE // regular login
}
