$(document).ready(function()
{
	
	var refImgCount = 0;
	
	$("#acceptTermsRDValue").val("NO");
	
	limitTextArea();
	
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
		$('#referencesSection > div').append('<div class="ajax" />');
		$.ajax({
			type: 'POST',
			 statusCode: {
				401: function()
				{
					window.location.reload();
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
				$('#referencesSection div.ajax').remove();
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
			
			/*
			$(".terms-box").attr('style','');
			$("#ref-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#ref-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#ref-info-bar-div .row span.error-hint").remove();
			*/
			refImgCount = 0;
			
			$.ajax({
				type: 'POST',
				 statusCode: {
					  401: function() {
						  window.location.reload();
					  }
				  },
				url:"/pgadmissions/acceptTerms",
				data:{  
					applicationId: $("#applicationId").val(), 
					acceptedTerms: $("#acceptTermsRDValue").val()
				},
				success:function(data) {}
			});
		}
		});
	
	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#refereeSaveAndCloseButton').click(function()
	{
		if ($("#acceptTermsRDValue").val() == 'NO' && !isFormEmpty('#referencesSection form'))
		{ 
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
		{
			$("span[name='nonAcceptedRD']").html('');
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
	});
	

	// -------------------------------------------------------------------------------
	// Add a referee.
	// -------------------------------------------------------------------------------
	$('#addReferenceButton').click(function(){
		if( $('#acceptTermsRDValue').length != 0  && $("#acceptTermsRDValue").val() =='NO'){ 
			//$("span[name='nonAcceptedRD']").html('You must agree to the terms and conditions');
//			$(this).parent().parent().parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#ref-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$infobar.prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				refImgCount = refImgCount + 1;
			}
			addToolTips();
			
		}
		else{
			$("span[name='nonAcceptedRD']").html('');
			postRefereeData("add");
		}
		
	});


	
	$('a[name="refereeCancelButton"]').click(function(){
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
				url:"/pgadmissions/update/getReferee",
				data:{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				success: function(data) {
					$('#referencesSection').html(data);
				}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Edit a referee.
	// -------------------------------------------------------------------------------
	$('a[name="editRefereeLink"]').click(function(){
		var id = this.id;
		id = id.replace('referee_', '');	
		$('#referencesSection > div').append('<div class="ajax" />');
		$.ajax({
		 type: 'GET',
		 statusCode: {
				401: function()
				{
					window.location.reload();
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
				$('#referencesSection div.ajax').remove();
			}
		});
	});
	
	addToolTips();
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");
	
});

function postRefereeData(message){
	
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 		
			messenger: $("#ref_messenger").val(), 
			addressCountry: $("#ref_address_country").val(), 
			email: $("#ref_email").val(),			
			applicationId:  $('#applicationId').val(),
			application:  $('#applicationId').val(),	
			refereeId: $("#refereeId").val(),
			phoneNumber: $("#refPhoneNumber").val(),
			message:message
	}

	$('#referencesSection > div').append('<div class="ajax" />');

	$.ajax({
		type: 'POST',
		statusCode: {
			401: function() {
				window.location.reload();
			 }
		 },
		url:"/pgadmissions/update/editReferee" , 
		data: $.param(postData),			
		success:function(data) {
				$('#referencesSection').html(data);
				markSectionError('#referencesSection');

				if (message == 'close')
				{
					// Close the section only if there are no errors.
					var errorCount = $('#referencesSection .invalid:visible').length;
					var referenceCount = $('#referencesSection table.existing tbody tr').length;
					if (errorCount == 0 && referenceCount >= 3)
					{
						$('#refereeCloseButton').trigger('click');
					}
				}
			},
    complete: function()
    {
      $('#referencesSection div.ajax').remove();
    }
	});
}