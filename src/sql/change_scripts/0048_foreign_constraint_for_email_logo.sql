ALTER TABLE institution
ADD CONSTRAINT `logo_image_email_fk` FOREIGN KEY (logo_image_email_id) REFERENCES document (id)
;
