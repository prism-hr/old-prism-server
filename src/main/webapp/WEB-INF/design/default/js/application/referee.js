$(document).ready(function()
{
	$('#referencesSection .selectpicker').selectpicker();
	$("#acceptTermsRDValue").val("NO");
	
	//limitTextArea();
	
	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
	
	// -------------------------------------------------------------------------------
	// Delete a referee.
	// -------------------------------------------------------------------------------
	$('a[name="deleteRefereeButton"]').click(function()
	{	
		var id = $(this).attr("id").replace("referee_", "");
		$('#ajaxloader').show();
		$.ajax({
			type: 'POST',
			 statusCode: {
				401: function()
				{
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
			url:"/pgadmissions/deleteentity/referee",
			data:{
				id: id	
			}, 
			success:function(data)
			{
				$('#referencesSection').html(data);
			},
			complete: function()
			{
				$('#referee-H2').trigger('click');
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
	
	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsRDCB']").click(function() {
		if ($("#acceptTermsRDValue").val() =='YES'){
			$("#acceptTermsRDValue").val("NO");
		} else {	
			$("#acceptTermsRDValue").val("YES");
			
			refImgCount = 0;
			
		
		}
		});
	
	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#refereeSaveAndCloseButton').click(function() {
		$("span[name='nonAcceptedRD']").html('');
		if ($('#existing-referee-container tr').length == 3)
		{
			$('#refereeForm').each (function(){
			  this.reset();
			});
			$('#refereeCloseButton').trigger('click');
		}
		else
		{
			if ($('#existing-referee-container tr').length < 3 || !isFormEmpty('#referencesSection form'))
			{
				postRefereeData('close');
			}
			else
			{
				$('#refereeCloseButton').trigger('click');
			}
		}
	});
	

	// -------------------------------------------------------------------------------
	// Add a referee.
	// -------------------------------------------------------------------------------
	$('#addReferenceButton').click(function()
	{
		$("span[name='nonAcceptedRD']").html('');
		postRefereeData("add");
	});

	$('#refereeClearButton').click(function()
	{
		$('#ajaxloader').show();
		loadReferenceSection(true);
	});
	

	// -------------------------------------------------------------------------------
	// Edit a referee.
	// -------------------------------------------------------------------------------
	$('a[name="editRefereeLink"]').click(function(){
		
		var id = this.id;
		id = id.replace('referee_', '');	
		
		$.ajax({
		 type: 'GET',
		 statusCode: {
				401: function()
				{
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
			url:"/pgadmissions/update/getReferee",
			data:{
				applicationId:  $('#applicationId').val(),
				refereeId: id,
				message: 'edit',					
				cacheBreaker: new Date().getTime()
			},
			success: function(data)
			{
				
				$('#referencesSection').html(data);
			},
			complete: function()
			{
				$('#referee-H2').trigger('click');
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
	
	addToolTips();
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");
	
});

function postRefereeData(message){
	var acceptedTheTerms;
	if ($("#acceptTermsRDValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
		acceptedTheTerms = true;
	}
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			'addressLocation.address1': $("#ref_address_location1").val(),
			'addressLocation.address2': $("#ref_address_location2").val(),
			'addressLocation.address3': $("#ref_address_location3").val(),
			'addressLocation.address4': $("#ref_address_location4").val(),
			'addressLocation.address5': $("#ref_address_location5").val(),
			messenger: $("#ref_messenger").val(), 
			'addressLocation.domicile': $("#ref_address_country").val(), 
			email: $("#ref_email").val(),			
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),	
			refereeId: $("#refereeId").val(),
			phoneNumber: $("#refPhoneNumber").val(),
			message:message,
			acceptedTerms: acceptedTheTerms
	};

	$('#ajaxloader').show();

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
		url:"/pgadmissions/update/editReferee" , 
		data: $.param(postData),			
		success:function(data) {
				$('#referencesSection').html(data);
				
				var errorCount = $('#referencesSection .alert-error').length;
				var referenceCount = $('#referencesSection table.existing tbody tr').length;				
				
				if(errorCount > 0 || referenceCount < 3){
					markSectionError('#referencesSection');
					$('#referee-H2').trigger('click');
				} else {
					if (message == 'add') {
						$('#referee-H2').trigger('click');
					}
				}
			},
		complete: function(){
		  $('#ajaxloader').fadeOut('fast');
		}
	});
}