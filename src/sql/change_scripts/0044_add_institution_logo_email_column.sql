ALTER TABLE institution
ADD COLUMN logo_image_email_id INT(10) UNSIGNED AFTER logo_image_id
;

UPDATE institution
SET logo_image_email_id = logo_image_id
;

