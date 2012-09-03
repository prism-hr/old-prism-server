insert into PROGRAM_INSTANCE(program_id, deadline, sequence, study_option) values((select id from PROGRAM where code = 'ABC'), '2020-08-24',4,'FULL-TIME')
;