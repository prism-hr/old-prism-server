$(document).ready(function()
{
	
	var refImgCount = 0;
	
	$("#acceptTermsRDValue").val("NO");
	
	limitTextArea();
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteRefereeButton"]').click( function(){	
				var id = $(this).attr("id").replace("referee_", "");
				$.post("/pgadmissions/deleteentity/referee",
				{
					id: id	
				}, 
				
				function(data) {
					$('#referencesSection').html(data);
				}	
					
			);
	});
	
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
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsRDValue").val()
			},
			function(data) {
			});
		}
		});
	
	$('#refereeSaveAndCloseButton').click(function()
	{
		if ($("#acceptTermsRDValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#referencesSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#ref-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#ref-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(refImgCount == 0){
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
	
	$('#addReferenceButton').click(function(){
		if( $('#acceptTermsRDValue').length != 0  && $("#acceptTermsRDValue").val() =='NO'){ 
			//$("span[name='nonAcceptedRD']").html('You must agree to the terms and conditions');
//			$(this).parent().parent().parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			$("#ref-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#ref-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			if(refImgCount == 0){
				$("#ref-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
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
		$.get("/pgadmissions/update/getReferee",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#referencesSection').html(data);
				}
		);
	});
	
	$('a[name="editRefereeLink"]').click(function(){
		var id = this.id;
		id = id.replace('referee_', '');	
		$.get("/pgadmissions/update/getReferee",
				{
					applicationId:  $('#applicationId').val(),
					refereeId: id,
					message: 'edit',					
					cacheBreaker: new Date().getTime()
				},
				function(data) {
					$('#referencesSection').html(data);
				}
		);
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

	$.post( "/pgadmissions/update/editReferee" , $.param(postData),			
			function(data) {
				$('#referencesSection').html(data);
				$('#referencesSection div.ajax').remove();
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
			}
	);
}