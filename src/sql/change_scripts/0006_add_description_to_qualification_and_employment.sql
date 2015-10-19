ALTER TABLE user_qualification
ADD COLUMN description TEXT
AFTER advert_id
;

ALTER TABLE application_qualification
ADD COLUMN description TEXT
AFTER advert_id
;

ALTER TABLE user_employment_position
ADD COLUMN description TEXT NOT NULL
AFTER advert_id
;

ALTER TABLE application_employment_position
ADD COLUMN description TEXT NOT NULL
AFTER advert_id
;
