/* Provide review comment */

ALTER TABLE COMMENT
	ADD COLUMN application_desire_to_interview INT(1) UNSIGNED AFTER application_suitable_for_opportunity,
	ADD COLUMN application_desire_to_supervise INT(1) UNSIGNED AFTER application_desire_to_interview
;

UPDATE REVIEW_COMMENT INNER JOIN COMMENT
	ON REVIEW_COMMENT.id = COMMENT.id
SET COMMENT.role_id = "APPLICATION_REVIEWER",
	COMMENT.action_id = "APPLICATION_PROVIDE_REVIEW",
	COMMENT.application_suitable_for_institution = REVIEW_COMMENT.suitable_candidate,
	COMMENT.application_suitable_for_opportunity = REVIEW_COMMENT.applicant_suitable_for_programme,
	COMMENT.application_desire_to_interview = REVIEW_COMMENT.willing_to_interview,
	COMMENT.application_desire_to_supervise = REVIEW_COMMENT.willing_to_work_with_applicant,
	COMMENT.application_rating = REVIEW_COMMENT.applicant_rating,
	COMMENT.declined_response = REVIEW_COMMENT.decline
;

DROP TABLE REVIEW_COMMENT
;

/* Review evaluation comment */
	
UPDATE COMMENT INNER JOIN REVIEW_EVALUATION_COMMENT
	ON COMMENT.id = REVIEW_EVALUATION_COMMENT.id
INNER JOIN STATECHANGE_COMMENT
	ON REVIEW_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET COMMENT.role_id = "PROGRAM_ADMINISTRATOR",
	COMMENT.action_id = "APPLICATION_COMPLETE_REVIEW_STAGE",
	COMMENT.transition_state_id = CONCAT("APPLICATION_", STATECHANGE_COMMENT.next_status),
	COMMENT.use_custom_recruiter_questions = STATECHANGE_COMMENT.use_custom_questions
;

INSERT INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT id, user_id, "APPLICATION_ADMINISTRATOR"
	FROM STATECHANGE_COMMENT
	WHERE comment_type = "REVIEW_EVALUATION"
		AND user_id IS NOT NULL
;

DROP TABLE REVIEW_EVALUATION_COMMENT
;

/* Is under consideration property for states */

ALTER TABLE STATE
	ADD COLUMN is_under_assessment INT(1) UNSIGNED
;

UPDATE STATE
SET is_under_assessment = 0
;

UPDATE STATE
SET is_under_assessment = 1
WHERE parent_state_id IN ("APPLICATION_VALIDATION", "APPLICATION_REVIEW",
	"APPLICATION_INTERVIEW", "APPLICATION_APPROVAL", "PROGRAM_MODIFIABLE")
	OR id IN ("APPLICATION_APPROVED", "APPLICATION_REJECTED")
;

ALTER TABLE STATE
	MODIFY COLUMN is_under_assessment INT(1) UNSIGNED NOT NULL
;

/* Reorganise comment table */

ALTER TABLE COMMENT
	CHANGE COLUMN use_custom_referee_questions application_use_custom_referee_questions INT(1) UNSIGNED AFTER application_rating,
	CHANGE COLUMN use_custom_recruiter_questions application_use_custom_recruiter_questions INT(1) UNSIGNED AFTER application_use_custom_referee_questions,
	MODIFY COLUMN comment_custom_question_version_id INT(10) UNSIGNED AFTER application_use_custom_recruiter_questions,
	MODIFY COLUMN custom_question_response LONGTEXT AFTER comment_custom_question_version_id
;

/* Remove non-UCL institutions & clean up system reference country data */

ALTER TABLE APPLICATION_QUALIFICATION
	DROP FOREIGN KEY application_qualification_ibfk_3,
	ADD FOREIGN KEY (institution_id) REFERENCES IMPORTED_INSTITUTION (id)
;

ALTER TABLE INSTITUTION
	DROP COLUMN code
;

DELETE FROM INSTITUTION
WHERE id != 5243
;

DELETE
FROM INSTITUTION_DOMICILE
WHERE code != "XK"
;

