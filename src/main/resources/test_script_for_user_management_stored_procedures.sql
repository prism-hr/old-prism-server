/* TEST SCRIPT FOR USER MANAGEMENT STORED PROCEDURES */

/* Could be automated completely at a later stage. */

/* Create some dummy users for the tests. It is difficult to delete users. 
	Just change the username/email values when you run your own tests. */

INSERT INTO registered_user (username, password, firstName, lastName, email, 
	accountNonLocked, accountNonExpired, credentialsNonExpired, enabled)
SELECT "mytest1@test.com", MD5("password"), "test", "test", "mytest1@test.com",
	1, 1, 1, 1
	UNION
SELECT "mytest2@test.com", MD5("password"), "test", "test", "mytest2@test.com",
	1, 1, 1, 1
	UNION
SELECT "mytest3@test.com", MD5("password"), "test", "test", "mytest3@test.com",
	1, 1, 1, 1
	UNION
SELECT "mytest4@test.com", MD5("password"), "test", "test", "mytest4@test.com",
	1, 1, 1, 1
	UNION
SELECT "mytest5@test.com", MD5("password"), "test", "test", "mytest5@test.com",
	1, 1, 1, 1;
	
/* Log in as prism@ucl.ac.uk and make the first test user a superadministrator.
	Run the next 8 statements to verify that the test user gets the same permissions
	as the existing superadministrator. Note that over time time the reference users 
	used by the tests may be deprecated. If you get failures you will need to find
	new reference users and update the reference statements (1, 3, 5, 7). */

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "sara.collins@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "SUPERADMINISTRATOR";

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest1@test.com");
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "sara.collins@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "SUPERADMINISTRATOR";
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest1@test.com");
	
SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "sara.collins@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "SUPERADMINISTRATOR";

SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest1@test.com");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "sara.collins@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "SUPERADMINISTRATOR";
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest1@test.com");
	
/* Log in as prism@ucl.ac.uk and make the second test user an admitter. Run the 
	next 8 statements to verify that the test user gets the same permissions as 
	the existing admitter. Note that over time time the reference users used by 
	the tests may be deprecated. If you get failures you will need to find new 
	reference users and update the reference statements (1, 3, 5, 7). */
	
SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "marc.mason@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMITTER";

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest2@test.com");
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "marc.mason@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMITTER";
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest2@test.com");
	
SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "marc.mason@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMITTER";

SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest2@test.com");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "marc.mason@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMITTER";
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest2@test.com");
	
/* Log in as prism@ucl.ac.uk and make the third test user an administrator. Run the 
	next 8 statements to verify that the test user gets the same permissions as the 
	existing administrator. Note that over time time the reference users used by the 
	tests may be deprecated. If you get failures you will need to find new reference 
	users and update the reference statements (1, 3, 5, 7). */
	
SELECT COUNT(application_form_user_role.id) 
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMINISTRATOR"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest3@test.com");
	
SELECT SUM(application_form_user_role.raises_urgent_flag)
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMINISTRATOR"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest3@test.com");
 
SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMINISTRATOR"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest3@test.com");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "ADMINISTRATOR"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest3@test.com");
	
/* Log in as prism@ucl.ac.uk and make the third test user an approver. Run the next 8 
	statements to verify that the test user gets the same permissions as the existing 
	approver. Note that over time time the reference users used by the tests may be 
	deprecated. If you get failures you will need to find new reference users and 
	update the reference statements (1, 3, 5, 7). */
	
SELECT COUNT(application_form_user_role.id) 
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "APPROVER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest4@test.com");
	
SELECT SUM(application_form_user_role.raises_urgent_flag)
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "APPROVER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest4@test.com");
 
SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "APPROVER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest4@test.com");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "APPROVER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest4@test.com");
	
/* Log in as prism@ucl.ac.uk and make the third test user an viewer. Run the next 8 
	statements to verify that the test user gets the same permissions as the existing 
	viewer. Note that over time time the reference users used by the tests may be 
	deprecated. If you get failures you will need to find new reference users and 
	update the reference statements (1, 3, 5, 7). */

SELECT COUNT(application_form_user_role.id) 
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "VIEWER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(id) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest5@test.com");
	
SELECT SUM(application_form_user_role.raises_urgent_flag)
FROM application_form_user_role INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "VIEWER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(raises_urgent_flag) 
FROM application_form_user_role
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest5@test.com");
 
SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "VIEWER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");

SELECT COUNT(application_form_action_required.id) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest5@test.com");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
INNER JOIN application_form
	ON application_form_user_role.application_form_id = application_form.id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "melanie.johnson@ucl.ac.uk")
	AND application_form_user_role.application_role_id = "VIEWER"
	AND application_form.program_id = (
		SELECT id
		FROM program
		WHERE code = "RRDCOMSING01");
	
SELECT SUM(application_form_action_required.raises_urgent_flag) 
FROM application_form_user_role INNER JOIN application_form_action_required
	ON application_form_user_role.id = application_form_action_required.application_form_user_role_id
WHERE registered_user_id = (
	SELECT id
	FROM registered_user 
	WHERE email = "mytest5@test.com");
	
/* The tests pass when in each batch of 8, the result of statement one equals that of statement two, 
	result of three equals that of four, five of six and seven of eight. */