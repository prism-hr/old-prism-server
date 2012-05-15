$(document).ready(function(){
	
	
	$('#addInterviewerBtn').click(function() {
		var selectedReviewers = $('#programInterviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#programInterviewers option[value='" + id + "']").text();
			$("#programInterviewers option[value='" + id + "']").remove();
			$("#applicationInterviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
	});
	
	$('#createInterviewer').click(function() {
		var idString = getAssignedInterviewerIdString();
		var postData ={ 
			applicationId : $('#applicationId').val(),
			firstName : $('#newInterviewerFirstName').val(),
			lastName : $('#newInterviewerLastName').val(),
			email : $('#newInterviewerEmail').val(),
			assignOnly: $('#assignOnly').val(),
			unsavedInterviewersRaw : idString
		};
		
		$.post("/pgadmissions/interview/moveToInterview/createInterviewer", 
			$.param(postData),
			function(data) {
				$('#interviewSection').html(data);
			}
		);
	});
	
	$('#removeInterviewerBtn').click(function() {
		var selectedReviewers = $('#applicationInterviewers').val();
		selectedReviewers.forEach(function(id) {
			var selText = $("#applicationInterviewers option[value='" + id + "']").text();
			$("#applicationInterviewers option[value='" + id + "']").remove();
			$("#interviewers").append('<option value="'+ id +'">'+ selText +'</option>');
		});
	});

	
	
	$('#moveToInterviewBtn').click(function() {
		var idString = getAssignedInterviewerIdString();
		var postData ={ 
			applicationId : $('#applicationId').val(),
			furtherDetails : $('#furtherDetails').val(),
			interviewDueDate : $('#interviewDate').val(),
			locationURL : $('#interviewLocation').val(),
			assignOnly: $('#assignOnly').val(),
			unsavedInterviewersRaw : idString
		};
		
		$.post("/pgadmissions/interview/moveToInterview/move", 
			$.param(postData),
			function(data) {
				$('#interviewSection').html(data);
			}
		);
	});
});

function getAssignedInterviewerIdString() {
	var assignedInterviewers = document.getElementById("applicationInterviewers").options;
	var revIds = "";
	for(i = 0; i < assignedInterviewers.length; i = i + 1) {
		if( i != 0) {
			revIds += "|";
		}
		revIds += assignedInterviewers.item(i).value;
	}
	return revIds;
}
//	bindDatePicker('#interviewDate');