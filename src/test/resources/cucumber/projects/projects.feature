Feature: Projects

Scenario: User applies for a project and submits the application
Given there is a list of projects
	|Project Code		| Project title    | Project description	|
    | 		id_1		| project_title1   | project description 1	|
    | 		id_2		| project_title2   | project description 2	|
And user views project list and apply for the first one
When user is logged in as anna
And anna sees the application
Then anna submits the application
And see the application as submitted in the application list
		| SubmissionStatus |Project Code| 
   	    | SUBMITTED        |  id_1  	|
 
 


