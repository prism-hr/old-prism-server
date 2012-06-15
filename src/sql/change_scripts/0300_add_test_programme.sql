insert into PROGRAM(code, title) values ('ABCDEFGHIJ01','Test-Programme 1')
;
insert into PROGRAM_INSTANCE(program_id, deadline, sequence, study_option) values((select id from PROGRAM where code = 'ABCDEFGHIJ01'), '2012-08-24', 7, 'FULL_TIME')
;
insert into PROGRAM_INSTANCE(program_id, deadline, sequence, study_option) values((select id from PROGRAM where code = 'ABCDEFGHIJ01'), '2011-08-26', 5, 'FULL_TIME')
;
insert into PROGRAM_INSTANCE(program_id, deadline, sequence, study_option) values((select id from PROGRAM where code = 'ABCDEFGHIJ01'), '2013-08-23', 9, 'FULL_TIME')
;