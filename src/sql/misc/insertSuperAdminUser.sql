Insert into REGISTERED_USER(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled, firstName, lastName, email) values ('john@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 1, 1, 1, 1, 'john', 'doe', 'john@test.com')
;
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='john@test.com'), (select id from APPLICATION_ROLE where authority='SUPERADMINISTRATOR'));
