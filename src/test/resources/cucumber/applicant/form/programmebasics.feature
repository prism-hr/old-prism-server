Feature: Basics of programme section

Scenario: Ian fills in programme section of application form
Given I log in as "ian@test.com"
When I select the latest unsubmitted application applications list
And I save the programme section
Then I see the following validation messages
	|The programme that you are applying for. You do not need to specify a project to apply for a programme|	
	|You must make a selection.|
	|You must make a selection.|

Given the above
When I choose "Full time" as study option
And I choose "Facebook advert" as the how-did-you-find-us-option
And I choose "1-Nov-2013" as start date
And I save the programme section
Then the "programmeDetailsSection" section should collapse

Given the above
When I expand the "programmeDetailsSection" section
Then the study option field should have value "Full time"
And the start date field should have value "01-Nov-2013"
And the how-did-you-find-us-option field should have value "Facebook advert"
