ALTER TABLE INTERVIEW ADD COLUMN stage VARCHAR(15) NOT NULL DEFAULT 'INITIAL'
;

-- all existing interviews are in 'scheduled' stage:
UPDATE INTERVIEW SET stage = 'SCHEDULED'
;
