SET FOREIGN_KEY_CHECKS = 0
;
DELETE FROM PROGRAM;
;
ALTER TABLE PROGRAM ADD COLUMN enabled BOOLEAN DEFAULT FALSE NOT NULL AFTER title
;
DELETE FROM PROGRAM_INSTANCE
;
ALTER TABLE PROGRAM_INSTANCE DROP COLUMN sequence
;
ALTER TABLE PROGRAM_INSTANCE ADD COLUMN academic_year VARCHAR(4) NOT NULL AFTER study_option
;
ALTER TABLE PROGRAM_INSTANCE ADD COLUMN start_date DATE NOT NULL AFTER academic_year
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRMBISING01", "MRes Medical and Biomedical Imaging")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMBISING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMBISING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRHEAAWLB01", "MRes Lifelong Health and Wellbeing")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRHEAAWLB01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRHEAAWLB01"), "PART_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRHEAAWLB01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRHEAAWLB01"), "PART_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRMSISING01", "MRes Management Sciences and Innovation")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMSISING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMSISING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRCOMSWEB01", "MRes Web Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSWEB01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSWEB01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDCIVSGEO01", "Research Degree: Civil, Environmental and Geomatic Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCIVSGEO01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCIVSGEO01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCIVSGEO01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCIVSGEO01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDBENSING01", "Research Degree: Biochemical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDBENSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDBENSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDBENSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDBENSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDEENSING01", "Research Degree: Electronic and Electrical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDMBISING01", "Research Degree: Medical and Biomedical Imaging")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMBISING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMBISING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDSECSING01", "Research Degree: Security Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSECSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSECSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "DDNENVSENG01", "EngD Environmental Engineering Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNENVSENG01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDMPHSING01", "Research Degree: Medical Physics")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMPHSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMPHSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMPHSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMPHSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRSECSING01", "MRes Security Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRSECSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRSECSING01"), "MODULAR_FLEXIBLE_STUDY", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRSECSING01"), "MODULAR_FLEXIBLE_STUDY", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRSECSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRCOMSFNC01", "MRes Financial Computing")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSFNC01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSFNC01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDMECSING01", "Research Degree: Mechanical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMECSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMECSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMECSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMECSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "DDNCOMSVEI09", "EngD Virtual Environments, Imaging and Visualisation")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNCOMSVEI09"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNCOMSVEI09"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRTELSING01", "MRes Telecommunications")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRTELSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRTELSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDCENSING01", "Research Degree: Chemical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCENSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCENSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDSCSSING01", "Research Degree: Security and Crime Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSCSSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSCSSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSCSSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDSCSSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "DDNBENSING09", "EngD Biochemical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNBENSING09"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNBENSING09"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "DDNPRFSING01", "EngD Professional Services")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNPRFSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNPRFSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRMPHSING01", "MRes Medical Physics and Bioengineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMPHSING01"), "MODULAR_FLEXIBLE_STUDY", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMPHSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMPHSING01"), "MODULAR_FLEXIBLE_STUDY", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRMPHSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDCOMSFNC01", "Research Degree: Financial Computing")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSFNC01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSFNC01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRBENSING01", "MRes Biochemical Engineering")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRBENSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRBENSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDEENSPHT01", "Research Degree: Photonics Systems Development")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSPHT01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDEENSPHT01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMREENSPHT01", "MRes Photonics Systems Development")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMREENSPHT01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMREENSPHT01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDCOMSING01", "Research Degree: Computer Science")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDCOMSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "DDNCIVSUSR09", "EngD Urban Sustainability and Resilience")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNCIVSUSR09"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "DDNCIVSUSR09"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RAFMANSING07", "Visiting Research: Management Science and Innovation")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RAFMANSING07"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRCIVSUSR01", "MRes Urban Sustainability and Resilience")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCIVSUSR01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCIVSUSR01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "TMRCOMSVEI01", "MRes Virtual Environments, Imaging and Visualisation")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSVEI01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-23" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "TMRCOMSVEI01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-22" )
;
INSERT INTO PROGRAM (`code`,`title`) VALUES ( "RRDMANSING01", "Research Degree: Management Science and Innovation")
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMANSING01"), "FULL_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMANSING01"), "PART_TIME", "2012", "2012-09-24", "2013-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMANSING01"), "FULL_TIME", "2013", "2013-09-23", "2014-09-15" )
;
INSERT INTO PROGRAM_INSTANCE ( `program_id`, `study_option`, `academic_year`, `start_date`, `deadline`) VALUES ( (SELECT id FROM PROGRAM WHERE code = "RRDMANSING01"), "PART_TIME", "2013", "2013-09-23", "2014-09-15" )
;
UPDATE PROGRAM SET enabled = TRUE
;
SET FOREIGN_KEY_CHECKS = 1
;