-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.26-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table prism.action
CREATE TABLE IF NOT EXISTS `action` (
  `id` varchar(100) NOT NULL,
  `system_invocation_only` int(1) unsigned NOT NULL,
  `action_category` varchar(50) NOT NULL,
  `rating_action` int(1) unsigned NOT NULL,
  `transition_action` int(1) unsigned DEFAULT NULL,
  `declinable_action` int(1) unsigned NOT NULL,
  `visible_action` int(1) unsigned NOT NULL,
  `fallback_action_id` varchar(100) DEFAULT NULL,
  `partnership_state` varchar(50) DEFAULT NULL,
  `partnership_transition_state` varchar(50) DEFAULT NULL,
  `scope_id` varchar(50) NOT NULL,
  `creation_scope_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `scope_id` (`scope_id`),
  KEY `creation_scope_id` (`creation_scope_id`),
  KEY `fallback_action_id` (`fallback_action_id`),
  KEY `visible_action` (`visible_action`),
  KEY `system_invocation_only` (`system_invocation_only`),
  CONSTRAINT `action_ibfk_3` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`),
  CONSTRAINT `action_ibfk_4` FOREIGN KEY (`creation_scope_id`) REFERENCES `scope` (`id`),
  CONSTRAINT `action_ibfk_5` FOREIGN KEY (`fallback_action_id`) REFERENCES `action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.action_redaction
CREATE TABLE IF NOT EXISTS `action_redaction` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `action_id` varchar(100) NOT NULL,
  `role_id` varchar(50) NOT NULL,
  `redaction_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `action_id` (`action_id`,`role_id`),
  KEY `role_id` (`role_id`),
  KEY `action_visibility_exclusion_rule_id` (`redaction_type`),
  CONSTRAINT `action_redaction_ibfk_1` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `action_redaction_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.address
CREATE TABLE IF NOT EXISTS `address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imported_domicile_id` varchar(10) NOT NULL,
  `address_line_1` varchar(255) NOT NULL,
  `address_line_2` varchar(255) DEFAULT NULL,
  `address_town` varchar(255) NOT NULL,
  `address_region` varchar(255) DEFAULT NULL,
  `address_code` varchar(255) DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  `location_x` decimal(18,14) DEFAULT NULL,
  `location_y` decimal(18,14) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `domicile_address_fk` (`imported_domicile_id`),
  KEY `location_x` (`location_x`),
  KEY `location_y` (`location_y`),
  KEY `google_id` (`google_id`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`imported_domicile_id`) REFERENCES `imported_domicile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert
