Feature: ViewApplication


Scenario: Applicants have view option in applications list
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When anna views the application list
Then she can view the application id_1

    
Scenario: Reviewers have view option in applications list
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When elsie views the application list
Then she can view the application id_1

Scenario: Administrators have view option in applications list
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When charles views the application list
Then she can view the application id_1

Scenario: Approvers have view option in applications list
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When foxy views the application list
Then she can view the application id_1

Scenario: Applicants sees their own application
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|    
When anna opens view for application id_1
Then she sees the view of application id_1

 
Scenario: Applicants cannot sees someoneelse's  application
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
When bert opens view for application id_1
Then she gets a resource not found error
    
    
Scenario: Reviewer can see application they are reviewing
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When elsie opens view for application id_1
Then she sees the view of application id_1

Scenario: Reviewer cannot see application they are not reviewing
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When dorotha opens view for application id_1
Then she gets a resource not found error

Scenario: Administrator can see an submitted application
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie			|
When charles opens view for application id_1
Then she sees the view of application id_1

Scenario: Administrator cannot see an unsubmitted application
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| UNSUBMITTED        | CUKEPROJ1	| elsie		|
When charles opens view for application id_1
Then she gets a resource not found error


Scenario: Approver can see an application submitted to a project in their program
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	| elsie		|
When foxy opens view for application id_1
Then she sees the view of application id_1

Scenario: Approver cannot see an application submitted to a project in another program
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| SUBMITTED        | CUKEPROJ2	| elsie		|
When foxy opens view for application id_1
Then she gets a resource not found error

Scenario: Approver cannot see an application to a project in their program that is not yet submitted
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_1		| UNSUBMITTED        | CUKEPROJ1	| elsie		|
When foxy opens view for application id_1
Then she gets a resource not found error