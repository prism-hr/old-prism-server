CREATE TABLE PROGRAM_TMP (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `academic_year` varchar(4) NOT NULL,
  `start_date` date NOT NULL,
  `deadline` date NOT NULL,
  `study_option` varchar(50) DEFAULT NULL,
  `study_code` varchar(50) NOT NULL DEFAULT -1,
  `identifier` varchar(50) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO PROGRAM_TMP (code, title, academic_year, start_date, deadline, study_code, identifier, study_option, enabled) VALUES 
("DDNBENSING09", "EngD Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0009", "Full-time", true), 
("DDNCIVSUSR09", "EngD Urban Sustainability and Resilience", "2013", "2013-09-23", "2014-09-15", "F+++++", "0009", "Full-time", true), 
("DDNCOMSVEI09", "EngD Virtual Environments, Imaging and Visualisation", "2013", "2013-09-23", "2014-09-15", "F+++++", "0009", "Full-time", true), 
("DDNPRFSING01", "EngD Professional Services", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDBENSING01", "Research Degree:  Biochemical Engineering", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDCENSING01", "Research Degree:  Chemical Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDCIVSGEO01", "Research Degree:  Civil, Environmental and Geomatic Engineering", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDCOMSING01", "Research Degree:  Computer Science", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDEENSING01", "Research Degree:  Electronic and Electrical Engineering", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDMANSING01", "Research Degree: Management Science and Innovation", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDMBISING01", "Research Degree: Medical and Biomedical Imaging", "2013", "2013-09-23", "2014-09-15", "F+++++", "0013", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDMECSING01", "Research Degree:  Mechanical Engineering", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2013", "2013-09-23", "2014-09-15", "F+++++", "0014", "Full-time", true), 
("RRDMPHSING01", "Research Degree:  Medical Physics", "2013", "2013-09-23", "2014-09-15", "P+++++", "0015", "Part-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2013", "2013-09-23", "2014-09-15", "F+++++", "0009", "Full-time", true), 
("RRDSCSSING01", "Research Degree: Security and Crime Science", "2013", "2013-09-23", "2014-09-15", "P+++++", "0010", "Part-time", true), 
("RRDSECSING01", "Research Degree: Security Science", "2013", "2013-09-23", "2014-09-15", "F+++++", "0013", "Full-time", true), 
("TMRBENSING01", "MRes Biochemical Engineering", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRCIVSUSR01", "MRes Urban Sustainability and Resilience", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRCOMSFNC01", "MRes Financial Computing", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRCOMSVEI01", "MRes Virtual Environments, Imaging and Visualisation", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRCOMSWEB01", "MRes Web Science", "2013", "2013-09-23", "2014-09-22", "F+++++", "0003", "Full-time", true), 
("TMREENSPHT01", "MRes Photonics Systems Development", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRHEAAWLB01", "MRes Lifelong Health and Wellbeing", "2013", "2013-09-23", "2014-09-22", "F+++++", "0007", "Full-time", true), 
("TMRHEAAWLB01", "MRes Lifelong Health and Wellbeing", "2013", "2013-09-23", "2014-09-22", "P+++++", "0009", "Part-time", true), 
("TMRMBISING01", "MRes Medical and Biomedical Imaging", "2013", "2013-09-23", "2014-09-22", "F+++++", "0009", "Full-time", true), 
("TMRMPHSING01", "MRes Medical Physics and Bioengineering", "2013", "2013-09-23", "2014-09-22", "B+++++", "0007", "Modular/flexible study", true), 
("TMRMPHSING01", "MRes Medical Physics and Bioengineering", "2013", "2013-09-23", "2014-09-22", "F+++++", "0008", "Full-time", true), 
("TMRMSISING01", "MRes Management Sciences and Innovation", "2013", "2013-09-23", "2014-09-22", "F+++++", "0005", "Full-time", true), 
("TMRSECSING01", "MRes Security Science", "2013", "2013-09-23", "2014-09-22", "B+++++", "0011", "Modular/flexible study", true), 
("TMRSECSING01", "MRes Security Science", "2013", "2013-09-23", "2014-09-22", "F+++++", "0012", "Full-time", true), 
("TMRTELSING01", "MRes Telecommunications", "2013", "2013-09-23", "2014-09-22", "F+++++", "0011", "Full-time", true)
;

UPDATE PROGRAM_INSTANCE a
INNER JOIN PROGRAM b ON a.program_id = b.id
INNER JOIN PROGRAM_TMP c ON b.code = c.code
SET a.deadline = c.deadline,
a.study_option = c.study_option,
a.study_code = c.study_code,
a.academic_year = c.academic_year,
a.start_date = c.start_date, 
a.identifier = c.identifier
WHERE a.academic_year = c.academic_year
;

DROP TABLE PROGRAM_TMP
;