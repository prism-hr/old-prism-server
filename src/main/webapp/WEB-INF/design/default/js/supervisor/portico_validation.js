$(document).ready(function()
{
	$('#applyQualificationsAndReferences').click(function()
	{
		$('#ajaxloader').show();
		
		var qualificationsSendToPortico = collectQualificationsSendToPortico();
		var refereesSendToPorticoData = collectRefereesSendToPortico();
	    var explanationText = $.trim($("#explanationText").val());

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
			    applicationNumber : $('#applicationId').val(),
			    qualificationsSendToPortico : JSON.stringify(qualificationsSendToPortico),
				refereesSendToPortico : JSON.stringify(refereesSendToPorticoData),
			    emptyQualificationsExplanation: explanationText,
			    cacheBreaker: new Date().getTime()
			},
			success: function(data)
			{
				if(data == "OK"){					
					window.location.href = '/pgadmissions/applications?messageCode=move.approval&application=' + $('#applicationId').val();
				
				}else{
					$('#approve-content').html(data);
				}
	            addCounter();
				addToolTips();
				bindRatings();
				bindFileUploaders();
			},
			complete: function()
			{
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});	
});