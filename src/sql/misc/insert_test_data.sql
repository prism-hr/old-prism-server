Insert into PROGRAM(code, description, title) values ('TEST-PROG-ABC', "Test Program One Description", "Test Program One" );
Insert into PROGRAM(code, description, title) values ('TEST-PROG-DEF', "Test Program Two Description", "Test Program Two" );

Insert into PROJECT(code, description, title, program_id) values ('TEST-PROJ-1', 'Test Project One Description', 'Test Project One Title', (select id from PROGRAM where code='TEST-PROG-ABC'));
Insert into PROJECT(code, description, title, program_id) values ('TEST-PROJ-2', 'Test Project Two Description', 'Test Project Two Title', (select id from PROGRAM where code='TEST-PROG-ABC'));
Insert into PROJECT(code, description, title, program_id) values ('TEST-PROJ-3', 'Test Project Three Description', 'Test Project Three Title', (select id from PROGRAM where code='TEST-PROG-DEF'));
Insert into PROJECT(code, description, title, program_id) values ('TEST-PROJ-4', 'Test Project Four Description', 'Test Project Four Title', (select id from PROGRAM where code='TEST-PROG-DEF'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('alice@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Alice', 'Aaronson', 'alice@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='alice@test.com'), (select id from APPLICATION_ROLE where authority='APPROVER'));
Insert into PROGRAM_APPROVER_LINK(program_id, registered_user_id) values ((select id from PROGRAM where code='TEST-PROG-ABC'), (select id from REGISTERED_USER where username='alice@test.com'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('bert@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Bert', 'Bronson', 'bert@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='bert@test.com'), (select id from APPLICATION_ROLE where authority='APPROVER'));
Insert into PROGRAM_APPROVER_LINK(program_id, registered_user_id) values ((select id from PROGRAM where code='TEST-PROG-DEF'), (select id from REGISTERED_USER where username='bert@test.com'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('charlie@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Charles', 'Chadworth', 'charlie@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='charlie@test.com'), (select id from APPLICATION_ROLE where authority='REVIEWER'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('dorris@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Dorris', 'Danson', 'dorris@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='dorris@test.com'), (select id from APPLICATION_ROLE where authority='REVIEWER'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('ellis@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Ellis', 'Ericson', 'ellis@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='ellis@test.com'), (select id from APPLICATION_ROLE where authority='REVIEWER'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('fabian@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Fabian', 'Fowler', 'fabian@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='fabian@test.com'), (select id from APPLICATION_ROLE where authority='REVIEWER'));


Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('george@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'George', 'Greir', 'george@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='george@test.com'), (select id from APPLICATION_ROLE where authority='ADMINISTRATOR'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('hetta@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Hetta', 'Harrison', 'hetta@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='hetta@test.com'), (select id from APPLICATION_ROLE where authority='ADMINISTRATOR'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('ian@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Ian', 'Idle', 'ian@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='ian@test.com'), (select id from APPLICATION_ROLE where authority='APPLICANT'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('janet@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'Janet', 'Jacobson', 'janet@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='janet@test.com'), (select id from APPLICATION_ROLE where authority='APPLICANT'));

Insert into REGISTERED_USER(username, password,firstName, lastName,email, accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('john@test.com', '5f4dcc3b5aa765d61d8327deb882cf99', 'John', 'Smith', 'john@test.com', 1, 1, 1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='john@test.com'), (select id from APPLICATION_ROLE where authority='SUPERADMINISTRATOR'));

Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values((select id from REGISTERED_USER where username='alice@test.com'), (select id from APPLICATION_ROLE where authority='ADMINISTRATOR'));

Insert into PROGRAM_APPROVER_LINK(program_id, registered_user_id) values ((select id from PROGRAM where code='TEST-PROG-ABC'), (select id from REGISTERED_USER where username='fabian@test.com'));