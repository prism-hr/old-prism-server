ALTER TABLE INTERVIEW ADD COLUMN time_zone VARCHAR(15) NOT NULL
;

-- all existing interviews took place in 'GMT' timezone
UPDATE INTERVIEW SET time_zone = 'GMT'
;
