Feature: Approve or reject an application

Scenario:Administrator has reject but not accept option on applications list page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
When charles views the application list
Then she can reject the application id_1
And she can not approve the application id_1

	
Scenario:Approver has reject and accept option on applications list page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
When foxy views the application list
Then she can reject the application id_1
And she can approve the application id_1


Scenario:Reviewer does not have accept or reject option on applications list page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|Reviewers	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|dorotha	|
When dorotha views the application list
Then she can not reject the application id_1
And she can not approve the application id_1

Scenario:Applicant does not have accept or reject option on applications list page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|
When anna views the application list
Then she can not reject the application id_1
And she can not approve the application id_1


Scenario:Administrator has reject option on applications management page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
When charles views the applications management page for application id_1
Then she sees reject option
And she does not see approve option

Scenario:Approver has reject and approve option on applications management page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
When foxy views the applications management page for application id_1
Then she sees reject option
And she sees approve option

Scenario:Reviewer has neither reject nor approve option on applications management page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|Reviewers	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|dorotha	|
When dorotha views the applications management page for application id_1
Then she does not see reject option
And  she does not see approve option

Scenario:Applicant cannot see applications management page
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|Reviewers	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|dorotha	|
When anna views the applications management page for application id_1
Then she gets an access denied error


Scenario:Reviewer cannot see applications management page for application that is not submitted
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code		|Reviewers	|
    | 		id_1		| UNSUBMITTED        | CUKEPROJ1		|elsie	    |
When elsie views the applications management page for application id_1
Then she gets a resource not found error

Scenario:Administrator cannot see applications management page for application that is not submitted
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|
    | 		id_1		| UNSUBMITTED        | CUKEPROJ1	|
When charles views the applications management page for application id_1
Then she gets a resource not found error

Scenario:Approver cannot see applications management page for application that is not submitted
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|
    | 		id_1		| UNSUBMITTED        | CUKEPROJ1	|
When foxy views the applications management page for application id_1
Then she gets a resource not found error


Scenario:Reviewer cannot see applications management page for application they are not reviewing
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|Reviewers	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|dorotha	|
When elsie views the applications management page for application id_1
Then she gets a resource not found error


Scenario:Approver cannot see applications management page for application to project not in their program
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code	|
    | 		id_1		| SUBMITTED        | CUKEPROJ2		|
When foxy views the applications management page for application id_1
Then she gets a resource not found error

