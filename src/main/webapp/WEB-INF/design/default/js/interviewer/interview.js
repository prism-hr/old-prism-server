$(document).ready(function(){
	
//	bindDatePicker('#interviewDate');
	
	$('#createInterviewer').click(function() {
		var postData ={ 
			applicationId : $('#applicationId').val(),
			firstName : $('#newInterviewerFirstName').val(),
			lastName : $('#newInterviewerLastName').val(),
			email : $('#newInterviewerEmail').val()
		};
		
		$.post("/pgadmissions/moveToInterview/createInterviewer", 
			$.param(postData),
			function(data) {
				$('#interviewSection').html(data);
			}
		);
	});
	
	$('#moveToInterviewBtn').click(function() {
		var postData ={ 
			applicationId : $('#applicationId').val(),
			furtherDetails : $('#furtherDetails').val(),
			dueDate : $('#interviewDate').val(),
			locationURL : $('#interviewLocation').val()
		};
		
		$.post("/pgadmissions/moveToInterview/move", 
			$.param(postData),
			function(data) {
				$('#interviewSection').html(data);
			}
		);
	});
});