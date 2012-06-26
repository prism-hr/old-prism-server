Insert into PROGRAM(code, description, title) values ('ABC', "Description1", "Program Title1" )
;

Insert into PROGRAM_APPROVER_LINK(program_id, registered_user_id) values ((select id from PROGRAM where code='ABC'), 4);

Insert into PROJECT(code, description, title, program_id) values ('KLM', 'Very important research', 'My research', (select id from PROGRAM where code='ABC'));

Insert into PROJECT(code, description, title, program_id) values ('OPT', 'Not Very important research', 'My favourite research', (select id from PROGRAM where code='ABC'));

Insert into PROJECT(code, description, title, program_id) values ('TRE', 'important research', 'research', (select id from PROGRAM where code='ABC'));