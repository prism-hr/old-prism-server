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
			 resetReviewersErrors();
		}
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
				$("#programReviewers option[value='" + id + "']").removeClass('selected') .removeAttr('disabled');
			});
			$('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
		}
		resetReviewersErrors();
	});

	
	
});