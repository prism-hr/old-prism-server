-- Modify program viewer link (viewer privilleges for all non-applicant users)
INSERT INTO PROGRAM_VIEWER_LINK 

SELECT interviewer_id AS viewer_id, program_id
FROM PROGRAM_INTERVIEWER_LINK

UNION DISTINCT

SELECT reviewer_id AS viewer_id, program_id
FROM PROGRAM_REVIEWER_LINK

UNION DISTINCT

SELECT supervisor_id AS viewer_id, program_id
FROM PROGRAM_SUPERVISOR_LINK

UNION DISTINCT

SELECT registered_user_id AS viewer_id, program_id
FROM PROGRAM_APPROVER_LINK

UNION DISTINCT

SELECT administrator_id AS viewer_id, program_id
FROM PROGRAM_ADMINISTRATOR_LINK
;


-- Modify user role link (viewer privilleges for all non-applicant users)
INSERT INTO USER_ROLE_LINK

SELECT interviewer_id AS viewer_id, (SELECT id FROM APPLICATION_ROLE WHERE authority = 'VIEWER')
FROM PROGRAM_INTERVIEWER_LINK

UNION DISTINCT

SELECT reviewer_id AS viewer_id, (SELECT id FROM APPLICATION_ROLE WHERE authority = 'VIEWER')
FROM PROGRAM_REVIEWER_LINK

UNION DISTINCT

SELECT supervisor_id AS viewer_id, (SELECT id FROM APPLICATION_ROLE WHERE authority = 'VIEWER')
FROM PROGRAM_SUPERVISOR_LINK

UNION DISTINCT

SELECT registered_user_id AS viewer_id, (SELECT id FROM APPLICATION_ROLE WHERE authority = 'VIEWER')
FROM PROGRAM_APPROVER_LINK

UNION DISTINCT

SELECT administrator_id AS viewer_id, (SELECT id FROM APPLICATION_ROLE WHERE authority = 'VIEWER')
FROM PROGRAM_ADMINISTRATOR_LINK
;
