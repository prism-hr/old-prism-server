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
	
});