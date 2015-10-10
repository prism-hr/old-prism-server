ALTER TABLE user_qualification
ADD COLUMN description TEXT NOT NULL
AFTER award_month
;

ALTER TABLE application_qualification
ADD COLUMN description TEXT NOT NULL
AFTER award_month
;

ALTER TABLE user_employment_position
ADD COLUMN description TEXT NOT NULL
AFTER end_month
;

ALTER TABLE application_employment_position
ADD COLUMN description TEXT NOT NULL
AFTER end_month
;
