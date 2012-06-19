$(document).ready(function()
{
	
	// -----------------------------------------------------------------------------------------
	// Add supervisor
	// -----------------------------------------------------------------------------------------
	$('#addSupervisorBtn').click(function()
	{
		var selectedSupervisors = $('#programSupervisors').val();
		if (selectedSupervisors)
		{
			selectedSupervisors.forEach(function(id)
			{
				var $option = $("#programSupervisors option[value='" + id + "']");
	
				if (!$option.hasClass('selected'))
				{
					var selText = $option.text();
					var category = $option.attr("category");
					$("#programSupervisors option[value='" + id + "']").addClass('selected')
																													 .removeAttr('selected')
																													 .attr('disabled', 'disabled');
					$("#applicationSupervisors").append('<option value="'+ id +'" category="' + category + '">'+ selText + ' (*)</option>');
				}
			});
			$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
		}
	});
	
	
	$('#createSupervisor').click(function() {

		$('#applicationSupervisors option').each(function()
		{
			var ids = $(this).val();
		 	var user = ids.substring(ids.indexOf("|") + 1);
			$('#postSupervisorForm').append("<input name='pendingSupervisor' type='hidden' value='" + user + "'/>");	
		});
		$('#postSupervisorForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
		$('#postSupervisorForm').append("<input name='approvalRoundId' type='hidden' value='" +  $('#approvalRoundId').val() + "'/>");
		$('#postSupervisorForm').append("<input name='firstName' type='hidden' value='" +  $('#newSupervisorFirstName').val() + "'/>");
		$('#postSupervisorForm').append("<input name='lastName' type='hidden' value='" +  $('#newSupervisorLastName').val() + "'/>");
		$('#postSupervisorForm').append("<input name='email' type='hidden' value='" +  $('#newSupervisorEmail').val() + "'/>");		
		
		$('#postSupervisorForm').submit();
		
	});
	

	// -----------------------------------------------------------------------------------------
	// Remove supervisor
	// -----------------------------------------------------------------------------------------
	$('#removeSupervisorBtn').click(function()
	{
		var selectedSupervisors = $('#applicationSupervisors').val();
		if (selectedSupervisors)
		{
			selectedSupervisors.forEach(function(id)
			{
				var selText = $("#applicationSupervisors option[value='" + id + "']").text().replace(' (*)', '');
				$("#applicationSupervisors option[value='" + id + "']").remove();
				$("#programSupervisors option[value='" + id + "']").removeClass('selected')
																												 .removeAttr('disabled');
			});
			$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
		}
	});

	
	
	$('#moveToApprovalBtn').click(function() {
		
		$('#applicationSupervisors option').each(function(){
			 	var ids = $(this).val();
			 	var user = ids.substring(ids.indexOf("|") + 1);
				$('#postApprovalForm').append("<input name='pendingSupervisors' type='hidden' value='" + user + "'/>");	
				$('#postApprovalForm').append("<input name='supervisors' type='hidden' value='" +  $(this).val() + "'/>");
		});
		$('#postApprovalForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");				
		$('#postApprovalForm').submit();
	});
		
		
	
});


