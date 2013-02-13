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
			$('#programReviewers').each(function(index)
			{
				var id = $(this).attr("value");
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
			$('#applicationReviewers').each(function(index)
			{
				var id = $(this).attr("value");
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