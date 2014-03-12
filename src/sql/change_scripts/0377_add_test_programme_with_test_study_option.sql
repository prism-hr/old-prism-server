INSERT INTO PROGRAM(code, title, enabled) VALUES ('ABC', 'Test Programme', 1)
;

INSERT INTO PROGRAM_INSTANCE(deadline, study_option, academic_year, start_date, program_id) values('2020-08-24', 'FULL_TIME', '2020', '2011-08-24', (select id from PROGRAM where code = 'ABC'))
;
