$(document).ready(function()
{
	
	var refImgCount = 0;
	
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
			completed: function()
			{
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
	$('#refereeSaveAndCloseButton').click(function()
	{
		if ($("#acceptTermsRDValue").val() =='NO')
	/*	if ($("#acceptTermsRDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#ref-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$("#ref-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				refImgCount = refImgCount + 1;
			}
			addToolTips();
			
		}
		else
		{*/
			$("span[name='nonAcceptedRD']").html('');
			// Check for 3 references provided.
			if ($('#referencesSection table.existing tbody tr').length == 3)
			{
				$('#refereeForm').each (function(){
				  this.reset();
				});
				$('#refereeCloseButton').trigger('click');
			}
			else
			{
				// Check for a "dirty" referee form. If there is data try to submit it.
				if ($('#referencesSection table.existing tbody tr').length < 3 || !isFormEmpty('#referencesSection form'))
				{
					postRefereeData('close');
				}
				else
				{
					$('#refereeCloseButton').trigger('click');
				}
			}
		//}
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
		$('#ajaxloader').show();
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
			completed: function()
			{
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
			'addressLocation.country': $("#ref_address_country").val(), 
			email: $("#ref_email").val(),			
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),	
			refereeId: $("#refereeId").val(),
			phoneNumber: $("#refPhoneNumber").val(),
			message:message,
			acceptedTerms: acceptedTheTerms
	}

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
				
				var errorCount = $('#referencesSection .alert-error:visible').length;
				var referenceCount = $('#referencesSection table.existing tbody tr').length;				
					
				if (message == 'close' && errorCount == 0 && referenceCount >= 3)
				{
					$('#referee-H2').trigger('click');
				}
				if(errorCount > 0){
					markSectionError('#referencesSection');
				}
		
			},
		complete: function(){
		  $('#ajaxloader').fadeOut('fast');
		}
	});
}