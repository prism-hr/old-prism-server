$(document).ready(function(){
	

		$('#interviewDate').attr("readonly", "readonly");	
		$('#interviewDate').datepicker({
			dateFormat: 'dd-M-yy',
			changeMonth: true,
			changeYear: true,
			yearRange: '1900:c+20' });

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
		var idString = getAssignedInterviewerIdString();

		$('#postInterviewerForm').html('');
		$('#postInterviewerForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
		$('#postInterviewerForm').append("<input name='firstName' type='hidden' value='" +  $('#newInterviewerFirstName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='lastName' type='hidden' value='" +  $('#newInterviewerLastName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='email' type='hidden' value='" +  $('#newInterviewerEmail').val() + "'/>");
		$('#postInterviewerForm').append("<input name='unsavedInterviewersRaw' type='hidden' value='" + idString + "'/>");
		
		$('#postInterviewerForm').submit();
		
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
		var timeErrors = false;
		if($('#hours').val() == "" || $('#minutes').val() == "" || $('#format').val() == ""){
			timeErrors = true;
			$("span[name='timeInvalid']").html('You must specify hour, minutes and format. ');
			$("span[name='timeInvalid']").show();
		}
		
		if(!timeErrors){
			$("span[name='timeInvalid']").html('');
			$("span[name='timeInvalid']").hide();
			var timeString = $('#hours').val() + ":" + $('#minutes').val() + " " + $('#format').val();

			$('#postInterviewForm').html('');
			$('#postInterviewForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
			$('#postInterviewForm').append("<input name='furtherDetails' type='hidden' value='" +  $('#furtherDetails').val() + "'/>");
			$('#postInterviewForm').append("<input name='interviewDueDate' type='hidden' value='" +  $('#interviewDate').val() + "'/>");
			$('#postInterviewForm').append("<input name='interviewTime' type='hidden' value='" +  timeString + "'/>");
			$('#postInterviewForm').append("<input name='locationURL' type='hidden' value='" +  $('#interviewLocation').val() + "'/>");
			$('#postInterviewForm').append("<input name='unsavedInterviewersRaw' type='hidden' value='" + idString + "'/>");
		
			$('#postInterviewForm').submit();
		}
		
		
		
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
