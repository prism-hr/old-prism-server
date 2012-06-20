$(document).ready(function()
{

	// -----------------------------------------------------------------------------------------
	// Add reviewer
	// -----------------------------------------------------------------------------------------
	$('#addReviewerBtn').click(function()
	{
			$("#programReviewers option").each(function()
			{
					$("#programReviewers option[value='" + $(this).val() + "']").addClass('selected').attr('disabled', 'disabled');
					$("#applicationReviewers").append('<option value="'+ $(this).val() +'" category="' + $(this).attr("category") + '">'+ $(this).html() + '</option>');
			});
			$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
	});
	
	
	$('#createReviewer').click(function()
	{
		$('#applicationReviewers option').each(function()
		{
			var ids = $(this).val();
		 	var user = ids.substring(ids.indexOf("|") + 1);
			$('#postReviewerForm').append("<input name='pendingReviewer' type='hidden' value='" + user + "'/>");	
		});
		$('#postReviewerForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");		
		$('#postReviewerForm').append("<input name='firstName' type='hidden' value='" +  $('#newReviewerFirstName').val() + "'/>");
		$('#postReviewerForm').append("<input name='lastName' type='hidden' value='" +  $('#newReviewerLastName').val() + "'/>");
		$('#postReviewerForm').append("<input name='email' type='hidden' value='" +  $('#newReviewerEmail').val() + "'/>");		
		
		$('#postReviewerForm').submit();
		
	});
	

	// -----------------------------------------------------------------------------------------
	// Remove reviewer
	// -----------------------------------------------------------------------------------------
	$('#removeReviewerBtn').click(function()
	{
		$("#applicationReviewers option").each(function()
				{
						$("#applicationReviewers option[value='" + $(this).val() + "']").remove();
						$("#programReviewers option[value='" + $(this).val() + "']").removeClass('selected');
				});
			$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
	});

	
	
	$('#moveToReviewBtn').click(function() {
		
		$('#applicationReviewers option').each(function(){
			 	var ids = $(this).val();
			 	var user = ids.substring(ids.indexOf("|") + 1);
				$('#postReviewForm').append("<input name='pendingReviewer' type='hidden' value='" + user + "'/>");	
				$('#postReviewForm').append("<input name='reviewers' type='hidden' value='" +  $(this).val() + "'/>");
		});
		$('#postReviewForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");				
		$('#postReviewForm').submit();
	});
		
		
	
});


