Insert into REGISTERED_USER(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled, firstName, lastName, email) values ('john', 'password', 1, 1, 1, 1, 'jane', 'doe', 'email@test.com')
;
Insert into USER_ROLE_LINK (registered_user_id, application_role_id) values(5, 5)
;