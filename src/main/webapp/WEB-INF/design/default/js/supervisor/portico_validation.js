$(document).ready(function()
{
	$('#applyQualificationsAndReferences').click(function()
	{
		// TODO show ajax progress
		$('#qualificationsSection').append('<div class="ajax" />');
		$('#referencesSection').append('<div class="ajax" />');
		
		var qualificationsSendToPortico = collectQualificationsSendToPortico();
		var refereesSendToPorticoData = collectRefereesSendToPortico();
		var explanation = $("#explanationText").val();

		if(qualificationsSendToPortico.length > 0){
			// explanation doesn't matter when at least one qualification is selected
			explanation = "";
		}

		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			url: "/pgadmissions/approval/applyPorticoData",
			data :  {
			    applicationId : $('#applicationId').val(),
			    qualificationsSendToPortico : JSON.stringify(qualificationsSendToPortico),
				refereesSendToPortico : JSON.stringify(refereesSendToPorticoData),
			    emptyQualificationsExplanation: explanation,
			    cacheBreaker: new Date().getTime()
			},
			success: function(data)
			{	
				if(data == "OK"){					
					window.location.href = '/pgadmissions/applications?messageCode=move.approval&application=' + $('#applicationId').val();
				
				}else{
					$('#approve-content').html(data);
				}
				addToolTips();
			},
			complete: function()
			{
				$('#qualificationsSection div.ajax').remove();
				$('#referencesSection div.ajax').remove();
			}
		});
	});
		
});
