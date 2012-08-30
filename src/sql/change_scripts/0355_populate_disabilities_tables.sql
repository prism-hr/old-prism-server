SET FOREIGN_KEY_CHECKS = 0
;
DELETE FROM DISABILITY;
;
ALTER TABLE DISABILITY ADD enabled BOOLEAN NOT NULL DEFAULT FALSE
;
INSERT INTO DISABILITY (name, enabled) VALUES 
	('No Disability', TRUE),
	('Autistic Spectrum Disorder', TRUE),
	('visual Impairment', TRUE),
	('Hearing Impairment', TRUE),
	('Unseen Disability', TRUE),
	('Mental Health Disability', TRUE),
	('Learning Difficulty', TRUE),
	('Mobility Difficulties', TRUE),
	('Disability Not Listed', TRUE),
	('2 or more Impairments/med conditions', TRUE)
;
SET FOREIGN_KEY_CHECKS = 1
;