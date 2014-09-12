ALTER TABLE INTERVIEW ADD COLUMN time_zone VARCHAR(32) NOT NULL
;

-- all existing interviews took place in 'Europe/London' timezone
UPDATE INTERVIEW SET time_zone = 'Europe/London'
;
