-- MySQL dump 10.13  Distrib 5.6.16, for Win64 (x86_64)
--
-- Host: localhost    Database: pgadmissions
-- ------------------------------------------------------
-- Server version 5.6.16-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` (`id`, `parent_state_id`, `is_under_assessment`) VALUES ('APPLICATION_APPROVAL','APPLICATION_APPROVAL',1),('APPLICATION_APPROVAL_PENDING_COMPLETION','APPLICATION_APPROVAL',1),('APPLICATION_APPROVAL_PENDING_FEEDBACK','APPLICATION_APPROVAL',1),('APPLICATION_APPROVED','APPLICATION_APPROVED',1),('APPLICATION_APPROVED_COMPLETED','APPLICATION_APPROVED',0),('APPLICATION_APPROVED_PENDING_CORRECTION','APPLICATION_APPROVED',0),('APPLICATION_APPROVED_PENDING_EXPORT','APPLICATION_APPROVED',0),('APPLICATION_INTERVIEW','APPLICATION_INTERVIEW',1),('APPLICATION_INTERVIEW_PENDING_AVAILABILITY','APPLICATION_INTERVIEW',1),('APPLICATION_INTERVIEW_PENDING_COMPLETION','APPLICATION_INTERVIEW',1),('APPLICATION_INTERVIEW_PENDING_FEEDBACK','APPLICATION_INTERVIEW',1),('APPLICATION_INTERVIEW_PENDING_INTERVIEW','APPLICATION_INTERVIEW',1),('APPLICATION_INTERVIEW_PENDING_SCHEDULING','APPLICATION_INTERVIEW',1),('APPLICATION_REJECTED','APPLICATION_REJECTED',1),('APPLICATION_REJECTED_COMPLETED','APPLICATION_REJECTED',0),('APPLICATION_REJECTED_PENDING_CORRECTION','APPLICATION_REJECTED',0),('APPLICATION_REJECTED_PENDING_EXPORT','APPLICATION_REJECTED',0),('APPLICATION_REVIEW','APPLICATION_REVIEW',1),('APPLICATION_REVIEW_PENDING_COMPLETION','APPLICATION_REVIEW',1),('APPLICATION_REVIEW_PENDING_FEEDBACK','APPLICATION_REVIEW',1),('APPLICATION_UNSUBMITTED','APPLICATION_UNSUBMITTED',0),('APPLICATION_UNSUBMITTED_PENDING_COMPLETION','APPLICATION_UNSUBMITTED',0),('APPLICATION_VALIDATION','APPLICATION_VALIDATION',1),('APPLICATION_VALIDATION_PENDING_COMPLETION','APPLICATION_VALIDATION',1),('APPLICATION_VALIDATION_PENDING_FEEDBACK','APPLICATION_VALIDATION',1),('APPLICATION_WITHDRAWN','APPLICATION_WITHDRAWN',0),('APPLICATION_WITHDRAWN_COMPLETED','APPLICATION_WITHDRAWN',0),('APPLICATION_WITHDRAWN_PENDING_CORRECTION','APPLICATION_WITHDRAWN',0),('INSTITUTION_APPROVED','INSTITUTION_APPROVED',0),('PROGRAM_APPROVAL','PROGRAM_APPROVAL',0),('PROGRAM_APPROVAL_PENDING_CORRECTION','PROGRAM_APPROVAL',0),('PROGRAM_APPROVED','PROGRAM_APPROVED',0),('PROGRAM_DEACTIVATED','PROGRAM_APPROVED',0),('PROGRAM_DISABLED','PROGRAM_DISABLED',0),('PROGRAM_DISABLED_COMPLETED','PROGRAM_DISABLED',0),('PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION','PROGRAM_DISABLED',0),('PROGRAM_DISABLED_PENDING_REACTIVATION','PROGRAM_DISABLED',0),('PROGRAM_REJECTED','PROGRAM_REJECTED',0),('PROGRAM_WITHDRAWN','PROGRAM_WITHDRAWN',0),('PROJECT_APPROVED','PROJECT_APPROVED',0),('PROJECT_DEACTIVATED','PROJECT_APPROVED',0),('PROJECT_DISABLED','PROJECT_DISABLED',0),('PROJECT_DISABLED_COMPLETED','PROJECT_DISABLED',0),('PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION','PROJECT_DISABLED',0),('PROJECT_DISABLED_PENDING_REACTIVATION','PROJECT_DISABLED',0),('SYSTEM_APPROVED','SYSTEM_APPROVED',0);
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` (`id`) VALUES ('APPLICATION_ADMINISTRATOR'),('APPLICATION_CREATOR'),('APPLICATION_INTERVIEWEE'),('APPLICATION_INTERVIEWER'),('APPLICATION_POTENTIAL_INTERVIEWEE'),('APPLICATION_POTENTIAL_INTERVIEWER'),('APPLICATION_PRIMARY_SUPERVISOR'),('APPLICATION_REFEREE'),('APPLICATION_REVIEWER'),('APPLICATION_SECONDARY_SUPERVISOR'),('APPLICATION_SUGGESTED_SUPERVISOR'),('APPLICATION_VIEWER_RECRUITER'),('APPLICATION_VIEWER_REFEREE'),('INSTITUTION_ADMINISTRATOR'),('INSTITUTION_ADMITTER'),('INSTITUTION_PROGRAM_CREATOR'),('PROGRAM_ADMINISTRATOR'),('PROGRAM_APPLICATION_CREATOR'),('PROGRAM_APPROVER'),('PROGRAM_PROJECT_CREATOR'),('PROGRAM_VIEWER'),('PROJECT_ADMINISTRATOR'),('PROJECT_APPLICATION_CREATOR'),('PROJECT_PRIMARY_SUPERVISOR'),('PROJECT_SECONDARY_SUPERVISOR'),('SYSTEM_ADMINISTRATOR'),('SYSTEM_APPLICATION_CREATOR'),('SYSTEM_PROGRAM_CREATOR');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role_inheritance`
--

LOCK TABLES `role_inheritance` WRITE;
/*!40000 ALTER TABLE `role_inheritance` DISABLE KEYS */;
INSERT INTO `role_inheritance` (`role_id`, `inherited_role_id`) VALUES ('PROGRAM_ADMINISTRATOR','INSTITUTION_PROGRAM_CREATOR'),('PROGRAM_APPROVER','INSTITUTION_PROGRAM_CREATOR'),('APPLICATION_ADMINISTRATOR','PROGRAM_PROJECT_CREATOR'),('APPLICATION_INTERVIEWER','PROGRAM_PROJECT_CREATOR'),('APPLICATION_PRIMARY_SUPERVISOR','PROGRAM_PROJECT_CREATOR'),('APPLICATION_REVIEWER','PROGRAM_PROJECT_CREATOR'),('APPLICATION_SECONDARY_SUPERVISOR','PROGRAM_PROJECT_CREATOR'),('PROJECT_ADMINISTRATOR','PROGRAM_PROJECT_CREATOR'),('PROJECT_PRIMARY_SUPERVISOR','PROGRAM_PROJECT_CREATOR'),('PROJECT_SECONDARY_SUPERVISOR','PROGRAM_PROJECT_CREATOR'),('APPLICATION_ADMINISTRATOR','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_CREATOR','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_INTERVIEWER','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_PRIMARY_SUPERVISOR','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_REFEREE','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_REVIEWER','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_SECONDARY_SUPERVISOR','SYSTEM_APPLICATION_CREATOR'),('INSTITUTION_ADMINISTRATOR','SYSTEM_APPLICATION_CREATOR'),('INSTITUTION_ADMITTER','SYSTEM_APPLICATION_CREATOR'),('PROGRAM_ADMINISTRATOR','SYSTEM_APPLICATION_CREATOR'),('PROGRAM_APPROVER','SYSTEM_APPLICATION_CREATOR'),('PROGRAM_VIEWER','SYSTEM_APPLICATION_CREATOR'),('PROJECT_ADMINISTRATOR','SYSTEM_APPLICATION_CREATOR'),('PROJECT_PRIMARY_SUPERVISOR','SYSTEM_APPLICATION_CREATOR'),('PROJECT_SECONDARY_SUPERVISOR','SYSTEM_APPLICATION_CREATOR'),('SYSTEM_ADMINISTRATOR','SYSTEM_APPLICATION_CREATOR'),('APPLICATION_ADMINISTRATOR','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_CREATOR','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_INTERVIEWER','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_PRIMARY_SUPERVISOR','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_REFEREE','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_REVIEWER','SYSTEM_PROGRAM_CREATOR'),('APPLICATION_SECONDARY_SUPERVISOR','SYSTEM_PROGRAM_CREATOR'),('INSTITUTION_ADMINISTRATOR','SYSTEM_PROGRAM_CREATOR'),('INSTITUTION_ADMITTER','SYSTEM_PROGRAM_CREATOR'),('PROGRAM_ADMINISTRATOR','SYSTEM_PROGRAM_CREATOR'),('PROGRAM_APPROVER','SYSTEM_PROGRAM_CREATOR'),('PROGRAM_VIEWER','SYSTEM_PROGRAM_CREATOR'),('PROJECT_ADMINISTRATOR','SYSTEM_PROGRAM_CREATOR'),('PROJECT_PRIMARY_SUPERVISOR','SYSTEM_PROGRAM_CREATOR'),('PROJECT_SECONDARY_SUPERVISOR','SYSTEM_PROGRAM_CREATOR'),('SYSTEM_ADMINISTRATOR','SYSTEM_PROGRAM_CREATOR');
/*!40000 ALTER TABLE `role_inheritance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `program_type`
--

