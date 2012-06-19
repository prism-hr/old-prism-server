$(document).ready(function()
{
	
	$('#interviewDate').attr("readonly", "readonly");	
	$('#interviewDate').datepicker({
		dateFormat: 'dd-M-yy',
		changeMonth: true,
		changeYear: true,
		yearRange: '1900:c+20' });


	// -----------------------------------------------------------------------------------------
	// Add interviewer
	// -----------------------------------------------------------------------------------------
	$('#addInterviewerBtn').click(function()
	{
		var selectedReviewers = $('#programInterviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var $option = $("#programInterviewers option[value='" + id + "']");
	
				if (!$option.hasClass('selected'))
				{
					var selText = $option.text();
					var category = $option.attr("category");
					$("#programInterviewers option[value='" + id + "']").addClass('selected')
																															.removeAttr('selected')
																															.attr('disabled', 'disabled');
					$("#applicationInterviewers").append('<option value="'+ id +'" category="' + category + '">'+ selText + '</option>');
				}
			});
			//$('#programInterviewers').attr("size", $('#programInterviewers option').size() + 1);
			$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
		}
	});
	
	
	$('#createInterviewer').click(function() {
		$('#applicationInterviewers option').each(function(){
			$('#postInterviewerForm').append("<input name='pendingInterviewer' type='hidden' value='" +  $(this).val() + "'/>");	
		});
		$('#postInterviewerForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
		$('#postInterviewerForm').append("<input name='interviewId' type='hidden' value='" +  $('#interviewId').val() + "'/>");
		$('#postInterviewerForm').append("<input name='firstName' type='hidden' value='" +  $('#newInterviewerFirstName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='lastName' type='hidden' value='" +  $('#newInterviewerLastName').val() + "'/>");
		$('#postInterviewerForm').append("<input name='email' type='hidden' value='" +  $('#newInterviewerEmail').val() + "'/>");		
		$('#postInterviewerForm').submit();
	});
	
	
	// -----------------------------------------------------------------------------------------
	// Remove interviewer
	// -----------------------------------------------------------------------------------------
	$('#removeInterviewerBtn').click(function()
	{
		var selectedReviewers = $('#applicationInterviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var selText = $("#applicationInterviewers option[value='" + id + "']").text();
				$("#applicationInterviewers option[value='" + id + "']").remove();
				//$("#programInterviewers").append('<option value="'+ id +'">'+ selText +'</option>');
				$("#programInterviewers option[value='" + id + "']").removeClass('selected')
																														 .removeAttr('disabled');
			});
			//$('#programInterviewers').attr("size", $('#programInterviewers option').size() + 1);
			$('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
		}
	});

	
	
	$('#moveToInterviewBtn').click(function()
	{
	
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
	
			$('#applicationInterviewers option').each(function(){
				$('#postInterviewForm').append("<input name='pendingInterviewer' type='hidden' value='" +  $(this).val() + "'/>");	
				$('#postInterviewForm').append("<input name='interviewers' type='hidden' value='" +  $(this).val() + "'/>");
			});
			$('#postInterviewForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
			$('#postInterviewForm').append("<input name='furtherDetails' type='hidden' value='" +  $('#furtherDetails').val() + "'/>");
			$('#postInterviewForm').append("<input name='interviewDueDate' type='hidden' value='" +  $('#interviewDate').val() + "'/>");
			$('#postInterviewForm').append("<input name='interviewTime' type='hidden' value='" +  timeString + "'/>");
			$('#postInterviewForm').append("<input name='locationURL' type='hidden' value='" +  $('#interviewLocation').val() + "'/>");				
			$('#postInterviewForm').submit();
		}
		
		
		
	});
});

function getAssignedInterviewerIdString()
{
	var assignedInterviewers = document.getElementById("applicationInterviewers").options;
	var revIds = "";
	for (i = 0; i < assignedInterviewers.length; i = i + 1)
	{
		if( i != 0)
		{
			revIds += "|";
		}
		revIds += assignedInterviewers.item(i).value;
	}
	return revIds;
}
