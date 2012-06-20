$(document).ready(function()
{

	// -----------------------------------------------------------------------------------------
	// Add reviewer
	// -----------------------------------------------------------------------------------------
	$('#addReviewerBtn').click(function()
	{
		var selectedReviewers = $('#programReviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var $option = $("#programReviewers option[value='" + id + "']");
	
				if (!$option.hasClass('selected'))
				{
					var selText = $option.text();
					var category = $option.attr("category");
					$("#programReviewers option[value='" + id + "']").addClass('selected')
																													 .removeAttr('selected')
																													 .attr('disabled', 'disabled');
					$("#applicationReviewers").append('<option value="'+ id +'" category="' + category + '">'+ selText + '</option>');
				}
			});
			$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
		}
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
		var selectedReviewers = $('#applicationReviewers').val();
		if (selectedReviewers)
		{
			selectedReviewers.forEach(function(id)
			{
				var selText = $("#applicationReviewers option[value='" + id + "']").text();
				$("#applicationReviewers option[value='" + id + "']").remove();
				//$("#programInterviewers").append('<option value="'+ id +'">'+ selText +'</option>');
				$("#programReviewers option[value='" + id + "']").removeClass('selected')
																												 .removeAttr('disabled');
			});
			$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
		}
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


