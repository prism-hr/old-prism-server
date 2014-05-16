/* Remove pointless roles */

DELETE
FROM USER_ROLE
WHERE role_id = "PROGRAM_PROJECT_CREATOR"
;

DELETE
FROM USER_ROLE
WHERE role_id = "INSTITUTION_PROGRAM_CREATOR"
;

DROP TABLE ROLE_INHERITANCE
;

DELETE
FROM ROLE
WHERE id = "PROGRAM_PROJECT_CREATOR"
;

/* Assign supervisor continued */



/* Reconfigure use custom question flags */
/* Find and expose move to different stage comments */

/* Fix null constraints on comment table */