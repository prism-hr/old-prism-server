$(document).ready(function(){
	
	$('#cancelReviewBtn').click(function() {
		window.location.href = "/pgadmissions/reviewFeedback?applicationId=" +  $('#applicationId').val();
	});	
	
	$('#submitReviewFeedback').click(function()
	{
		var onOk = function()
		{
			$('#reviewForm').append("<input type='hidden' name='type' value='REVIEW'/>");
			if ($('#decline:checked').length > 0) {
				$('#reviewForm').append("<input type='hidden' name='decline' value='true'/>");
			}
			var scores = getScores($('#scoring-questions'));
			var scoresInputDef = "<input type='hidden' name='scores' />";
			var scoresInput = $(scoresInputDef).val(scores);
			$('#reviewForm').append(scoresInput);
			$('#reviewForm').submit();
		};
		
		var section = $(this).closest('section.form-rows');
		if (section.length == 1 && section.find('#confirmNextStage').length > 0) {
			onOk();
		}
		else {
			var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
			var onCancel = function(){};
			modalPrompt(message, onOk, onCancel);
		}
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
			
			// "Willing to interview" radio buttons.
			$("#supervise-lbl").addClass("grey-label");
			$("#supervise-lbl em").remove();
			$('input[name="willingToInterview"]').removeAttr("checked").addClass("grey-label").attr("disabled", "disabled");
			$('input[name="willingToInterview"]').parent().addClass("grey-label");
			
			// "Willing to work with applicant" radio buttons.
			$("#supervise-work-lbl").addClass("grey-label");
			$("#supervise-work-lbl em").remove();
			$('input[name="willingToWorkWithApplicant"]').removeAttr("checked").addClass("grey-label").attr("disabled", "disabled");
			$('input[name="willingToWorkWithApplicant"]').parent().addClass("grey-label");
			
			// "Suitable for UCL" radio buttons.			
			$("#suitable-lbl").addClass("grey-label");
			$("#suitable-lbl em").remove();
			$('input[name="suitableCandidate"]').removeAttr("checked").addClass("grey-label").attr("disabled", "disabled");
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
			$('input[name="willingToInterview"]').addClass("grey-label").removeAttr("disabled");
			$('input[name="willingToInterview"]').parent().removeClass("grey-label");
			
			// "Willing to work with applicant" radio buttons.
			$("#supervise-work-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="willingToWorkWithApplicant"]').addClass("grey-label").removeAttr("disabled");
			$('input[name="willingToWorkWithApplicant"]').parent().removeClass("grey-label");
			
			// "Suitable for UCL" radio buttons.			
			$("#suitable-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="suitableCandidate"]').removeClass("grey-label").removeAttr("disabled");
			$('input[name="suitableCandidate"]').parent().removeClass("grey-label");
		}
	});
	
});
