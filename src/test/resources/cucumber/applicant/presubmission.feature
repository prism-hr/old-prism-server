Feature: Fill in application form

Scenario: Ian fills in application for computer science
Given I create an application to program "RRDCOMSING01" as "ian@test.com"
When I submit the application
Then the terms and conditions field is red

Given the above
When I click the terms and conditions field
And I submit the application
Then I see the following validation messages		
	|Some required fields are missing, please review your application form|
	|The programme that you are applying for. You do not need to specify a project to apply for a programme|	
	|Supply your personal details here. Some fields have been completed for you based upon what we know about you already.|
	|Supply your address details here.|
	|Supply details of your referees here. You must supply details of three referees.|
	|Supply your supporting documents here.|
	|Supply additional information relevant to your application here.|
	
