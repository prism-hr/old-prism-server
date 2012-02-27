Feature: ApplicationsList


Scenario: Applicants sees list of own applications
Given anna has applications
	|ApplicationNumber	| SubmissionStatus |
    | 		id_1		| SUBMITTED        |
    | 		id_2		| UNSUBMITTED      |	
And bert has applications
	|ApplicationNumber	| SubmissionStatus |
    | 		id_3		| SUBMITTED        |
When anna views the application list
Then she sees a list containing only applications
	|ApplicationNumber	| SubmissionStatus |
    | 		id_1		| SUBMITTED        |
    | 		id_2		| UNSUBMITTED      |

 Scenario: Administrator sees list of all submitted applications
 Given anna has applications
	|ApplicationNumber	| SubmissionStatus |
    | 		id_1		| SUBMITTED        |
    | 		id_2		| UNSUBMITTED      |	
And bert has applications
	|ApplicationNumber	| SubmissionStatus |
    | 		id_3		| SUBMITTED        |
When charles views the application list
Then she sees a list containing applications
	|ApplicationNumber	|
    | 		id_1		|
    | 		id_3		|
And not containing applications
	|ApplicationNumber	|
    | 		id_2		|
    

Scenario: Reviewer sees list of all applications to which they have been assigned-I
Given anna has applications
	|ApplicationNumber	| SubmissionStatus | Reviewers		|
    | 		id_1		| SUBMITTED        | dorotha, elsie	|   	
And bert has applications
	|ApplicationNumber	| SubmissionStatus | Reviewers 		|
    | 		id_3		| SUBMITTED        | dorotha		|
When elsie views the application list
Then she sees a list containing only applications
	|ApplicationNumber	|
    | 		id_1		|

 Scenario: Reviewer sees list of all applications to which they have been assigned-II   
Given anna has applications
	|ApplicationNumber	| SubmissionStatus | Reviewers		|
    | 		id_1		| SUBMITTED        | dorotha, elsie	|   	
And bert has applications
	|ApplicationNumber	| SubmissionStatus | Reviewers 	|
    | 		id_3		| SUBMITTED        | dorotha		|
When dorotha views the application list
Then she sees a list containing only applications
	|ApplicationNumber	| 
    | 		id_1		| 
    | 		id_3		| 