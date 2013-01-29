$(document).ready(function()
{
	showFirstQualificationEntry();
	$('#applyQualificationsAndReferences').click(function()
	{
		// TODO show ajax progress
//		$('#approvalsection').append('<div class="ajax" />');
		
		var qualificationsSendToPorticoData = collectQualificationsSendToPortico();
		var referencesSendToPorticoData = collectReferencesSendToPortico();
		
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
	            qualificationsSendToPorticoData: JSON.stringify(qualificationsSendToPorticoData),
	            referencesSendToPorticoData: JSON.stringify(referencesSendToPorticoData),
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
//					$('#approvalsection div.ajax').remove();
		      }
		});
	});
		
});