UPDATE INSTITUTION_DOMICILE
SET code = "GB"
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AF",  "Afghanistan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AX",  "Åland Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AL",  "Albania")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DZ",  "Algeria")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AS",  "American Samoa")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AD",  "Andorra")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AO",  "Angola")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AI",  "Anguilla")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AQ",  "Antarctica")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AG",  "Antigua and Barbuda")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AR",  "Argentina")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AM",  "Armenia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AW",  "Aruba")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AU",  "Australia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AT",  "Austria")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AZ",  "Azerbaijan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BS",  "Bahamas")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BH",  "Bahrain")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BD",  "Bangladesh")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BB",  "Barbados")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BY",  "Belarus")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BE",  "Belgium")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BZ",  "Belize")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BJ",  "Benin")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BM",  "Bermuda")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BT",  "Bhutan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BO",  "Bolivia, Plurinational State of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BQ",  "Bonaire, Sint Eustatius and Saba")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BA",  "Bosnia and Herzegovina")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BW",  "Botswana")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BV",  "Bouvet Island")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BR",  "Brazil")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IO",  "British Indian Ocean Territory")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BN",  "Brunei Darussalam")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BG",  "Bulgaria")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BF",  "Burkina Faso")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BI",  "Burundi")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KH",  "Cambodia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CM",  "Cameroon")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CA",  "Canada")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CV",  "Cabo Verde")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KY",  "Cayman Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CF",  "Central African Republic")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TD",  "Chad")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CL",  "Chile")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CN",  "China")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CX",  "Christmas Island")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CC",  "Cocos (Keeling) Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CO",  "Colombia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KM",  "Comoros")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CG",  "Congo")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CD",  "Congo, the Democratic Republic of the")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CK",  "Cook Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CR",  "Costa Rica")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CI",  "Côte d'Ivoire")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HR",  "Croatia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CU",  "Cuba")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CW",  "Curaçao")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CY",  "Cyprus")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CZ",  "Czech Republic")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DK",  "Denmark")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DJ",  "Djibouti")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DM",  "Dominica")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DO",  "Dominican Republic")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("EC",  "Ecuador")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("EG",  "Egypt")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SV",  "El Salvador")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GQ",  "Equatorial Guinea")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ER",  "Eritrea")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("EE",  "Estonia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ET",  "Ethiopia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FK",  "Falkland Islands (Malvinas)")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FO",  "Faroe Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FJ",  "Fiji")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FI",  "Finland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FR",  "France")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GF",  "French Guiana")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PF",  "French Polynesia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TF",  "French Southern Territories")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GA",  "Gabon")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GM",  "Gambia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GE",  "Georgia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("DE",  "Germany")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GH",  "Ghana")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GI",  "Gibraltar")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GR",  "Greece")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GL",  "Greenland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GD",  "Grenada")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GP",  "Guadeloupe")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GU",  "Guam")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GT",  "Guatemala")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GG",  "Guernsey")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GN",  "Guinea")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GW",  "Guinea-Bissau")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GY",  "Guyana")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HT",  "Haiti")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HM",  "Heard Island and McDonald Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VA",  "Holy See (Vatican City State)")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HN",  "Honduras")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HK",  "Hong Kong")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("HU",  "Hungary")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IS",  "Iceland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IN",  "India")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ID",  "Indonesia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IR",  "Iran, Islamic Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IQ",  "Iraq")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IE",  "Ireland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IM",  "Isle of Man")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IL",  "Israel")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("IT",  "Italy")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("JM",  "Jamaica")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("JP",  "Japan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("JE",  "Jersey")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("JO",  "Jordan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KZ",  "Kazakhstan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KE",  "Kenya")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KI",  "Kiribati")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KP",  "Korea, Democratic People's Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KR",  "Korea, Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KW",  "Kuwait")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KG",  "Kyrgyzstan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LA",  "Lao People's Democratic Republic")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LV",  "Latvia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LB",  "Lebanon")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LS",  "Lesotho")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LR",  "Liberia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LY",  "Libya")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LI",  "Liechtenstein")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LT",  "Lithuania")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LU",  "Luxembourg")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MO",  "Macao")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MK",  "Macedonia, the former Yugoslav Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MG",  "Madagascar")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MW",  "Malawi")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MY",  "Malaysia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MV",  "Maldives")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ML",  "Mali")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MT",  "Malta")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MH",  "Marshall Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MQ",  "Martinique")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MR",  "Mauritania")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MU",  "Mauritius")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("YT",  "Mayotte")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MX",  "Mexico")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("FM",  "Micronesia, Federated States of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MD",  "Moldova, Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MC",  "Monaco")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MN",  "Mongolia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ME",  "Montenegro")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MS",  "Montserrat")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MA",  "Morocco")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MZ",  "Mozambique")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MM",  "Myanmar")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NA",  "Namibia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NR",  "Nauru")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NP",  "Nepal")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NL",  "Netherlands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NC",  "New Caledonia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NZ",  "New Zealand")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NI",  "Nicaragua")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NE",  "Niger")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NG",  "Nigeria")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NU",  "Niue")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NF",  "Norfolk Island")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MP",  "Northern Mariana Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("NO",  "Norway")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("OM",  "Oman")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PK",  "Pakistan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PW",  "Palau")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PS",  "Palestine, State of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PA",  "Panama")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PG",  "Papua New Guinea")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PY",  "Paraguay")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PE",  "Peru")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PH",  "Philippines")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PN",  "Pitcairn")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PL",  "Poland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PT",  "Portugal")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PR",  "Puerto Rico")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("QA",  "Qatar")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("RE",  "Réunion")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("RO",  "Romania")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("RU",  "Russian Federation")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("RW",  "Rwanda")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("BL",  "Saint Barthélemy")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SH",  "Saint Helena, Ascension and Tristan da Cunha")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("KN",  "Saint Kitts and Nevis")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LC",  "Saint Lucia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("MF",  "Saint Martin (French part)")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("PM",  "Saint Pierre and Miquelon")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VC",  "Saint Vincent and the Grenadines")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("WS",  "Samoa")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SM",  "San Marino")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ST",  "Sao Tome and Principe")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SA",  "Saudi Arabia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SN",  "Senegal")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("RS",  "Serbia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SC",  "Seychelles")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SL",  "Sierra Leone")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SG",  "Singapore")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SX",  "Sint Maarten (Dutch part)")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SK",  "Slovakia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SI",  "Slovenia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SB",  "Solomon Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SO",  "Somalia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ZA",  "South Africa")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GS",  "South Georgia and the South Sandwich Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SS",  "South Sudan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ES",  "Spain")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("LK",  "Sri Lanka")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SD",  "Sudan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SR",  "Suriname")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SJ",  "Svalbard and Jan Mayen")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SZ",  "Swaziland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SE",  "Sweden")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("CH",  "Switzerland")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("SY",  "Syrian Arab Republic")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TW",  "Taiwan, Province of China")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TJ",  "Tajikistan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TZ",  "Tanzania, United Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TH",  "Thailand")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TL",  "Timor-Leste")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TG",  "Togo")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TK",  "Tokelau")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TO",  "Tonga")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TT",  "Trinidad and Tobago")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TN",  "Tunisia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TR",  "Turkey")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TM",  "Turkmenistan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TC",  "Turks and Caicos Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("TV",  "Tuvalu")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("UG",  "Uganda")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("UA",  "Ukraine")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("AE",  "United Arab Emirates")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("GB",  "United Kingdom")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("US",  "United States")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("UM",  "United States Minor Outlying Islands")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("UY",  "Uruguay")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("UZ",  "Uzbekistan")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VU",  "Vanuatu")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VE",  "Venezuela, Bolivarian Republic of")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VN",  "Viet Nam")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VG",  "Virgin Islands, British")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("VI",  "Virgin Islands, U.S.")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("WF",  "Wallis and Futuna")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("EH",  "Western Sahara")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("YE",  "Yemen")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ZM",  "Zambia")
;

INSERT IGNORE INTO INSTITUTION_DOMICILE (code, name)
	VALUES ("ZW",  "Zimbabwe")
;
