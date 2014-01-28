ALTER TABLE APPROVAL_ROUND ADD COLUMN project_description_available tinyint(1)
;

ALTER TABLE APPROVAL_ROUND ADD COLUMN project_title varchar(100)
;

ALTER TABLE APPROVAL_ROUND ADD COLUMN project_abstract varchar(2000)
;

ALTER TABLE APPROVAL_ROUND ADD COLUMN recommended_start_date date
;

ALTER TABLE APPROVAL_ROUND ADD COLUMN recommended_conditions_available tinyint(1)
;

ALTER TABLE APPROVAL_ROUND ADD COLUMN recommended_conditions varchar(1000)
;
