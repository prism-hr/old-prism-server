$(document).ready(function()
{
	
	$('#cancelInterviewFeedbackBtn').click(function()
	{
		window.location.href = "/pgadmissions/interviewFeedback?applicationId=" +  $('#applicationId').val();
	});	
	
	$('#submitInterviewFeedback').click(function()
	{
		if (!validateFeedback())
		{
			return false;
		}
		
		var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
		var onOk    = function()
		{
			$('#interviewForm').append("<input type='hidden' name='type' value='INTERVIEW'/>");		
			if ($('#decline:checked').length > 0)
			{
				$('#interviewForm').append("<input type='hidden' name='decline' value='true'/>");			
			}
			$('#interviewForm').submit();
		};
		var onCancel = function()
		{
			$('div.content-box-inner div.ajax').remove();
		};
		
		modalPrompt(message, onOk, onCancel);
		return false;
	});	
	
	$('#decline').click(function()
	{
		if ($(this).is(':checked'))
		{
			// Comment field.
			$("#comment-lbl").addClass("grey-label");
			$("#comment-lbl em").remove();
			$("#review-comment").val("");
			$("#review-comment").addClass("grey-label");
			$("#review-comment").attr("disabled", "disabled");
			
			// "Willing to supervise" radio buttons.
			$("#supervise-lbl").addClass("grey-label");
			$("#supervise-lbl em").remove();
			$('input[name="willingToSupervise"]').removeAttr("checked")
																					 .addClass("grey-label")
												                   .attr("disabled", "disabled");
			$('input[name="willingToSupervise"]').parent().addClass("grey-label");
			
			// "Suitable for UCL" radio buttons.			
			$("#suitable-lbl").addClass("grey-label");
			$("#suitable-lbl em").remove();
			$('input[name="suitableCandidate"]').removeAttr("checked")
																					.addClass("grey-label")
													                .attr("disabled", "disabled");
			$('input[name="suitableCandidate"]').parent().addClass("grey-label");
			
			//remove validation messages
			$('span[class="invalid"]').html('');
		}
		else
		{
			//comment field
			$("#comment-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#review-comment").removeClass("grey-label").removeAttr("disabled");
			
			// "Willing to interview" radio buttons.
			$("#supervise-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="willingToSupervise"]').addClass("grey-label")
																					 .removeAttr("disabled");
			$('input[name="willingToSupervise"]').parent().removeClass("grey-label");

			// "Suitable for UCL" radio buttons.			
			$("#suitable-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="suitableCandidate"]').removeClass("grey-label")
																					.removeAttr("disabled");
			$('input[name="suitableCandidate"]').parent().removeClass("grey-label");
		}
	});
	
});


function validateFeedback()
{
	var errors = 0;
	$('#interviewForm div.alert-error').remove();
	
	if ($('#interview-comment').val() == '')
	{
		$('#interview-comment').after('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make an entry.</div>');;
		errors++;
	}

	if ($('input[name="suitableCandidateForUcl"]:checked').length == 0)
	{
		$('#field-issuitableucl').append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make a selection.</div>');
		errors++;
	}

	if ($('input[name="suitableCandidateForProgramme"]:checked').length == 0)
	{
		$('#field-issuitableprog').append('<div class="alert alert-error"> <i class="icon-warning-sign"></i> You must make a selection.</div>');
		errors++;
	}
	
	if ($('input[name="willingToSupervise"]:checked').length == 0)
	{
		$('#field-wouldsupervise').append('<div class="alert alert-error"> <i class="icon-warning-sign"> You must make a selection.</div>');
		errors++;
	}
	
	return (errors == 0);
}