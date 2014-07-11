CREATE TABLE `QUALIFICATION_TYPE_TMP` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `code` varchar(10) NOT NULL DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO QUALIFICATION_TYPE_TMP (name, code, enabled) VALUES 
("Other examinations and/or information", "6", true), 
("Bachelors degree with 1-10 grading scheme", "DEG10", true), 
("Bachelors degree with 110 grading scheme", "DEG110", true), 
("Bachelors degree with 1-12 grading scheme", "DEG12", true), 
("Bachelors degree with 1-20 grading scheme", "DEG20", true), 
("Bachelors degree with 1-5 grading scheme", "DEG5", true), 
("Bachelors degree with 5-10 grading scheme", "DEG510", true), 
("Bachelors degree with 1-6 grading scheme", "DEG6", true), 
("Bachelors degree with 1-7 grading scheme", "DEG7", true), 
("Bachelors degree with 1-9 grading scheme", "DEG9", true), 
("Bachelors degree with A+ - F grading scheme", "DEGA+", true), 
("Bachelors degree with credit / merit grading scheme", "DEGCRD", true), 
("Bachelors degree - Czech Republic / Slovakia", "DEGCZ", true), 
("Bachelors degree with grading scheme of Excel, V.Good, Good, Satisfactory", "DEGEXL", true), 
("Bachelors Degree - India", "DEGHIN", true), 
("Bachelors degree with UK honours grading scheme", "DEGHON", true), 
("MEng UK degree with honours grading scheme", "DEGHONMENG", true), 
("MSci UK degree with honours grading scheme", "DEGHONMSCI", true), 
("Bachelors degree CGPA 4.0 grading scheme", "DEGP40", true), 
("Bachelors degree CGPA 4.3 grading scheme", "DEGP43", true), 
("Bachelors degree CGPA 4.5 grading scheme", "DEGP45", true), 
("Bachelors degree with grading scheme of honours (pass)", "DEGPAS", true), 
("Bachelors Degree - not listed elsewhere", "DEGREE", true), 
("Bachelors Degree - Spain", "DEGSPN", true), 
("Bachelors Degree - Sweden", "DEGSWE", true), 
("Bachelors Degree - France", "DEGTRE", true), 
("Bachelors degree with precentage grading scheme", "DEG_%", true), 
("Masters Degree - not listed elsewhere", "MASTER", true), 
("MPhil degree (Postgraduate)", "MPHIL", true), 
("Masters degree with 1-10 grading scheme", "MST10", true), 
("Masters degree with 110 grading scheme", "MST110", true), 
("Masters degree with 1-12 grading scheme", "MST12", true), 
("Masters degree with 1-20 grading scheme", "MST20", true), 
("Masters degree with 1-5 grading scheme", "MST5", true), 
("Masters degree with 5-10 grading scheme", "MST510", true), 
("Masters degree with 1-6 grading scheme", "MST6", true), 
("Masters degree with 1-7 grading scheme", "MST7", true), 
("Masters degree with 1-9 grading scheme", "MST9", true), 
("Masters degree with A+ - F grading scheme", "MSTA+", true), 
("Masters degree with credit / merit grading scheme", "MSTCRD", true), 
("Masters degree Czech Republic / Slovakia", "MSTCZ", true), 
("Masters degree with grading scheme of Excel, V.Good, Good, Satisisfactory", "MSTEXL", true), 
("Masters degree with grading scheme of honours - India", "MSTHIN", true), 
("Masters degree CGPA 4.0 grading scheme", "MSTP40", true), 
("Masters degree CGPA 4.3 grading scheme", "MSTP43", true), 
("Masters degree CGPA 4.5 grading scheme", "MSTP45", true), 
("Masters degree with grading scheme of honours (pass)", "MSTPAS", true), 
("Masters Degree - Spain", "MSTSPN", true), 
("Masters Degree - Sweden", "MSTSWE", true), 
("Masters Degree - France", "MSTTRE", true), 
("Masters degree with percentage grading scheme", "MST_%", true), 
("Non-Honours Ordinary Degree", "ORD", true), 
("PhD degree (postgraduate)", "PHD", true)
;

ALTER TABLE QUALIFICATION_TYPE ADD COLUMN code varchar(10) NOT NULL DEFAULT '' AFTER name
;

ALTER TABLE QUALIFICATION_TYPE ADD INDEX language_code_idx (code)
;

UPDATE QUALIFICATION_TYPE a 
INNER JOIN QUALIFICATION_TYPE_TMP b ON a.name = b.name 
SET a.code = b.code
;

UPDATE QUALIFICATION_TYPE a 
SET a.enabled = false 
WHERE a.code = ''
;

INSERT INTO QUALIFICATION_TYPE (name, code, enabled) 
SELECT t.name, t.code, true FROM QUALIFICATION_TYPE_TMP t 
WHERE t.code NOT IN (SELECT x.code FROM QUALIFICATION_TYPE x)
;

DROP TABLE QUALIFICATION_TYPE_TMP
;
