$(document).ready(function()
{
	$('#applyQualificationsAndReferences').click(function()
	{
		// TODO show ajax progress
		$('#qualificationsSection').append('<div class="ajax" />');
		$('#referencesSection').append('<div class="ajax" />');
		
		var qualificationsSendToPorticoData = collectQualificationsSendToPortico();
		var referencesSendToPorticoData = collectReferencesSendToPortico();
		var sendToPorticoData = {
				qualifications : qualificationsSendToPorticoData.qualifications,
				referees : referencesSendToPorticoData.referees
		};
		var explanation = $("#explanationText").val();
		
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
			    sendToPorticoData: JSON.stringify(sendToPorticoData),
			    explanation: explanation,
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
