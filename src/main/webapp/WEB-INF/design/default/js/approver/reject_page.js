$(document).ready(function() {

	$('input:radio[name=rejectionReason]').click(function() {
	
		var postData ={ 
				applicationId : $('#applicationId').val(),
				rejectionReason : $(this).val()
		};
		
		if( $('#includeProspectusLink:checked').val() !== undefined ){
			postData.includeProspectusLink = true;
		}else{
			postData.includeProspectusLink = false;
		}
		
		$.post("/pgadmissions/rejectApplication/rejectionText", 
				$.param(postData),
				function(data) {
					$('#emailText').html(data);
				}
		);
	});
	
	$('#includeProspectusLink').click(function() {
		
	
		var postData ={ 
				applicationId : $('#applicationId').val()	
		};
		
		if( $('input:radio[name=rejectionReason]:checked').val() !== undefined ){
			postData.rejectionReason = $('input:radio[name=rejectionReason]:checked').val() ;
		}
		
		if( $('#includeProspectusLink:checked').val() !== undefined ){
			postData.includeProspectusLink = true;
		}else{
			postData.includeProspectusLink = false;
		}
		
		$.post("/pgadmissions/rejectApplication/rejectionText", 
				$.param(postData),
				function(data) {
					$('#emailText').html(data);
				}
		);
	});
	

});

