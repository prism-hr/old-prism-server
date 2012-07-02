$(document).ready(function(){

	
	$('#cancelReviewBtn').click(function() {
		window.location.href = "/pgadmissions/reviewFeedback?applicationId=" +  $('#applicationId').val();
	});	
	
	$('#submitReviewFeedback').click(function() {
						
			$('#reviewForm').append("<input type='hidden' name='type' value='REVIEW'/>");
			
			
			if ($('#decline:checked').length > 0) {
				$('#reviewForm').append("<input type='hidden' name='decline' value='true'/>");
				
			}

			
			$('#reviewForm').submit();
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
			$('input[name="willingToInterview"]').removeAttr("checked")
			                                     .addClass("grey-label")
												 .attr("disabled", "disabled");
			$('input[name="willingToInterview"]').parent().addClass("grey-label");
		
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
			$('input[name="willingToInterview"]').addClass("grey-label")
			                                     .removeAttr("disabled");
			$('input[name="willingToInterview"]').parent().removeClass("grey-label");

			// "Suitable for UCL" radio buttons.			
			$("#suitable-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="suitableCandidate"]').removeClass("grey-label")
			                                    .removeAttr("disabled");
			$('input[name="suitableCandidate"]').parent().removeClass("grey-label");
		}
	});
	
});