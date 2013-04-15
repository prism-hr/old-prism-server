Feature: supervisor part of programme section

Scenario: Ian fills in supervisor partof application form
Given I log in as "ian@test.com"
When I select the latest unsubmitted application applications list
And I click the "addSupervisorButton" button
Then I see the following validation messages
	|You must make an entry.|
	|You must make an entry.|
	|You must enter a valid email address.|
	
Given the above
When I enter "bob" into the supervisor firstname field
And I enter "Smith" into the supervisor lastname field
And I enter "notvalid" into the supervisor email field
When I click the "addSupervisorButton" button
Then I see the following validation messages
	|You must enter a valid email address.|

Given the above
When I enter "bob" into the supervisor firstname field
And I enter "Smith" into the supervisor lastname field
And I enter "bobsmith@test.com" into the supervisor email field
And I select "no" to is-supervisor-aware
When I click the "addSupervisorButton" button
Then I see the following supervisor table
	|bob smith (bobsmith@test.com)|

Given the above
When I enter "jane" into the supervisor firstname field
And I enter "doe" into the supervisor lastname field
And I enter "doe@test.com" into the supervisor email field
And I select "yes" to is-supervisor-aware
When I click the "addSupervisorButton" button
Then I see the following supervisor table
	|bob smith (bobsmith@test.com)|
	|jane doe (doe@test.com)|

Given the above
And I save the programme section
Then the "programmeDetailsSection" section should collapse

Given the above
When I expand the "programmeDetailsSection" section
Then I see the following supervisor table
	|bob smith (bobsmith@test.com)|
	|jane doe (doe@test.com)|


Given the above
When I click the edit icon on row number 1
Then I see "bob" in the supervisor firstname field
And I see "smith" in the supervisor lastName field
And I see "bobsmith@test.com" in the supervisor email field

Given the above
When I enter "Jones" into the supervisor lastname field
When I click the "addSupervisorButton" button
Then I see the following supervisor table
	|bob|Jones|(bobsmith@test.com)|
	|jane|doe|(doe@test.com)|


Given the above
When I click the delete icon on row number 2
Then I see the following supervisor table
	|bob|Jones|(bobsmith@test.com)|
