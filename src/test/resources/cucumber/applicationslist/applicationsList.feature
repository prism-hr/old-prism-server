Feature: ApplicationsList


Scenario: Applicants sees list of own applications
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
    | 		id_2		| UNSUBMITTED      | CUKEPROJ1	|
And bert has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_3		| SUBMITTED        | CUKEPROJ1	|
When anna views the application list
Then she sees a list containing only applications
	|ApplicationNumber	|
    | 		id_2		| 
    | 		id_1		| 

 Scenario: Administrator sees list of all submitted applications
 Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        |CUKEPROJ1	|
    | 		id_2		| UNSUBMITTED      |CUKEPROJ1	|	
And bert has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_3		| SUBMITTED        |CUKEPROJ1	|
When charles views the application list
Then she sees a list containing applications
	|ApplicationNumber	|
    | 		id_3		|
    | 		id_1		|
And not containing applications
	|ApplicationNumber	|
    | 		id_2		|
    

Scenario: Reviewer sees list of all applications to which they have been assigned-I
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers		|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|dorotha, elsie	|   	
And bert has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| Reviewers 	|
    | 		id_3		| SUBMITTED        |  CUKEPROJ1	| dorotha		|
When elsie views the application list
Then she sees a list containing only applications
	|ApplicationNumber	|
    | 		id_1		|

Scenario: Reviewer sees list of all applications to which they have been assigned-II   
Given anna has applications
	|ApplicationNumber	| SubmissionStatus | Project Code 	|Reviewers		|
    | 		id_1		| SUBMITTED        | CUKEPROJ1		|dorotha, elsie	|   	
And bert has applications
	|ApplicationNumber	| SubmissionStatus | Project Code 	| Reviewers 	|
    | 		id_3		| SUBMITTED        | CUKEPROJ1		| dorotha		|
When dorotha views the application list
Then she sees a list containing only applications
	|ApplicationNumber	| 
    | 		id_3		| 
    | 		id_1		| 
    
Scenario: Approver sees list of all applications to projects in the program on which they are approver 
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |Project Code|
    | 		id_1		| SUBMITTED        | CUKEPROJ1	|
    | 		id_2		| SUBMITTED		   | CUKEPROJ2	|  	
And bert has applications
	|ApplicationNumber	| SubmissionStatus |Project Code| 
    | 		id_3		| SUBMITTED        | CUKEPROJ1	|
When foxy views the application list
Then she sees a list containing only applications
	|ApplicationNumber	|
    | 		id_3		| 
    | 		id_1		|    