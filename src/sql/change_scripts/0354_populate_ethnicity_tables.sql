SET FOREIGN_KEY_CHECKS = 0
;
DELETE FROM ETHNICITY;
;
ALTER TABLE ETHNICITY ADD enabled BOOLEAN NOT NULL DEFAULT FALSE
;
INSERT INTO ETHNICITY (name, enabled) VALUES 
	('White', TRUE),
	('Gypsy or Traveller', TRUE),
	('Black or Black British - Caribbean', TRUE),
	('Black or Black British - African', TRUE),
	('Black - Other background', TRUE),
	('Asian or Asian British - Indian', TRUE),
	('Asian or Asian British - Pakistani', TRUE),
	('Asian or Asian British - Bangladeshi', TRUE),
	('Chinese', TRUE),
	('Asian - other background', TRUE),
	('Mixed - White and Black Caribbean', TRUE),
	('Mixed - White and Black African', TRUE),
	('Mixed - White and Asian', TRUE),
	('Mixed - other background', TRUE),
	('Arab', TRUE),
	('Other Ethnic background', TRUE),
	('Information refused', TRUE)
;
SET FOREIGN_KEY_CHECKS = 1
;