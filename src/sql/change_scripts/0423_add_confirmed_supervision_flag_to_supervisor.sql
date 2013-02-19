ALTER TABLE SUPERVISOR ADD COLUMN confirmed_supervision tinyint(1) NOT NULL DEFAULT 0
;
ALTER TABLE SUPERVISOR ADD COLUMN declined_supervision_reason varchar(200) NULL 
;
