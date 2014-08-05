CREATE TABLE `ETHNICITY_TMP` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `code` int(10) NOT NULL DEFAULT -1,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16008 DEFAULT CHARSET=utf8
;

INSERT INTO ETHNICITY_TMP (name, code, enabled) VALUES 
("White", "10", true), 
("Gypsy or Traveller", "15", true), 
("Black or Black British - Caribbean", "21", true), 
("Black or Black British - African", "22", true), 
("Black - Other background", "29", true), 
("Asian or Asian British - Indian", "31", true), 
("Asian or Asian British - Pakistani", "32", true), 
("Asian or Asian British - Bangladeshi", "33", true), 
("Chinese", "34", true), 
("Asian - other background", "39", true), 
("Mixed - White and Black Caribbean", "41", true), 
("Mixed - White and Black African", "42", true), 
("Mixed - White and Asian", "43", true), 
("Mixed - other background", "49", true), 
("Arab", "50", true), 
("Other Ethnic background", "80", true), 
("Information refused", "98", true)
; 

ALTER TABLE ETHNICITY ADD COLUMN code INT(10) NOT NULL DEFAULT -1 AFTER name
;

ALTER TABLE ETHNICITY ADD INDEX ethnicity_code_idx (code)
;

UPDATE ETHNICITY a 
INNER JOIN ETHNICITY_TMP b ON a.name = b.name 
SET a.code = b.code
;

UPDATE ETHNICITY a 
SET a.enabled = false 
WHERE a.code = -1
;

INSERT INTO ETHNICITY (name, code, enabled) 
SELECT t.name, t.code, true FROM ETHNICITY_TMP t 
WHERE t.code NOT IN (SELECT x.code FROM ETHNICITY x)
;

DROP TABLE ETHNICITY_TMP
;