CREATE TABLE IF NOT EXISTS `advert` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `imported_opportunity_type_id` int(10) unsigned DEFAULT NULL,
  `opportunity_category` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `summary` text,
  `description` text,
  `background_image_id` int(10) unsigned DEFAULT NULL,
  `homepage` varchar(2000) DEFAULT NULL,
  `apply_homepage` varchar(2000) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `advert_address_id` int(10) unsigned DEFAULT NULL,
  `pay_interval` varchar(10) DEFAULT NULL,
  `pay_currency_specified` varchar(10) DEFAULT NULL,
  `pay_currency_at_locale` varchar(10) DEFAULT NULL,
  `month_pay_minimum_specified` decimal(10,2) unsigned DEFAULT NULL,
  `month_pay_maximum_specified` decimal(10,2) unsigned DEFAULT NULL,
  `year_pay_minimum_specified` decimal(10,2) unsigned DEFAULT NULL,
  `year_pay_maximum_specified` decimal(10,2) unsigned DEFAULT NULL,
  `month_pay_minimum_at_locale` decimal(10,2) unsigned DEFAULT NULL,
  `month_pay_maximum_at_locale` decimal(10,2) unsigned DEFAULT NULL,
  `year_pay_minimum_at_locale` decimal(10,2) unsigned DEFAULT NULL,
  `year_pay_maximum_at_locale` decimal(10,2) unsigned DEFAULT NULL,
  `pay_converted` int(1) unsigned DEFAULT NULL,
  `advert_closing_date_id` int(10) unsigned DEFAULT NULL,
  `last_currency_conversion_date` date DEFAULT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  UNIQUE KEY `institution_id` (`institution_id`,`department_id`,`program_id`,`project_id`),
  KEY `title` (`name`,`sequence_identifier`),
  KEY `institution_address_id` (`advert_address_id`,`sequence_identifier`),
  KEY `month_pay_minimum_specified` (`month_pay_minimum_specified`,`sequence_identifier`),
  KEY `month_pay_maximum_specified` (`month_pay_maximum_specified`,`sequence_identifier`),
  KEY `year_pay_minimum_specified` (`year_pay_minimum_specified`,`sequence_identifier`),
  KEY `year_pay_maximum_specified` (`year_pay_maximum_specified`,`sequence_identifier`),
  KEY `month_pay_minimum_at_locale` (`month_pay_minimum_at_locale`,`sequence_identifier`),
  KEY `month_pay_maximum_at_locale` (`month_pay_maximum_at_locale`,`sequence_identifier`),
  KEY `year_pay_minimum_at_locale` (`year_pay_minimum_at_locale`,`sequence_identifier`),
  KEY `year_pay_maximum_at_locale` (`year_pay_maximum_at_locale`,`sequence_identifier`),
  KEY `advert_closing_date_id` (`advert_closing_date_id`,`sequence_identifier`),
  KEY `fee_currency` (`sequence_identifier`),
  KEY `pay_currency` (`pay_currency_specified`,`sequence_identifier`),
  KEY `pay_currency_at_locale` (`pay_currency_at_locale`,`sequence_identifier`),
  KEY `last_currency_conversion_date` (`last_currency_conversion_date`),
  KEY `background_image_id` (`background_image_id`),
  KEY `institution_id_2` (`institution_id`,`sequence_identifier`),
  KEY `department_id` (`department_id`,`sequence_identifier`),
  KEY `program_id` (`program_id`,`sequence_identifier`),
  KEY `project_id` (`project_id`,`sequence_identifier`),
  KEY `system_id` (`system_id`,`sequence_identifier`),
  KEY `imported_opportunity_type_id` (`imported_opportunity_type_id`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  CONSTRAINT `advert_ibfk_10` FOREIGN KEY (`imported_opportunity_type_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `advert_ibfk_11` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `advert_ibfk_2` FOREIGN KEY (`advert_closing_date_id`) REFERENCES `advert_closing_date` (`id`),
  CONSTRAINT `advert_ibfk_3` FOREIGN KEY (`advert_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `advert_ibfk_4` FOREIGN KEY (`background_image_id`) REFERENCES `document` (`id`),
  CONSTRAINT `advert_ibfk_5` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `advert_ibfk_6` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `advert_ibfk_7` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `advert_ibfk_8` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `advert_ibfk_9` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert_closing_date
CREATE TABLE IF NOT EXISTS `advert_closing_date` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_id` int(10) unsigned NOT NULL,
  `closing_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `advert_id` (`advert_id`,`closing_date`),
  KEY `closing_date` (`closing_date`),
  CONSTRAINT `advert_closing_date_ibfk_2` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert_competence
CREATE TABLE IF NOT EXISTS `advert_competence` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_id` int(10) unsigned NOT NULL,
  `competence_id` int(10) unsigned NOT NULL,
  `description` text,
  `importance` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `advert_id` (`advert_id`,`competence_id`),
  KEY `competence_id` (`competence_id`),
  CONSTRAINT `advert_competence_ibfk_1` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `advert_competence_ibfk_2` FOREIGN KEY (`competence_id`) REFERENCES `competence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert_function
CREATE TABLE IF NOT EXISTS `advert_function` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_id` int(10) unsigned NOT NULL,
  `function` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `function` (`function`,`advert_id`),
  UNIQUE KEY `advert_id` (`advert_id`,`function`),
  CONSTRAINT `advert_function_ibfk_1` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert_industry
CREATE TABLE IF NOT EXISTS `advert_industry` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_id` int(10) unsigned NOT NULL,
  `industry` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `industry` (`industry`,`advert_id`),
  UNIQUE KEY `advert_id` (`advert_id`,`industry`),
  CONSTRAINT `advert_industry_ibfk_1` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.advert_target_advert
CREATE TABLE IF NOT EXISTS `advert_target_advert` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_id` int(10) unsigned NOT NULL,
  `target_advert_id` int(10) unsigned NOT NULL,
  `target_advert_user_id` int(10) unsigned DEFAULT NULL,
  `selected` int(10) unsigned NOT NULL,
  `partnership_state` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `advert_id` (`advert_id`,`target_advert_id`),
  KEY `target_advert_id` (`target_advert_id`),
  KEY `target_advert_user_id` (`target_advert_user_id`),
  CONSTRAINT `advert_target_advert_ibfk_1` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `advert_target_advert_ibfk_4` FOREIGN KEY (`target_advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `advert_target_advert_ibfk_5` FOREIGN KEY (`target_advert_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application
CREATE TABLE IF NOT EXISTS `application` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `code_legacy` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned NOT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `opportunity_category` varchar(50) NOT NULL,
  `closing_date` date DEFAULT NULL,
  `application_program_detail_id` int(10) unsigned DEFAULT NULL,
  `application_personal_detail_id` int(10) unsigned DEFAULT NULL,
  `application_address_id` int(10) unsigned DEFAULT NULL,
  `application_document_id` int(10) unsigned DEFAULT NULL,
  `application_additional_information_id` int(10) unsigned DEFAULT NULL,
  `primary_theme` text,
  `secondary_theme` text,
  `application_rating_count` int(10) unsigned DEFAULT NULL,
  `application_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `application_reserve_status` varchar(50) DEFAULT NULL,
  `completion_date` date DEFAULT NULL,
  `confirmed_start_date` date DEFAULT NULL,
  `confirmed_offer_type` varchar(50) DEFAULT NULL,
  `retain` int(1) unsigned DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `submitted_timestamp` datetime DEFAULT NULL,
  `application_year` varchar(10) DEFAULT NULL,
  `application_month` int(2) unsigned DEFAULT NULL,
  `application_month_sequence` date DEFAULT NULL,
  `application_week` int(2) unsigned DEFAULT NULL,
  `application_week_sequence` date DEFAULT NULL,
  `updated_timestamp` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_personal_detail_id` (`application_personal_detail_id`),
  UNIQUE KEY `application_program_detail_id` (`application_program_detail_id`),
  UNIQUE KEY `application_address_id` (`application_address_id`),
  UNIQUE KEY `application_document_id` (`application_document_id`),
  UNIQUE KEY `application_additional_information_id` (`application_additional_information_id`),
  UNIQUE KEY `code_2` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  KEY `due_date` (`due_date`),
  KEY `system_id` (`system_id`),
  KEY `do_retain` (`retain`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  KEY `program_id` (`program_id`,`sequence_identifier`),
  KEY `project_id` (`project_id`,`sequence_identifier`),
  KEY `closing_date` (`closing_date`,`sequence_identifier`),
  KEY `state_id` (`state_id`,`sequence_identifier`),
  KEY `rating_count` (`application_rating_count`,`sequence_identifier`),
  KEY `average_rating` (`application_rating_average`,`sequence_identifier`),
  KEY `institution_id` (`institution_id`,`sequence_identifier`),
  KEY `created_timestamp` (`created_timestamp`,`sequence_identifier`),
  KEY `submitted_timestamp` (`submitted_timestamp`,`sequence_identifier`),
  KEY `updated_timestamp` (`updated_timestamp`,`sequence_identifier`),
  KEY `code` (`code`,`sequence_identifier`),
  KEY `previous_state_id` (`previous_state_id`,`sequence_identifier`),
  KEY `previous_closing_date` (`sequence_identifier`),
  KEY `confirmed_start_date` (`confirmed_start_date`,`sequence_identifier`),
  KEY `confirmed_offer_type` (`confirmed_offer_type`,`sequence_identifier`),
  KEY `institution_id_2` (`institution_id`,`application_rating_count`),
  KEY `institution_id_3` (`institution_id`,`application_rating_average`),
  KEY `program_id_2` (`program_id`,`application_rating_count`),
  KEY `program_id_3` (`program_id`,`application_rating_average`),
  KEY `project_id_2` (`project_id`,`application_rating_count`),
  KEY `project_id_3` (`project_id`,`application_rating_average`),
  KEY `completion_date` (`completion_date`,`sequence_identifier`),
  KEY `code_legacy` (`code_legacy`,`sequence_identifier`),
  KEY `advert_id` (`advert_id`,`sequence_identifier`),
  KEY `application_reserve_rating` (`application_reserve_status`,`sequence_identifier`),
  KEY `institution_id_4` (`institution_id`,`application_year`,`application_month_sequence`),
  KEY `program_id_4` (`program_id`,`application_year`,`application_month_sequence`),
  KEY `project_id_4` (`project_id`,`application_year`,`application_month_sequence`),
  KEY `institution_id_5` (`institution_id`,`application_year`,`application_month_sequence`,`application_week_sequence`),
  KEY `program_id_5` (`program_id`,`application_year`,`application_month_sequence`,`application_week_sequence`),
  KEY `project_id_5` (`project_id`,`application_year`,`application_month_sequence`,`application_week_sequence`),
  KEY `department_id` (`department_id`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  CONSTRAINT `APPLICATION_FORM_ibfk_1` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `application_form_additional_info_fk` FOREIGN KEY (`application_additional_information_id`) REFERENCES `application_additional_information` (`id`),
  CONSTRAINT `application_form_address_fk` FOREIGN KEY (`application_address_id`) REFERENCES `application_address` (`id`),
  CONSTRAINT `application_form_document_fk` FOREIGN KEY (`application_document_id`) REFERENCES `application_document` (`id`),
  CONSTRAINT `application_form_personal_detail_fk` FOREIGN KEY (`application_personal_detail_id`) REFERENCES `application_personal_detail` (`id`),
  CONSTRAINT `application_form_programme_details_fk` FOREIGN KEY (`application_program_detail_id`) REFERENCES `application_program_detail` (`id`),
  CONSTRAINT `application_ibfk_2` FOREIGN KEY (`previous_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `application_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `application_ibfk_4` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `application_ibfk_5` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `application_ibfk_6` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `application_ibfk_8` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_application_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_additional_information
CREATE TABLE IF NOT EXISTS `application_additional_information` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `convictions_text` varchar(400) DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_address
CREATE TABLE IF NOT EXISTS `application_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `current_address_id` int(10) unsigned NOT NULL,
  `contact_address_id` int(10) unsigned NOT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `current_address_id` (`current_address_id`),
  KEY `contact_address_id` (`contact_address_id`),
  CONSTRAINT `application_address_ibfk_3` FOREIGN KEY (`current_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `application_address_ibfk_4` FOREIGN KEY (`contact_address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_document
CREATE TABLE IF NOT EXISTS `application_document` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `personal_summary` varchar(1000) DEFAULT NULL,
  `cv_id` int(10) unsigned DEFAULT NULL,
  `covering_letter_id` int(10) unsigned DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cv_id` (`cv_id`),
  UNIQUE KEY `covering_letter_id` (`covering_letter_id`),
  CONSTRAINT `application_document_ibfk_1` FOREIGN KEY (`cv_id`) REFERENCES `document` (`id`),
  CONSTRAINT `application_document_ibfk_3` FOREIGN KEY (`covering_letter_id`) REFERENCES `document` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_employment_position
CREATE TABLE IF NOT EXISTS `application_employment_position` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned DEFAULT NULL,
  `start_year` int(4) unsigned NOT NULL,
  `start_month` int(2) unsigned NOT NULL,
  `end_year` int(4) unsigned DEFAULT NULL,
  `end_month` int(2) unsigned DEFAULT NULL,
  `current` int(1) unsigned NOT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_id` (`application_id`,`advert_id`,`start_year`),
  KEY `advert_id` (`advert_id`),
  CONSTRAINT `application_employment_position_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `application_employment_position_ibfk_3` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_personal_detail
CREATE TABLE IF NOT EXISTS `application_personal_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imported_title_id` int(10) unsigned DEFAULT NULL,
  `imported_gender_id` int(10) unsigned DEFAULT NULL,
  `imported_age_range_id` int(10) unsigned DEFAULT NULL,
  `imported_nationality_id` varchar(10) DEFAULT NULL,
  `imported_domicile_id` varchar(10) DEFAULT NULL,
  `visa_required` int(1) unsigned DEFAULT NULL,
  `phone` varchar(50) NOT NULL,
  `skype` varchar(50) DEFAULT NULL,
  `imported_ethnicity_id` int(10) unsigned DEFAULT NULL,
  `imported_disability_id` int(10) unsigned DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_ethnicity_fk` (`imported_ethnicity_id`),
  KEY `user_disability_fk` (`imported_disability_id`),
  KEY `title_id` (`imported_title_id`),
  KEY `gender_id` (`imported_gender_id`),
  KEY `age_range_id` (`imported_age_range_id`),
  KEY `imported_nationality_id` (`imported_nationality_id`),
  KEY `imported_domicile_id` (`imported_domicile_id`),
  CONSTRAINT `application_personal_detail_ibfk_11` FOREIGN KEY (`imported_age_range_id`) REFERENCES `imported_age_range` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_12` FOREIGN KEY (`imported_gender_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_14` FOREIGN KEY (`imported_disability_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_15` FOREIGN KEY (`imported_ethnicity_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_19` FOREIGN KEY (`imported_title_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_20` FOREIGN KEY (`imported_nationality_id`) REFERENCES `imported_domicile` (`id`),
  CONSTRAINT `application_personal_detail_ibfk_21` FOREIGN KEY (`imported_domicile_id`) REFERENCES `imported_domicile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_program_detail
CREATE TABLE IF NOT EXISTS `application_program_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imported_opportunity_type_id` int(10) unsigned DEFAULT NULL,
  `imported_study_option_id` int(10) unsigned DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_option_id_2` (`imported_study_option_id`),
  KEY `opportunity_type_id` (`imported_opportunity_type_id`),
  CONSTRAINT `application_program_detail_ibfk_6` FOREIGN KEY (`imported_study_option_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `application_program_detail_ibfk_7` FOREIGN KEY (`imported_opportunity_type_id`) REFERENCES `imported_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_qualification
CREATE TABLE IF NOT EXISTS `application_qualification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `start_year` int(4) unsigned NOT NULL,
  `start_month` int(2) unsigned NOT NULL,
  `award_year` int(4) unsigned DEFAULT NULL,
  `award_month` int(2) unsigned DEFAULT NULL,
  `grade` varchar(200) NOT NULL,
  `completed` varchar(10) NOT NULL,
  `document_id` int(10) unsigned DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_id` (`application_id`,`advert_id`,`start_year`),
  UNIQUE KEY `document_id` (`document_id`),
  KEY `advert_id` (`advert_id`),
  CONSTRAINT `application_qualification_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `application_qualification_ibfk_2` FOREIGN KEY (`document_id`) REFERENCES `document` (`id`),
  CONSTRAINT `application_qualification_ibfk_3` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.application_referee
CREATE TABLE IF NOT EXISTS `application_referee` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `skype` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `phone` varchar(50) NOT NULL,
  `comment_id` int(10) unsigned DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_id` (`application_id`,`user_id`),
  KEY `referee_user_fk` (`user_id`),
  KEY `comment_id` (`comment_id`),
  KEY `advert_id` (`advert_id`),
  CONSTRAINT `application_form_referee_fk` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `application_referee_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `application_referee_ibfk_2` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `fk_application_form_referee_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.changelog
CREATE TABLE IF NOT EXISTS `changelog` (
  `change_number` bigint(20) NOT NULL,
  `complete_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `applied_by` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  PRIMARY KEY (`change_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment
CREATE TABLE IF NOT EXISTS `comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `delegate_user_id` int(10) unsigned DEFAULT NULL,
  `action_id` varchar(100) NOT NULL,
  `declined_response` int(1) unsigned NOT NULL,
  `content` text,
  `state_id` varchar(100) DEFAULT NULL,
  `transition_state_id` varchar(100) DEFAULT NULL,
  `rating` decimal(3,2) unsigned DEFAULT NULL,
  `application_identified` int(1) unsigned DEFAULT NULL,
  `application_eligible` varchar(10) DEFAULT NULL,
  `application_interested` int(1) unsigned DEFAULT NULL,
  `application_interview_datetime` datetime DEFAULT NULL,
  `application_interview_timezone` varchar(50) DEFAULT NULL,
  `application_interview_duration` int(10) unsigned DEFAULT NULL,
  `application_interviewee_instructions` text,
  `application_interviewer_instructions` text,
  `application_interview_location` varchar(2000) DEFAULT NULL,
  `application_position_title` varchar(255) DEFAULT NULL,
  `application_position_description` varchar(2000) DEFAULT NULL,
  `application_position_provisional_start_date` date DEFAULT NULL,
  `application_appointment_conditions` text,
  `application_recruiter_accept_appointment` int(1) unsigned DEFAULT NULL,
  `application_reserve_status` varchar(50) DEFAULT NULL,
  `application_imported_rejection_reason_id` int(10) unsigned DEFAULT NULL,
  `application_rejection_reason_system` varchar(255) DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `application_id_fk` (`application_id`),
  KEY `review_user_fk` (`user_id`),
  KEY `program_id` (`program_id`),
  KEY `project_id` (`project_id`),
  KEY `action_id` (`action_id`),
  KEY `delegate_user_id` (`delegate_user_id`),
  KEY `transition_state_id` (`transition_state_id`),
  KEY `system_id` (`system_id`),
  KEY `institution_id` (`institution_id`),
  KEY `application_rejection_reason_id` (`application_imported_rejection_reason_id`),
  KEY `state_id` (`state_id`),
  KEY `application_id` (`application_id`,`application_interested`),
  KEY `application_id_2` (`application_id`,`application_reserve_status`),
  KEY `department_id` (`department_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `comment_ibfk_11` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `comment_ibfk_12` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `comment_ibfk_15` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `comment_ibfk_18` FOREIGN KEY (`application_imported_rejection_reason_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `comment_ibfk_19` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `comment_ibfk_4` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `comment_ibfk_6` FOREIGN KEY (`delegate_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_ibfk_8` FOREIGN KEY (`transition_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `fk_comment_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_appointment_preference
CREATE TABLE IF NOT EXISTS `comment_appointment_preference` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `preference_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`preference_datetime`),
  CONSTRAINT `comment_appointment_preference_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_appointment_timeslot
CREATE TABLE IF NOT EXISTS `comment_appointment_timeslot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `timeslot_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`timeslot_datetime`),
  CONSTRAINT `comment_appointment_timeslot_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_assigned_user
CREATE TABLE IF NOT EXISTS `comment_assigned_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `role_id` varchar(50) NOT NULL,
  `role_transition_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`user_id`,`role_id`),
  KEY `user_id` (`user_id`),
  KEY `role_id` (`role_id`),
  KEY `role_transition_type` (`role_transition_type`),
  CONSTRAINT `comment_assigned_user_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `comment_assigned_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_assigned_user_ibfk_3` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_competence
CREATE TABLE IF NOT EXISTS `comment_competence` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `competence_id` int(10) unsigned NOT NULL,
  `rating` int(1) unsigned NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`competence_id`),
  KEY `competence_id` (`competence_id`),
  CONSTRAINT `comment_competence_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `comment_competence_ibfk_2` FOREIGN KEY (`competence_id`) REFERENCES `competence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_state
CREATE TABLE IF NOT EXISTS `comment_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `state_id` varchar(100) NOT NULL,
  `primary_state` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`state_id`),
  KEY `state_id` (`state_id`),
  CONSTRAINT `comment_state_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `comment_state_ibfk_2` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.comment_transition_state
CREATE TABLE IF NOT EXISTS `comment_transition_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` int(10) unsigned NOT NULL,
  `state_id` varchar(100) NOT NULL,
  `primary_state` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`,`state_id`),
  KEY `primary_state` (`comment_id`,`primary_state`),
  KEY `state_id` (`state_id`),
  CONSTRAINT `comment_transition_state_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `comment_transition_state_ibfk_2` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.competence
CREATE TABLE IF NOT EXISTS `competence` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `adopted_count` int(10) unsigned NOT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `title` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.department
CREATE TABLE IF NOT EXISTS `department` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `imported_code` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned NOT NULL,
  `institution_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned DEFAULT NULL,
  `opportunity_category` varchar(255) DEFAULT NULL,
  `advert_incomplete_section` text,
  `name` varchar(255) NOT NULL,
  `application_rating_count` int(10) unsigned DEFAULT NULL,
  `application_rating_frequency` decimal(10,2) unsigned DEFAULT NULL,
  `application_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `opportunity_rating_count` int(10) unsigned DEFAULT NULL,
  `opportunity_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `updated_timestamp_sitemap` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `institution_id` (`institution_id`,`name`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  KEY `institution_id_2` (`institution_id`,`sequence_identifier`),
  KEY `title` (`name`,`sequence_identifier`),
  KEY `advert_id` (`advert_id`,`sequence_identifier`),
  KEY `application_rating_count` (`application_rating_count`,`sequence_identifier`),
  KEY `application_rating_frequency` (`application_rating_frequency`,`sequence_identifier`),
  KEY `application_rating_average` (`application_rating_average`,`sequence_identifier`),
  KEY `state_id` (`state_id`,`sequence_identifier`),
  KEY `previous_state_id` (`previous_state_id`,`sequence_identifier`),
  KEY `due_date` (`due_date`),
  KEY `created_timestamp` (`created_timestamp`,`sequence_identifier`),
  KEY `updated_timestamp` (`updated_timestamp`,`sequence_identifier`),
  KEY `updated_timestamp_sitemap` (`updated_timestamp_sitemap`),
  KEY `system_id` (`system_id`,`sequence_identifier`),
  KEY `opportunity_rating_count` (`opportunity_rating_count`,`sequence_identifier`),
  KEY `opportunity_rating_average` (`opportunity_rating_average`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  CONSTRAINT `department_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `department_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `department_ibfk_3` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `department_ibfk_4` FOREIGN KEY (`previous_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `department_ibfk_5` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.display_property_configuration
CREATE TABLE IF NOT EXISTS `display_property_configuration` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `opportunity_type` varchar(50) DEFAULT NULL,
  `display_property_definition_id` varchar(100) NOT NULL,
  `value` text NOT NULL,
  `system_default` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`opportunity_type`,`display_property_definition_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`opportunity_type`,`display_property_definition_id`),
  UNIQUE KEY `program_id` (`program_id`,`display_property_definition_id`),
  UNIQUE KEY `project_id` (`project_id`,`display_property_definition_id`),
  UNIQUE KEY `department_id` (`department_id`,`opportunity_type`,`display_property_definition_id`),
  KEY `display_property_id` (`display_property_definition_id`),
  KEY `system_default` (`system_id`,`system_default`),
  CONSTRAINT `display_property_configuration_ibfk_2` FOREIGN KEY (`display_property_definition_id`) REFERENCES `display_property_definition` (`id`),
  CONSTRAINT `display_property_configuration_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `display_property_configuration_ibfk_4` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.display_property_definition
CREATE TABLE IF NOT EXISTS `display_property_definition` (
  `id` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `scope_id` (`scope_id`),
  KEY `scope_id_2` (`scope_id`,`category`),
  CONSTRAINT `display_property_definition_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.document
CREATE TABLE IF NOT EXISTS `document` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(500) NOT NULL,
  `file_content` longblob,
  `content_type` varchar(200) NOT NULL,
  `exported` int(1) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `comment_id` int(10) unsigned DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `category` varchar(8) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `document_comment_fk` (`comment_id`),
  KEY `uploaded_time_stamp` (`created_timestamp`),
  KEY `user_id` (`user_id`),
  KEY `exported` (`exported`),
  CONSTRAINT `document_comment_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `document_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.imported_age_range
CREATE TABLE IF NOT EXISTS `imported_age_range` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `lower_bound` int(3) unsigned NOT NULL,
  `upper_bound` int(3) unsigned DEFAULT NULL,
  `enabled` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `lower_bound` (`lower_bound`),
  UNIQUE KEY `upper_bound` (`upper_bound`),
  KEY `enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.imported_domicile
CREATE TABLE IF NOT EXISTS `imported_domicile` (
  `id` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `enabled` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `currency` (`currency`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.imported_entity
CREATE TABLE IF NOT EXISTS `imported_entity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imported_entity_type` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `enabled` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `imported_entity_type` (`imported_entity_type`,`name`),
  KEY `enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.imported_entity_type
CREATE TABLE IF NOT EXISTS `imported_entity_type` (
  `id` varchar(50) NOT NULL,
  `last_imported_timestamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.institution
CREATE TABLE IF NOT EXISTS `institution` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `opportunity_category` varchar(255) DEFAULT NULL,
  `advert_incomplete_section` text,
  `name` varchar(255) NOT NULL,
  `logo_image_id` int(10) unsigned DEFAULT NULL,
  `currency` varchar(10) NOT NULL,
  `business_year_start_month` int(2) unsigned NOT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  `ucl_institution` int(1) unsigned NOT NULL,
  `application_rating_count` int(10) unsigned DEFAULT NULL,
  `application_rating_frequency` decimal(10,2) unsigned DEFAULT NULL,
  `application_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `opportunity_rating_count` int(10) unsigned DEFAULT NULL,
  `opportunity_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `updated_timestamp_sitemap` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `institution_domicile_id` (`user_id`,`name`),
  UNIQUE KEY `code_2` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  KEY `system_id` (`system_id`),
  KEY `is_ucl_institution` (`ucl_institution`),
  KEY `title` (`name`,`sequence_identifier`),
  KEY `created_timestamp` (`created_timestamp`,`sequence_identifier`),
  KEY `updated_timestamp` (`updated_timestamp`,`sequence_identifier`),
  KEY `currency` (`currency`),
  KEY `state_id` (`state_id`,`sequence_identifier`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  KEY `code` (`code`,`sequence_identifier`),
  KEY `due_date` (`due_date`),
  KEY `google_identifier` (`google_id`),
  KEY `updated_timestamp_sitemap` (`updated_timestamp_sitemap`),
  KEY `advert_id` (`advert_id`,`sequence_identifier`),
  KEY `logo_image_id` (`logo_image_id`),
  KEY `application_rating_count` (`application_rating_count`,`sequence_identifier`),
  KEY `application_rating_frequency` (`application_rating_frequency`,`sequence_identifier`),
  KEY `application_rating_average` (`application_rating_average`,`sequence_identifier`),
  KEY `opportunity_rating_count` (`opportunity_rating_count`,`sequence_identifier`),
  KEY `opportunity_rating_average` (`opportunity_rating_average`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  CONSTRAINT `institution_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `institution_ibfk_10` FOREIGN KEY (`logo_image_id`) REFERENCES `document` (`id`),
  CONSTRAINT `institution_ibfk_2` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `institution_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `institution_ibfk_9` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.notification_configuration
CREATE TABLE IF NOT EXISTS `notification_configuration` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `opportunity_type` varchar(50) DEFAULT NULL,
  `notification_definition_id` varchar(100) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `system_default` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`opportunity_type`,`notification_definition_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`opportunity_type`,`notification_definition_id`),
  UNIQUE KEY `program_id` (`program_id`,`notification_definition_id`),
  UNIQUE KEY `project_id` (`project_id`,`notification_definition_id`),
  UNIQUE KEY `department_id` (`department_id`,`opportunity_type`,`notification_definition_id`),
  KEY `notification_template_id` (`notification_definition_id`),
  KEY `system_default` (`system_id`,`system_default`),
  CONSTRAINT `notification_configuration_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `notification_configuration_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `notification_configuration_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `notification_configuration_ibfk_5` FOREIGN KEY (`notification_definition_id`) REFERENCES `notification_definition` (`id`),
  CONSTRAINT `notification_configuration_ibfk_6` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `notification_configuration_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.notification_definition
CREATE TABLE IF NOT EXISTS `notification_definition` (
  `id` varchar(100) NOT NULL,
  `notification_type` varchar(50) NOT NULL,
  `notification_purpose` varchar(50) NOT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `notification_type_id` (`notification_type`),
  KEY `scope_id` (`scope_id`),
  CONSTRAINT `notification_definition_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.program
CREATE TABLE IF NOT EXISTS `program` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `imported_code` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned NOT NULL,
  `institution_id` int(10) unsigned NOT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `advert_incomplete_section` text,
  `imported_opportunity_type_id` int(10) unsigned NOT NULL,
  `opportunity_category` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `duration_minimum` int(3) unsigned DEFAULT NULL,
  `duration_maximum` int(3) unsigned DEFAULT NULL,
  `require_position_definition` int(1) unsigned NOT NULL,
  `application_rating_count` int(10) unsigned DEFAULT NULL,
  `application_rating_frequency` decimal(10,2) unsigned DEFAULT NULL,
  `application_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `opportunity_rating_count` int(10) unsigned DEFAULT NULL,
  `opportunity_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `updated_timestamp_sitemap` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_2` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  KEY `system_id` (`system_id`),
  KEY `created_timestamp` (`created_timestamp`,`sequence_identifier`),
  KEY `state_id` (`state_id`,`sequence_identifier`),
  KEY `previous_state_id` (`previous_state_id`,`sequence_identifier`),
  KEY `institution_id` (`institution_id`,`sequence_identifier`),
  KEY `code` (`code`,`sequence_identifier`),
  KEY `title` (`name`,`sequence_identifier`),
  KEY `updated_timestamp` (`updated_timestamp`,`sequence_identifier`),
  KEY `imported_code` (`imported_code`,`sequence_identifier`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  KEY `advert_id` (`advert_id`,`sequence_identifier`),
  KEY `due_date` (`due_date`),
  KEY `updated_timestamp_sitemap` (`updated_timestamp_sitemap`),
  KEY `department_id` (`department_id`,`sequence_identifier`),
  KEY `opportunity_type_id` (`imported_opportunity_type_id`,`sequence_identifier`),
  KEY `duration_minimum` (`duration_minimum`,`duration_maximum`,`sequence_identifier`),
  KEY `application_rating_count` (`application_rating_count`,`sequence_identifier`),
  KEY `application_rating_frequency` (`application_rating_frequency`,`sequence_identifier`),
  KEY `application_rating_average` (`application_rating_average`,`sequence_identifier`),
  KEY `opportunity_rating_count` (`opportunity_rating_count`,`sequence_identifier`),
  KEY `opportunity_rating_average` (`opportunity_rating_average`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  CONSTRAINT `program_ibfk_12` FOREIGN KEY (`imported_opportunity_type_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `program_ibfk_3` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `program_ibfk_4` FOREIGN KEY (`previous_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `program_ibfk_5` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `program_ibfk_6` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `program_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `program_ibfk_9` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `program_institution_fk` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.project
CREATE TABLE IF NOT EXISTS `project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `imported_code` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `system_id` int(10) unsigned NOT NULL,
  `institution_id` int(10) unsigned NOT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `advert_incomplete_section` text,
  `imported_opportunity_type_id` int(10) unsigned NOT NULL,
  `opportunity_category` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `duration_minimum` int(3) unsigned DEFAULT NULL,
  `duration_maximum` int(3) unsigned DEFAULT NULL,
  `require_position_definition` int(1) unsigned NOT NULL,
  `application_rating_count` int(10) unsigned DEFAULT NULL,
  `application_rating_frequency` decimal(10,2) unsigned DEFAULT NULL,
  `application_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `opportunity_rating_count` int(10) unsigned DEFAULT NULL,
  `opportunity_rating_average` decimal(3,2) unsigned DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `updated_timestamp_sitemap` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_2` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  KEY `system_id` (`system_id`),
  KEY `program_id` (`program_id`,`sequence_identifier`),
  KEY `title` (`name`,`sequence_identifier`),
  KEY `created_timestamp` (`created_timestamp`,`sequence_identifier`),
  KEY `state_id` (`state_id`,`sequence_identifier`),
  KEY `previous_state_id` (`previous_state_id`,`sequence_identifier`),
  KEY `institution_id` (`institution_id`,`sequence_identifier`),
  KEY `updated_timestamp` (`updated_timestamp`,`sequence_identifier`),
  KEY `code` (`code`,`sequence_identifier`),
  KEY `user_id` (`user_id`,`sequence_identifier`),
  KEY `advert_id` (`advert_id`,`sequence_identifier`),
  KEY `due_date` (`due_date`),
  KEY `updated_timestamp_sitemap` (`updated_timestamp_sitemap`),
  KEY `department_id` (`department_id`,`sequence_identifier`),
  KEY `opportunity_type_id` (`imported_opportunity_type_id`,`sequence_identifier`),
  KEY `duration_minimum` (`duration_minimum`,`duration_maximum`,`sequence_identifier`),
  KEY `application_rating_count` (`application_rating_count`,`sequence_identifier`),
  KEY `application_rating_frequency` (`application_rating_frequency`,`sequence_identifier`),
  KEY `application_rating_average` (`application_rating_average`,`sequence_identifier`),
  KEY `imported_code` (`imported_code`,`sequence_identifier`),
  KEY `opportunity_rating_count` (`opportunity_rating_count`,`sequence_identifier`),
  KEY `opportunity_rating_average` (`opportunity_rating_average`,`sequence_identifier`),
  KEY `opportunity_category` (`opportunity_category`,`sequence_identifier`),
  CONSTRAINT `project_ibfk_11` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `project_ibfk_12` FOREIGN KEY (`imported_opportunity_type_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `project_ibfk_2` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `project_ibfk_3` FOREIGN KEY (`previous_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `project_ibfk_4` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `project_ibfk_5` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `project_ibfk_6` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `project_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `project_ibfk_8` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_condition
CREATE TABLE IF NOT EXISTS `resource_condition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `action_condition` varchar(50) NOT NULL,
  `internal_mode` int(10) unsigned DEFAULT NULL,
  `external_mode` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`action_condition`),
  UNIQUE KEY `institution_id` (`institution_id`,`action_condition`),
  UNIQUE KEY `program_id` (`program_id`,`action_condition`),
  UNIQUE KEY `project_id` (`project_id`,`action_condition`),
  UNIQUE KEY `application_id` (`application_id`,`action_condition`),
  UNIQUE KEY `department_id` (`department_id`,`action_condition`),
  KEY `system_id_2` (`system_id`,`internal_mode`),
  KEY `institution_id_2` (`institution_id`,`internal_mode`),
  KEY `department_id_2` (`department_id`,`internal_mode`),
  KEY `program_id_2` (`program_id`,`internal_mode`),
  KEY `project_id_2` (`project_id`,`internal_mode`),
  KEY `application_id_2` (`application_id`,`internal_mode`),
  KEY `resume_id_2` (`internal_mode`),
  KEY `system_id_3` (`system_id`,`external_mode`),
  KEY `institution_id_3` (`institution_id`,`external_mode`),
  KEY `department_id_3` (`department_id`,`external_mode`),
  KEY `program_id_3` (`program_id`,`external_mode`),
  KEY `project_id_3` (`project_id`,`external_mode`),
  KEY `application_id_3` (`application_id`,`external_mode`),
  KEY `resume_id_3` (`external_mode`),
  CONSTRAINT `resource_condition_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `resource_condition_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `resource_condition_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_condition_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `resource_condition_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `resource_condition_ibfk_6` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_list_filter
CREATE TABLE IF NOT EXISTS `resource_list_filter` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_account_id` int(10) unsigned NOT NULL,
  `scope_id` varchar(50) NOT NULL,
  `urgent_only` int(1) unsigned NOT NULL,
  `match_mode` varchar(10) NOT NULL,
  `sort_order` varchar(10) NOT NULL,
  `value_string` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_id` (`user_account_id`,`scope_id`),
  KEY `scope_id` (`scope_id`),
  CONSTRAINT `resource_list_filter_ibfk_1` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `resource_list_filter_ibfk_2` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_list_filter_constraint
CREATE TABLE IF NOT EXISTS `resource_list_filter_constraint` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resource_list_filter_id` int(10) unsigned NOT NULL,
  `filter_property` varchar(50) NOT NULL,
  `filter_expression` varchar(50) NOT NULL,
  `negated` int(1) unsigned NOT NULL,
  `display_position` int(3) NOT NULL,
  `value_string` varchar(255) DEFAULT NULL,
  `value_state_group_id` varchar(50) DEFAULT NULL,
  `value_reserve_status` varchar(50) DEFAULT NULL,
  `value_date_start` date DEFAULT NULL,
  `value_date_close` date DEFAULT NULL,
  `value_decimal_start` decimal(10,2) DEFAULT NULL,
  `value_decimal_close` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_list_filter_id` (`resource_list_filter_id`,`filter_property`,`filter_expression`,`value_string`),
  UNIQUE KEY `resource_list_filter_id_2` (`resource_list_filter_id`,`filter_property`,`filter_expression`,`value_state_group_id`),
  UNIQUE KEY `resource_list_filter_id_3` (`resource_list_filter_id`,`filter_property`,`filter_expression`,`value_date_start`,`value_date_close`),
  UNIQUE KEY `resource_list_filter_id_4` (`resource_list_filter_id`,`filter_property`,`filter_expression`,`value_decimal_start`,`value_decimal_close`),
  UNIQUE KEY `resource_list_filter_id_6` (`resource_list_filter_id`,`filter_property`,`filter_expression`,`value_reserve_status`),
  KEY `filter_property` (`filter_property`),
  KEY `resource_list_filter_id_5` (`resource_list_filter_id`,`display_position`),
  KEY `value_state_group_id` (`value_state_group_id`),
  CONSTRAINT `resource_list_filter_constraint_ibfk_1` FOREIGN KEY (`resource_list_filter_id`) REFERENCES `resource_list_filter` (`id`),
  CONSTRAINT `resource_list_filter_constraint_ibfk_2` FOREIGN KEY (`value_state_group_id`) REFERENCES `state_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_previous_state
CREATE TABLE IF NOT EXISTS `resource_previous_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `state_id` varchar(100) NOT NULL,
  `primary_state` int(1) unsigned NOT NULL,
  `created_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`state_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`state_id`),
  UNIQUE KEY `program_id` (`program_id`,`state_id`),
  UNIQUE KEY `project_id` (`project_id`,`state_id`),
  UNIQUE KEY `application_id` (`application_id`,`state_id`),
  UNIQUE KEY `department_id` (`department_id`,`state_id`),
  KEY `state_id` (`state_id`),
  KEY `state_id_2` (`state_id`,`created_date`),
  CONSTRAINT `resource_previous_state_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_6` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `resource_previous_state_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_state
CREATE TABLE IF NOT EXISTS `resource_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `state_id` varchar(100) NOT NULL,
  `primary_state` int(1) unsigned NOT NULL,
  `created_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`state_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`state_id`),
  UNIQUE KEY `program_id` (`program_id`,`state_id`),
  UNIQUE KEY `project_id` (`project_id`,`state_id`),
  UNIQUE KEY `application_id` (`application_id`,`state_id`),
  UNIQUE KEY `department_id` (`department_id`,`state_id`),
  KEY `state_id` (`state_id`),
  KEY `state_id_2` (`state_id`,`created_date`),
  CONSTRAINT `resource_state_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `resource_state_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `resource_state_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_state_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `resource_state_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `resource_state_ibfk_6` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `resource_state_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_state_transition_summary
CREATE TABLE IF NOT EXISTS `resource_state_transition_summary` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `state_group_id` varchar(50) NOT NULL,
  `transition_state_selection` varchar(255) NOT NULL,
  `frequency` int(10) unsigned NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`state_group_id`,`transition_state_selection`),
  UNIQUE KEY `institution_id` (`institution_id`,`state_group_id`,`transition_state_selection`),
  UNIQUE KEY `program_id` (`program_id`,`state_group_id`,`transition_state_selection`),
  UNIQUE KEY `project_id` (`project_id`,`state_group_id`,`transition_state_selection`),
  UNIQUE KEY `department_id` (`department_id`,`state_group_id`,`transition_state_selection`),
  KEY `state_group_id` (`state_group_id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_5` FOREIGN KEY (`state_group_id`) REFERENCES `state_group` (`id`),
  CONSTRAINT `resource_state_transition_summary_ibfk_6` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.resource_study_option
CREATE TABLE IF NOT EXISTS `resource_study_option` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `imported_study_option_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `program_id` (`program_id`,`imported_study_option_id`),
  UNIQUE KEY `project_id` (`project_id`,`imported_study_option_id`),
  KEY `study_option_id` (`imported_study_option_id`),
  CONSTRAINT `resource_study_option_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_study_option_ibfk_4` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `resource_study_option_ibfk_5` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `resource_study_option_ibfk_6` FOREIGN KEY (`imported_study_option_id`) REFERENCES `imported_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` varchar(50) NOT NULL,
  `role_category` varchar(50) NOT NULL,
  `scope_creator` int(1) unsigned DEFAULT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `scope_id` (`scope_id`),
  KEY `role_category` (`role_category`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.role_transition
CREATE TABLE IF NOT EXISTS `role_transition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_transition_id` int(10) unsigned NOT NULL,
  `role_id` varchar(50) NOT NULL,
  `role_transition_type` varchar(50) NOT NULL,
  `transition_role_id` varchar(50) NOT NULL,
  `restrict_to_action_owner` int(1) unsigned NOT NULL,
  `minimum_permitted` int(1) unsigned DEFAULT NULL,
  `maximum_permitted` int(1) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_transition_id` (`state_transition_id`,`role_id`,`role_transition_type`),
  KEY `role_id` (`role_id`),
  KEY `role_transition_type_id` (`role_transition_type`),
  KEY `transition_role_id` (`transition_role_id`),
  CONSTRAINT `role_transition_ibfk_1` FOREIGN KEY (`state_transition_id`) REFERENCES `state_transition` (`id`),
  CONSTRAINT `role_transition_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `role_transition_ibfk_4` FOREIGN KEY (`transition_role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.scope
CREATE TABLE IF NOT EXISTS `scope` (
  `id` varchar(50) NOT NULL,
  `scope_category` varchar(50) NOT NULL,
  `short_code` varchar(2) NOT NULL,
  `ordinal` int(2) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `precedence` (`ordinal`),
  UNIQUE KEY `short_code` (`short_code`),
  KEY `scope_category` (`scope_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state
CREATE TABLE IF NOT EXISTS `state` (
  `id` varchar(100) NOT NULL,
  `state_group_id` varchar(50) NOT NULL,
  `state_duration_definition_id` varchar(100) DEFAULT NULL,
  `state_duration_evaluation` varchar(50) DEFAULT NULL,
  `parallelizable` int(1) unsigned DEFAULT NULL,
  `hidden` int(1) unsigned DEFAULT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_state_id` (`state_group_id`),
  KEY `state_duration_definition_id` (`state_duration_definition_id`),
  KEY `parallelizable` (`parallelizable`),
  KEY `scope_id` (`scope_id`,`hidden`),
  CONSTRAINT `state_ibfk_3` FOREIGN KEY (`state_group_id`) REFERENCES `state_group` (`id`),
  CONSTRAINT `state_ibfk_4` FOREIGN KEY (`state_duration_definition_id`) REFERENCES `state_duration_definition` (`id`),
  CONSTRAINT `state_ibfk_5` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_action
CREATE TABLE IF NOT EXISTS `state_action` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_id` varchar(100) NOT NULL,
  `action_id` varchar(100) NOT NULL,
  `raises_urgent_flag` int(1) unsigned NOT NULL,
  `action_condition` varchar(50) DEFAULT NULL,
  `action_enhancement` varchar(50) DEFAULT NULL,
  `notification_definition_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_id` (`state_id`,`action_id`),
  KEY `action_id` (`action_id`),
  KEY `notification_template_id` (`notification_definition_id`),
  KEY `raises_urgent_flag` (`raises_urgent_flag`),
  KEY `action_enhancement` (`action_enhancement`),
  KEY `action_condition` (`action_condition`),
  CONSTRAINT `state_action_ibfk_1` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `state_action_ibfk_2` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `state_action_ibfk_4` FOREIGN KEY (`notification_definition_id`) REFERENCES `notification_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_action_assignment
CREATE TABLE IF NOT EXISTS `state_action_assignment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_action_id` int(10) unsigned DEFAULT NULL,
  `role_id` varchar(50) NOT NULL,
  `external_mode` int(1) unsigned NOT NULL,
  `action_enhancement` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_action_id` (`state_action_id`,`role_id`,`external_mode`),
  KEY `role_id` (`role_id`),
  KEY `action_enhancement` (`action_enhancement`),
  CONSTRAINT `state_action_assignment_ibfk_1` FOREIGN KEY (`state_action_id`) REFERENCES `state_action` (`id`),
  CONSTRAINT `state_action_assignment_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_action_notification
CREATE TABLE IF NOT EXISTS `state_action_notification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_action_id` int(10) unsigned DEFAULT NULL,
  `role_id` varchar(50) NOT NULL,
  `notification_definition_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_action_id` (`state_action_id`,`role_id`),
  KEY `role_id` (`role_id`),
  KEY `notification_template_id` (`notification_definition_id`),
  CONSTRAINT `state_action_notification_ibfk_1` FOREIGN KEY (`state_action_id`) REFERENCES `state_action` (`id`),
  CONSTRAINT `state_action_notification_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `state_action_notification_ibfk_4` FOREIGN KEY (`notification_definition_id`) REFERENCES `notification_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_duration_configuration
CREATE TABLE IF NOT EXISTS `state_duration_configuration` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `opportunity_type` varchar(50) DEFAULT NULL,
  `state_duration_definition_id` varchar(100) NOT NULL,
  `duration` int(3) unsigned NOT NULL,
  `system_default` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`opportunity_type`,`state_duration_definition_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`opportunity_type`,`state_duration_definition_id`),
  UNIQUE KEY `program_id` (`program_id`,`state_duration_definition_id`),
  UNIQUE KEY `project_id` (`project_id`,`state_duration_definition_id`),
  UNIQUE KEY `department_id` (`department_id`,`opportunity_type`,`state_duration_definition_id`),
  KEY `state_id` (`state_duration_definition_id`),
  KEY `system_default` (`system_id`,`system_default`),
  CONSTRAINT `state_duration_configuration_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `state_duration_configuration_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `state_duration_configuration_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `state_duration_configuration_ibfk_5` FOREIGN KEY (`state_duration_definition_id`) REFERENCES `state_duration_definition` (`id`),
  CONSTRAINT `state_duration_configuration_ibfk_6` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `state_duration_configuration_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_duration_definition
CREATE TABLE IF NOT EXISTS `state_duration_definition` (
  `id` varchar(100) NOT NULL,
  `escalation` int(1) unsigned NOT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `scope_id` (`scope_id`),
  CONSTRAINT `state_duration_definition_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_group
CREATE TABLE IF NOT EXISTS `state_group` (
  `id` varchar(50) NOT NULL,
  `ordinal` int(2) unsigned NOT NULL,
  `repeatable` int(1) unsigned DEFAULT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `scope_id` (`scope_id`,`ordinal`),
  CONSTRAINT `state_group_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_termination
CREATE TABLE IF NOT EXISTS `state_termination` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_transition_id` int(10) unsigned NOT NULL,
  `termination_state_id` varchar(50) NOT NULL,
  `state_termination_evaluation` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_transition_id` (`state_transition_id`,`termination_state_id`),
  KEY `termination_state_id` (`termination_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_transition
CREATE TABLE IF NOT EXISTS `state_transition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state_action_id` int(10) unsigned NOT NULL,
  `transition_state_id` varchar(100) DEFAULT NULL,
  `transition_action_id` varchar(100) NOT NULL,
  `state_transition_evaluation_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `state_action_id` (`state_action_id`,`transition_state_id`),
  KEY `transition_state_id` (`transition_state_id`),
  KEY `transition_action_id` (`transition_action_id`),
  KEY `state_transition_evaluation_id` (`state_transition_evaluation_id`),
  CONSTRAINT `state_transition_ibfk_1` FOREIGN KEY (`state_action_id`) REFERENCES `state_action` (`id`),
  CONSTRAINT `state_transition_ibfk_3` FOREIGN KEY (`transition_state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `state_transition_ibfk_4` FOREIGN KEY (`transition_action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `state_transition_ibfk_5` FOREIGN KEY (`state_transition_evaluation_id`) REFERENCES `state_transition_evaluation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_transition_evaluation
CREATE TABLE IF NOT EXISTS `state_transition_evaluation` (
  `id` varchar(100) NOT NULL,
  `next_state_selection` int(1) unsigned NOT NULL,
  `scope_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `next_state_selection` (`next_state_selection`),
  KEY `scope_id` (`scope_id`),
  CONSTRAINT `state_transition_evaluation_ibfk_1` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_transition_pending
CREATE TABLE IF NOT EXISTS `state_transition_pending` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `action_id` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`action_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`action_id`),
  UNIQUE KEY `program_id` (`program_id`,`action_id`),
  UNIQUE KEY `project_id` (`project_id`,`action_id`),
  UNIQUE KEY `application_id` (`application_id`,`action_id`),
  UNIQUE KEY `department_id` (`department_id`,`action_id`),
  KEY `action_id` (`action_id`),
  CONSTRAINT `state_transition_pending_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_6` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `state_transition_pending_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.state_transition_propagation
CREATE TABLE IF NOT EXISTS `state_transition_propagation` (
  `state_transition_id` int(10) unsigned NOT NULL,
  `propagated_action_id` varchar(100) NOT NULL,
  PRIMARY KEY (`state_transition_id`,`propagated_action_id`),
  KEY `action_id` (`propagated_action_id`),
  CONSTRAINT `state_transition_propagation_ibfk_1` FOREIGN KEY (`state_transition_id`) REFERENCES `state_transition` (`id`),
  CONSTRAINT `state_transition_propagation_ibfk_2` FOREIGN KEY (`propagated_action_id`) REFERENCES `action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.system
CREATE TABLE IF NOT EXISTS `system` (
  `id` int(10) unsigned NOT NULL,
  `code` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `cipher_salt` varchar(36) NOT NULL,
  `amazon_access_key` varchar(50) DEFAULT NULL,
  `amazon_secret_key` varchar(50) DEFAULT NULL,
  `state_id` varchar(100) DEFAULT NULL,
  `previous_state_id` varchar(100) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `last_amazon_cleanup_date` date DEFAULT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `sequence_identifier` (`sequence_identifier`),
  UNIQUE KEY `advert_id` (`advert_id`),
  KEY `state_id` (`state_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `system_ibfk_1` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`),
  CONSTRAINT `system_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `system_ibfk_3` FOREIGN KEY (`advert_id`) REFERENCES `system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(30) NOT NULL,
  `first_name_2` varchar(30) DEFAULT NULL,
  `first_name_3` varchar(30) DEFAULT NULL,
  `last_name` varchar(40) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_bounced_message` text,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `position_title` varchar(255) DEFAULT NULL,
  `activation_code` varchar(40) DEFAULT NULL,
  `user_account_id` int(10) unsigned DEFAULT NULL,
  `parent_user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `activation_code` (`activation_code`),
  KEY `last_name` (`last_name`,`first_name`),
  KEY `full_name` (`full_name`),
  KEY `parent_user_id` (`parent_user_id`),
  KEY `institution_id` (`institution_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`parent_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_ibfk_3` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_account
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `password` varchar(32) DEFAULT NULL,
  `user_account_external_id` int(10) unsigned DEFAULT NULL,
  `portrait_image_id` int(10) unsigned DEFAULT NULL,
  `temporary_password` varchar(32) DEFAULT NULL,
  `temporary_password_expiry_timestamp` datetime DEFAULT NULL,
  `send_application_recommendation_notification` int(1) unsigned NOT NULL,
  `user_personal_detail_id` int(10) unsigned DEFAULT NULL,
  `user_address_id` int(10) unsigned DEFAULT NULL,
  `user_document_id` int(10) unsigned DEFAULT NULL,
  `user_additional_information_id` int(10) unsigned DEFAULT NULL,
  `enabled` int(1) unsigned NOT NULL DEFAULT '0',
  `shared` int(1) unsigned NOT NULL,
  `updated_timestamp` datetime NOT NULL,
  `sequence_identifier` varchar(23) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_external_id` (`user_account_external_id`),
  UNIQUE KEY `user_document_id` (`user_document_id`),
  UNIQUE KEY `user_address_id` (`user_address_id`),
  UNIQUE KEY `user_personal_detail_id` (`user_personal_detail_id`),
  UNIQUE KEY `user_additional_information_id` (`user_additional_information_id`),
  KEY `password` (`password`),
  KEY `send_recommendation_email` (`send_application_recommendation_notification`),
  KEY `portrait_image_id` (`portrait_image_id`),
  KEY `shared` (`shared`),
  CONSTRAINT `user_account_ibfk_1` FOREIGN KEY (`user_account_external_id`) REFERENCES `user_account_external` (`id`),
  CONSTRAINT `user_account_ibfk_2` FOREIGN KEY (`portrait_image_id`) REFERENCES `document` (`id`),
  CONSTRAINT `user_account_ibfk_4` FOREIGN KEY (`user_address_id`) REFERENCES `user_address` (`id`),
  CONSTRAINT `user_account_ibfk_5` FOREIGN KEY (`user_personal_detail_id`) REFERENCES `user_personal_detail` (`id`),
  CONSTRAINT `user_account_ibfk_6` FOREIGN KEY (`user_document_id`) REFERENCES `user_document` (`id`),
  CONSTRAINT `user_account_ibfk_7` FOREIGN KEY (`user_additional_information_id`) REFERENCES `user_additional_information` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_account_external
CREATE TABLE IF NOT EXISTS `user_account_external` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_account_id` int(10) unsigned NOT NULL,
  `external_account_type` varchar(50) NOT NULL,
  `external_account_identifier` varchar(50) NOT NULL,
  `external_account_profile_url` mediumtext,
  `external_account_image_url` mediumtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_id_2` (`user_account_id`,`external_account_type`),
  UNIQUE KEY `external_account_type` (`external_account_type`,`external_account_identifier`),
  KEY `user_account_id` (`user_account_id`),
  CONSTRAINT `user_account_external_ibfk_1` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_additional_information
CREATE TABLE IF NOT EXISTS `user_additional_information` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `convictions_text` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_address
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `current_address_id` int(10) unsigned NOT NULL,
  `contact_address_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `current_address_id` (`current_address_id`),
  KEY `contact_address_id` (`contact_address_id`),
  CONSTRAINT `user_address_ibfk_1` FOREIGN KEY (`current_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `user_address_ibfk_2` FOREIGN KEY (`contact_address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_advert
CREATE TABLE IF NOT EXISTS `user_advert` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `identified` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`advert_id`),
  KEY `advert_id` (`advert_id`),
  KEY `user_id_2` (`user_id`,`identified`),
  CONSTRAINT `user_advert_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_advert_ibfk_2` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_competence
CREATE TABLE IF NOT EXISTS `user_competence` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `competence_id` int(10) unsigned NOT NULL,
  `rating_count` int(10) unsigned NOT NULL,
  `rating_average` decimal(3,2) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`competence_id`),
  KEY `competence_id` (`competence_id`),
  CONSTRAINT `user_competence_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_competence_ibfk_2` FOREIGN KEY (`competence_id`) REFERENCES `competence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_document
CREATE TABLE IF NOT EXISTS `user_document` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `personal_summary` varchar(1000) DEFAULT NULL,
  `cv_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cv_id` (`cv_id`),
  CONSTRAINT `user_document_ibfk_1` FOREIGN KEY (`cv_id`) REFERENCES `document` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_employment_position
CREATE TABLE IF NOT EXISTS `user_employment_position` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_account_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `start_year` int(4) unsigned NOT NULL,
  `start_month` int(2) unsigned NOT NULL,
  `end_year` int(4) unsigned DEFAULT NULL,
  `end_month` int(2) unsigned DEFAULT NULL,
  `current` int(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_id` (`user_account_id`,`advert_id`),
  KEY `advert_id` (`advert_id`),
  CONSTRAINT `user_employment_position_ibfk_1` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `user_employment_position_ibfk_2` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_feedback
CREATE TABLE IF NOT EXISTS `user_feedback` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_category` varchar(50) NOT NULL,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `action_id` varchar(100) NOT NULL,
  `declined_response` int(1) unsigned NOT NULL,
  `rating` int(1) unsigned DEFAULT NULL,
  `content` mediumtext,
  `feature_request` mediumtext,
  `recommended` int(1) DEFAULT NULL,
  `created_timestamp` datetime NOT NULL,
  `sequence_identifier` varchar(23) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `declined_response` (`declined_response`,`sequence_identifier`),
  KEY `system_id` (`system_id`,`sequence_identifier`),
  KEY `institution_id` (`institution_id`,`sequence_identifier`),
  KEY `program_id` (`program_id`,`sequence_identifier`),
  KEY `project_id` (`project_id`,`sequence_identifier`),
  KEY `application_id` (`application_id`,`sequence_identifier`),
  KEY `action_id` (`action_id`),
  KEY `department_id` (`department_id`,`sequence_identifier`),
  CONSTRAINT `user_feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_feedback_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `user_feedback_ibfk_3` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `user_feedback_ibfk_4` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `user_feedback_ibfk_5` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `user_feedback_ibfk_6` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `user_feedback_ibfk_7` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`),
  CONSTRAINT `user_feedback_ibfk_8` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_notification
CREATE TABLE IF NOT EXISTS `user_notification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `notification_definition_id` varchar(100) NOT NULL,
  `last_notified_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`user_id`,`notification_definition_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`user_id`,`notification_definition_id`),
  UNIQUE KEY `program_id` (`program_id`,`user_id`,`notification_definition_id`),
  UNIQUE KEY `project_id` (`project_id`,`user_id`,`notification_definition_id`),
  UNIQUE KEY `application_id` (`application_id`,`user_id`,`notification_definition_id`),
  UNIQUE KEY `department_id` (`department_id`,`user_id`,`notification_definition_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_notification_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `user_notification_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `user_notification_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `user_notification_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `user_notification_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `user_notification_ibfk_6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_notification_ibfk_7` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_personal_detail
CREATE TABLE IF NOT EXISTS `user_personal_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `imported_title_id` int(10) unsigned DEFAULT NULL,
  `imported_gender_id` int(10) unsigned DEFAULT NULL,
  `date_of_birth` date NOT NULL,
  `imported_nationality_id` varchar(10) DEFAULT NULL,
  `imported_domicile_id` varchar(10) DEFAULT NULL,
  `visa_required` int(1) unsigned DEFAULT NULL,
  `phone` varchar(50) NOT NULL,
  `skype` varchar(50) DEFAULT NULL,
  `imported_ethnicity_id` int(10) unsigned DEFAULT NULL,
  `imported_disability_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `imported_ethnicity_id` (`imported_ethnicity_id`),
  KEY `imported_disability_id` (`imported_disability_id`),
  KEY `imported_title_id` (`imported_title_id`),
  KEY `imported_gender_id` (`imported_gender_id`),
  KEY `imported_nationality_id` (`imported_nationality_id`),
  KEY `imported_domicile_id` (`imported_domicile_id`),
  CONSTRAINT `user_personal_detail_ibfk_1` FOREIGN KEY (`imported_gender_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `user_personal_detail_ibfk_2` FOREIGN KEY (`imported_disability_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `user_personal_detail_ibfk_3` FOREIGN KEY (`imported_ethnicity_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `user_personal_detail_ibfk_4` FOREIGN KEY (`imported_title_id`) REFERENCES `imported_entity` (`id`),
  CONSTRAINT `user_personal_detail_ibfk_5` FOREIGN KEY (`imported_nationality_id`) REFERENCES `imported_domicile` (`id`),
  CONSTRAINT `user_personal_detail_ibfk_6` FOREIGN KEY (`imported_domicile_id`) REFERENCES `imported_domicile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_qualification
CREATE TABLE IF NOT EXISTS `user_qualification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `start_year` int(4) unsigned NOT NULL,
  `start_month` int(2) unsigned NOT NULL,
  `award_year` int(4) unsigned DEFAULT NULL,
  `award_month` int(2) unsigned DEFAULT NULL,
  `grade` varchar(200) NOT NULL,
  `completed` varchar(10) NOT NULL,
  `document_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_id` (`user_id`,`advert_id`),
  UNIQUE KEY `document_id` (`document_id`),
  KEY `advert_id` (`advert_id`),
  CONSTRAINT `user_qualification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_qualification_ibfk_2` FOREIGN KEY (`document_id`) REFERENCES `document` (`id`),
  CONSTRAINT `user_qualification_ibfk_3` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_referee
CREATE TABLE IF NOT EXISTS `user_referee` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_account_id` int(10) unsigned NOT NULL,
  `advert_id` int(10) unsigned NOT NULL,
  `skype` varchar(50) DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `phone` varchar(50) NOT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_id` (`user_account_id`,`advert_id`),
  KEY `advert_id` (`advert_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_referee_ibfk_2` FOREIGN KEY (`advert_id`) REFERENCES `advert` (`id`),
  CONSTRAINT `user_referee_ibfk_4` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `user_referee_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table prism.user_role
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(10) unsigned DEFAULT NULL,
  `institution_id` int(10) unsigned DEFAULT NULL,
  `department_id` int(10) unsigned DEFAULT NULL,
  `program_id` int(10) unsigned DEFAULT NULL,
  `project_id` int(10) unsigned DEFAULT NULL,
  `application_id` int(10) unsigned DEFAULT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `role_id` varchar(50) NOT NULL,
  `target_role_id` varchar(50) DEFAULT NULL,
  `assigned_timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_id` (`system_id`,`user_id`,`role_id`),
  UNIQUE KEY `institution_id` (`institution_id`,`user_id`,`role_id`),
  UNIQUE KEY `program_id` (`program_id`,`user_id`,`role_id`),
  UNIQUE KEY `project_id` (`project_id`,`user_id`,`role_id`),
  UNIQUE KEY `application_id` (`application_id`,`user_id`,`role_id`),
  UNIQUE KEY `department_id` (`department_id`,`user_id`,`role_id`),
  KEY `user_id` (`user_id`),
  KEY `role_id` (`role_id`),
  KEY `target_role_id` (`target_role_id`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `system` (`id`),
  CONSTRAINT `user_role_ibfk_10` FOREIGN KEY (`target_role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`),
  CONSTRAINT `user_role_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `program` (`id`),
  CONSTRAINT `user_role_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `user_role_ibfk_5` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `user_role_ibfk_6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_role_ibfk_7` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `user_role_ibfk_8` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
