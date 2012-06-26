
INSERT INTO PROGRAM_ADMINISTRATOR_LINK(administrator_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='bob'), (SELECT id FROM PROGRAM where code='ABC'));

INSERT INTO PROGRAM_ADMINISTRATOR_LINK(administrator_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='bob'), (SELECT id FROM PROGRAM where code='ABC'));

INSERT INTO PROGRAM_APPROVER_LINK(registered_user_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='janet@test.com'), (SELECT id FROM PROGRAM where code='ABC'));

INSERT INTO PROGRAM_APPROVER_LINK(registered_user_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='hetta@test.com'), (SELECT id FROM PROGRAM where code='ABC'));

INSERT INTO PROGRAM_APPROVER_LINK(registered_user_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='hetta@test.com'), (SELECT id FROM PROGRAM where code='TEST-PROG-DEF'));

INSERT INTO PROGRAM_REVIEWER_LINK(reviewer_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='janet@test.com'), (SELECT id FROM PROGRAM where code='ABC'));

INSERT INTO PROGRAM_REVIEWER_LINK(reviewer_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='ian@test.com'), (SELECT id FROM PROGRAM where code='TEST-PROG-DEF'));

INSERT INTO PROGRAM_REVIEWER_LINK(reviewer_id, program_id) VALUES((SELECT id from REGISTERED_USER where username='hetta@test.com'), (SELECT id FROM PROGRAM where code='TEST-PROG-DEF'));