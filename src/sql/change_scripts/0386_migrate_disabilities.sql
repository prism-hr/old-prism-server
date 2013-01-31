CREATE TABLE `DISABILITY_TMP` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `code` int(10) NOT NULL DEFAULT -1,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO DISABILITY_TMP (name, code, enabled) VALUES 
("No Disability", "0", true), 
("2 or more Impairments/med conditions", "8", true), 
("Learning Difficulty", "51", true), 
("Autistic Spectrum Disorder", "53", true), 
("Unseen Disability", "54", true), 
("Mental Health Disability", "55", true), 
("Mobility Difficulties", "56", true), 
("Hearing Impairment", "57", true), 
("Visual Impairment", "58", true), 
("Disability Not Listed", "96", true)
;

ALTER TABLE DISABILITY ADD COLUMN code INT(10) NOT NULL DEFAULT -1 AFTER name
;

ALTER TABLE DISABILITY ADD INDEX disability_code_idx (code)
;

UPDATE DISABILITY a 
INNER JOIN DISABILITY_TMP b ON a.name = b.name 
SET a.code = b.code
;

UPDATE DISABILITY a 
SET a.enabled = false 
WHERE a.code = -1
;

INSERT INTO DISABILITY (name, code, enabled) 
SELECT t.name, t.code, true FROM DISABILITY_TMP t 
WHERE t.code NOT IN (SELECT x.code FROM DISABILITY x)
;

DROP TABLE DISABILITY_TMP
;
