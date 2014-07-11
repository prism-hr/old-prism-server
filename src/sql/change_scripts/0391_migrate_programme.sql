CREATE TABLE PROGRAM_TMP (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `academic_year` varchar(4) NOT NULL,
  `start_date` date NOT NULL,
  `deadline` date NOT NULL,
  `study_option` varchar(50) DEFAULT NULL,
  `study_code` int(10) NOT NULL DEFAULT -1,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO PROGRAM_TMP (code, title, academic_year, start_date, deadline, study_code, study_option, enabled) VALUES 
("DDNBENSING09", "EngD Biochemical Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("DDNCIVSUSR09", "EngD Urban Sustainability and Resilience", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("DDNCOMSVEI09", "EngD Virtual Environments, Imaging and Visualisation", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("DDNENVSENG01", "EngD Environmental Engineering Science", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("DDNPRFSING01", "EngD Professional Services", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDCENSING01", "Research Degree:  Chemical Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDCOMSFNC01", "Research Degree: Financial Computing", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDEENSPHT01", "Research Degree: Photonics Systems Development", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDMBISING01", "Research Degree: Medical and Biomedical Imaging", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2012", "2012-09-24", "2013-09-15", "31", "Part-time", true), 
("RRDSECSING01", "Research Degree: Security Science", "2012", "2012-09-24", "2013-09-15", "1", "Full-time", true), 
("DDNBENSING09", "EngD Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("DDNCIVSUSR09", "EngD Urban Sustainability and Resilience", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("DDNCOMSVEI09", "EngD Virtual Environments, Imaging and Visualisation", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("DDNPRFSING01", "EngD Professional Services", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDCENSING01", "Research Degree:  Chemical Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDCOMSFNC01", "Research Degree: Financial Computing", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDEENSPHT01", "Research Degree: Photonics Systems Development", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDMBISING01", "Research Degree: Medical and Biomedical Imaging", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2013", "2013-09-23", "2014-09-15", "31", "Part-time", true), 
("RRDSECSING01", "Research Degree: Security Science", "2013", "2013-09-23", "2014-09-15", "1", "Full-time", true), 
("TMRBENSING01", "MRes Biochemical Engineering", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRCIVSUSR01", "MRes Urban Sustainability and Resilience", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRCOMSFNC01", "MRes Financial Computing", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRCOMSVEI01", "MRes Virtual Environments, Imaging and Visualisation", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRCOMSWEB01", "MRes Web Science", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMREENSPHT01", "MRes Photonics Systems Development", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRHEAAWLB01", "MRes Lifelong Health and Wellbeing", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRHEAAWLB01", "MRes Lifelong Health and Wellbeing", "2013", "2013-09-23", "2014-09-22", "31", "Part-time", true), 
("TMRMBISING01", "MRes Medical and Biomedical Imaging", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRMPHSING01", "MRes Medical Physics and Bioengineering", "2013", "2013-09-23", "2014-09-22", "31", "Modular/flexible study", true), 
("TMRMPHSING01", "MRes Medical Physics and Bioengineering", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRMSISING01", "MRes Management Sciences and Innovation", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRSECSING01", "MRes Security Science", "2013", "2013-09-23", "2014-09-22", "31", "Modular/flexible study", true), 
("TMRSECSING01", "MRes Security Science", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true), 
("TMRTELSING01", "MRes Telecommunications", "2013", "2013-09-23", "2014-09-22", "1", "Full-time", true)
;

UPDATE PROGRAM a 
INNER JOIN PROGRAM_TMP b ON a.code = b.code 
SET a.title = b.title
;

INSERT INTO PROGRAM (code, title, enabled)
SELECT t.code, t.title, true FROM PROGRAM_TMP t 
WHERE t.code NOT IN (SELECT x.code FROM PROGRAM x)
;

ALTER TABLE PROGRAM_INSTANCE 
ADD COLUMN study_code INT(10) NOT NULL DEFAULT -1 AFTER study_option,
ADD COLUMN enabled tinyint(1) NOT NULL DEFAULT '0'
;

ALTER TABLE APPLICATION_FORM_PROGRAMME_DETAIL
ADD COLUMN study_code INT(10) NOT NULL DEFAULT -1 AFTER project_name
;

UPDATE PROGRAM_INSTANCE a
INNER JOIN PROGRAM b ON a.program_id = b.id
INNER JOIN PROGRAM_TMP c ON b.code = c.code
SET a.deadline = c.deadline,
a.study_option = c.study_option,
a.study_code = c.study_code,
a.academic_year = c.academic_year,
a.start_date = c.start_date 
WHERE a.academic_year = c.academic_year
;

UPDATE PROGRAM_INSTANCE a
SET a.enabled = false
WHERE a.study_code = -1;
;

UPDATE PROGRAM_INSTANCE a
SET a.enabled = true
WHERE a.study_code <> -1;
;

UPDATE PROGRAM_INSTANCE
SET study_option = "Full-time",
study_code = 1
WHERE study_option = "FULL_TIME"
;

UPDATE PROGRAM_INSTANCE
SET study_option = "Part-time",
study_code = 31
WHERE study_option = "PART_TIME"
;

UPDATE PROGRAM_INSTANCE
SET study_option = "Modular/flexible study",
study_code = 31
WHERE study_option = "MODULAR_FLEXIBLE_STUDY"
;

-- START ReEnable Test Programe
UPDATE PROGRAM a
SET a.enabled = true
WHERE a.title LIKE "Test%"
;

UPDATE PROGRAM_INSTANCE
SET enabled = true 
WHERE program_id = ( SELECT id FROM PROGRAM WHERE title LIKE "Test%" )
;
-- END ReEnable Test Programe

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET study_option = "Full-time",
study_code = 1
WHERE study_option = "FULL_TIME"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET study_option = "Part-time",
study_code = 31
WHERE study_option = "PART_TIME"
;

UPDATE APPLICATION_FORM_PROGRAMME_DETAIL
SET study_option = "Modular/flexible study",
study_code = 31
WHERE study_option = "MODULAR_FLEXIBLE_STUDY" 
;

DROP TABLE PROGRAM_TMP
;