LOCK TABLES `program_type` WRITE;
/*!40000 ALTER TABLE `program_type` DISABLE KEYS */;
INSERT INTO `program_type` (`id`, `default_study_duration`) VALUES ('ENGINEERING_DOCTORATE',48),('INTERNSHIP',3),('MRES',12),('MSC',12),('RESEARCH_DEGREE',48),('VISITING_RESEARCH',12);
/*!40000 ALTER TABLE `program_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system`
--

LOCK TABLES `system` WRITE;
/*!40000 ALTER TABLE `system` DISABLE KEYS */;
INSERT INTO `system` (`id`, `name`, `state_id`) VALUES (1,'PRiSM','SYSTEM_APPROVED');
/*!40000 ALTER TABLE `system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `institution_domicile`
--

LOCK TABLES `institution_domicile` WRITE;
/*!40000 ALTER TABLE `institution_domicile` DISABLE KEYS */;
INSERT INTO `institution_domicile` (`id`, `code`, `name`) VALUES (558,'GB','United Kingdom'),(815,'AF','Afghanistan'),(816,'AX','Åland Islands'),(817,'AL','Albania'),(818,'DZ','Algeria'),(819,'AS','American Samoa'),(820,'AD','Andorra'),(821,'AO','Angola'),(822,'AI','Anguilla'),(823,'AQ','Antarctica'),(824,'AG','Antigua and Barbuda'),(825,'AR','Argentina'),(826,'AM','Armenia'),(827,'AW','Aruba'),(828,'AU','Australia'),(829,'AT','Austria'),(830,'AZ','Azerbaijan'),(831,'BS','Bahamas'),(832,'BH','Bahrain'),(833,'BD','Bangladesh'),(834,'BB','Barbados'),(835,'BY','Belarus'),(836,'BE','Belgium'),(837,'BZ','Belize'),(838,'BJ','Benin'),(839,'BM','Bermuda'),(840,'BT','Bhutan'),(841,'BO','Bolivia, Plurinational State of'),(842,'BQ','Bonaire, Sint Eustatius and Saba'),(843,'BA','Bosnia and Herzegovina'),(844,'BW','Botswana'),(845,'BV','Bouvet Island'),(846,'BR','Brazil'),(847,'IO','British Indian Ocean Territory'),(848,'BN','Brunei Darussalam'),(849,'BG','Bulgaria'),(850,'BF','Burkina Faso'),(851,'BI','Burundi'),(852,'KH','Cambodia'),(853,'CM','Cameroon'),(854,'CA','Canada'),(855,'CV','Cabo Verde'),(856,'KY','Cayman Islands'),(857,'CF','Central African Republic'),(858,'TD','Chad'),(859,'CL','Chile'),(860,'CN','China'),(861,'CX','Christmas Island'),(862,'CC','Cocos (Keeling) Islands'),(863,'CO','Colombia'),(864,'KM','Comoros'),(865,'CG','Congo'),(866,'CD','Congo, the Democratic Republic of the'),(867,'CK','Cook Islands'),(868,'CR','Costa Rica'),(869,'CI','Côte d\'Ivoire'),(870,'HR','Croatia'),(871,'CU','Cuba'),(872,'CW','Curaçao'),(873,'CY','Cyprus'),(874,'CZ','Czech Republic'),(875,'DK','Denmark'),(876,'DJ','Djibouti'),(877,'DM','Dominica'),(878,'DO','Dominican Republic'),(879,'EC','Ecuador'),(880,'EG','Egypt'),(881,'SV','El Salvador'),(882,'GQ','Equatorial Guinea'),(883,'ER','Eritrea'),(884,'EE','Estonia'),(885,'ET','Ethiopia'),(886,'FK','Falkland Islands (Malvinas)'),(887,'FO','Faroe Islands'),(888,'FJ','Fiji'),(889,'FI','Finland'),(890,'FR','France'),(891,'GF','French Guiana'),(892,'PF','French Polynesia'),(893,'TF','French Southern Territories'),(894,'GA','Gabon'),(895,'GM','Gambia'),(896,'GE','Georgia'),(897,'DE','Germany'),(898,'GH','Ghana'),(899,'GI','Gibraltar'),(900,'GR','Greece'),(901,'GL','Greenland'),(902,'GD','Grenada'),(903,'GP','Guadeloupe'),(904,'GU','Guam'),(905,'GT','Guatemala'),(906,'GG','Guernsey'),(907,'GN','Guinea'),(908,'GW','Guinea-Bissau'),(909,'GY','Guyana'),(910,'HT','Haiti'),(911,'HM','Heard Island and McDonald Islands'),(912,'VA','Holy See (Vatican City State)'),(913,'HN','Honduras'),(914,'HK','Hong Kong'),(915,'HU','Hungary'),(916,'IS','Iceland'),(917,'IN','India'),(918,'ID','Indonesia'),(919,'IR','Iran, Islamic Republic of'),(920,'IQ','Iraq'),(921,'IE','Ireland'),(922,'IM','Isle of Man'),(923,'IL','Israel'),(924,'IT','Italy'),(925,'JM','Jamaica'),(926,'JP','Japan'),(927,'JE','Jersey'),(928,'JO','Jordan'),(929,'KZ','Kazakhstan'),(930,'KE','Kenya'),(931,'KI','Kiribati'),(932,'KP','Korea, Democratic People\'s Republic of'),(933,'KR','Korea, Republic of'),(934,'KW','Kuwait'),(935,'KG','Kyrgyzstan'),(936,'LA','Lao People\'s Democratic Republic'),(937,'LV','Latvia'),(938,'LB','Lebanon'),(939,'LS','Lesotho'),(940,'LR','Liberia'),(941,'LY','Libya'),(942,'LI','Liechtenstein'),(943,'LT','Lithuania'),(944,'LU','Luxembourg'),(945,'MO','Macao'),(946,'MK','Macedonia, the former Yugoslav Republic of'),(947,'MG','Madagascar'),(948,'MW','Malawi'),(949,'MY','Malaysia'),(950,'MV','Maldives'),(951,'ML','Mali'),(952,'MT','Malta'),(953,'MH','Marshall Islands'),(954,'MQ','Martinique'),(955,'MR','Mauritania'),(956,'MU','Mauritius'),(957,'YT','Mayotte'),(958,'MX','Mexico'),(959,'FM','Micronesia, Federated States of'),(960,'MD','Moldova, Republic of'),(961,'MC','Monaco'),(962,'MN','Mongolia'),(963,'ME','Montenegro'),(964,'MS','Montserrat'),(965,'MA','Morocco'),(966,'MZ','Mozambique'),(967,'MM','Myanmar'),(968,'NA','Namibia'),(969,'NR','Nauru'),(970,'NP','Nepal'),(971,'NL','Netherlands'),(972,'NC','New Caledonia'),(973,'NZ','New Zealand'),(974,'NI','Nicaragua'),(975,'NE','Niger'),(976,'NG','Nigeria'),(977,'NU','Niue'),(978,'NF','Norfolk Island'),(979,'MP','Northern Mariana Islands'),(980,'NO','Norway'),(981,'OM','Oman'),(982,'PK','Pakistan'),(983,'PW','Palau'),(984,'PS','Palestine, State of'),(985,'PA','Panama'),(986,'PG','Papua New Guinea'),(987,'PY','Paraguay'),(988,'PE','Peru'),(989,'PH','Philippines'),(990,'PN','Pitcairn'),(991,'PL','Poland'),(992,'PT','Portugal'),(993,'PR','Puerto Rico'),(994,'QA','Qatar'),(995,'RE','Réunion'),(996,'RO','Romania'),(997,'RU','Russian Federation'),(998,'RW','Rwanda'),(999,'BL','Saint Barthélemy'),(1000,'SH','Saint Helena, Ascension and Tristan da Cunha'),(1001,'KN','Saint Kitts and Nevis'),(1002,'LC','Saint Lucia'),(1003,'MF','Saint Martin (French part)'),(1004,'PM','Saint Pierre and Miquelon'),(1005,'VC','Saint Vincent and the Grenadines'),(1006,'WS','Samoa'),(1007,'SM','San Marino'),(1008,'ST','Sao Tome and Principe'),(1009,'SA','Saudi Arabia'),(1010,'SN','Senegal'),(1011,'RS','Serbia'),(1012,'SC','Seychelles'),(1013,'SL','Sierra Leone'),(1014,'SG','Singapore'),(1015,'SX','Sint Maarten (Dutch part)'),(1016,'SK','Slovakia'),(1017,'SI','Slovenia'),(1018,'SB','Solomon Islands'),(1019,'SO','Somalia'),(1020,'ZA','South Africa'),(1021,'GS','South Georgia and the South Sandwich Islands'),(1022,'SS','South Sudan'),(1023,'ES','Spain'),(1024,'LK','Sri Lanka'),(1025,'SD','Sudan'),(1026,'SR','Suriname'),(1027,'SJ','Svalbard and Jan Mayen'),(1028,'SZ','Swaziland'),(1029,'SE','Sweden'),(1030,'CH','Switzerland'),(1031,'SY','Syrian Arab Republic'),(1032,'TW','Taiwan, Province of China'),(1033,'TJ','Tajikistan'),(1034,'TZ','Tanzania, United Republic of'),(1035,'TH','Thailand'),(1036,'TL','Timor-Leste'),(1037,'TG','Togo'),(1038,'TK','Tokelau'),(1039,'TO','Tonga'),(1040,'TT','Trinidad and Tobago'),(1041,'TN','Tunisia'),(1042,'TR','Turkey'),(1043,'TM','Turkmenistan'),(1044,'TC','Turks and Caicos Islands'),(1045,'TV','Tuvalu'),(1046,'UG','Uganda'),(1047,'UA','Ukraine'),(1048,'AE','United Arab Emirates'),(1050,'US','United States'),(1051,'UM','United States Minor Outlying Islands'),(1052,'UY','Uruguay'),(1053,'UZ','Uzbekistan'),(1054,'VU','Vanuatu'),(1055,'VE','Venezuela, Bolivarian Republic of'),(1056,'VN','Viet Nam'),(1057,'VG','Virgin Islands, British'),(1058,'VI','Virgin Islands, U.S.'),(1059,'WF','Wallis and Futuna'),(1060,'EH','Western Sahara'),(1061,'YE','Yemen'),(1062,'ZM','Zambia'),(1063,'ZW','Zimbabwe');
/*!40000 ALTER TABLE `institution_domicile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `institution`
--

LOCK TABLES `institution` WRITE;
/*!40000 ALTER TABLE `institution` DISABLE KEYS */;
INSERT INTO `institution` (`id`, `institution_domicile_id`, `name`, `system_id`, `state_id`) VALUES (5243,558,'University College London',1,'INSTITUTION_APPROVED');
/*!40000 ALTER TABLE `institution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `imported_entity_feed`
--

LOCK TABLES `imported_entity_feed` WRITE;
/*!40000 ALTER TABLE `imported_entity_feed` DISABLE KEYS */;
INSERT INTO `imported_entity_feed` (`id`, `institution_id`, `imported_entity_type_id`, `username`, `password`, `location`) VALUES (1,5243,'PROGRAM','prism','tCP5++Vm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/prism/prismProgrammes.xml'),(2,5243,'COUNTRY','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/countriesOfBirth.xml'),(3,5243,'DOMICILE','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/countriesOfDomicile.xml'),(4,5243,'DISABILITY','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/disabilities.xml'),(5,5243,'ETHNICITY','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/ethnicities.xml'),(6,5243,'NATIONALITY','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/nationalities.xml'),(7,5243,'QUALIFICATION_TYPE','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/qualifications.xml'),(8,5243,'REFERRAL_SOURCE','reference','wiI2+sZm','https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/sourcesOfInterest.xml');
/*!40000 ALTER TABLE `imported_entity_feed` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-05-15 10:53:01
