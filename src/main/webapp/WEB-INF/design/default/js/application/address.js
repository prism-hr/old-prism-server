$(document).ready(function(){
	
	var addrImgCount = 0; 
	
	/*
	if($("#currentAddressInvalid").html() !== null || $("#currentAddressCountryInvalid").html() !== null
			|| $("#contactAddressLocationInvalid").html() !== null || $("#contactAddressCountryInvalid").html() !== null
			|| $("#currentAddressInvalidInvalid").html() !== null || $("#currentAddressCountryInvalid").html() !== null){
		$("#addr-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
		$("#addr-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
		if(addrImgCount == 0){
			$("#addr-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
			addrImgCount = addrImgCount + 1;
		}
		addToolTips();
	}
	*/
	
	$("#acceptTermsADValue").val("NO");
	limitTextArea();

	$('#addressCloseButton').click(function(){
		$('#address-H2').trigger('click');
		return false;
	});
	$('#currentAddressLocation').change(function(){
		
		if(isSame()){

			$('#contactAddressLocation').val($('#currentAddressLocation').val());
		}
	});

	$('#currentAddressCountry').change(function(){
		
		if(isSame()){

			$('#contactAddressCountry').val($('#currentAddressCountry').val());
		}
	});
	
	$("input[name*='acceptTermsADCB']").click(function() {
		if ($("#acceptTermsADValue").val() =='YES'){
			$("#acceptTermsADValue").val("NO");
		} else {	
			$("#acceptTermsADValue").val("YES");
			
			/*
			$(".terms-box").attr('style','');
			$("#addr-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#addr-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#addr-info-bar-div .row span.error-hint").remove();
			*/
			addrImgCount = 0;

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
					acceptedTerms: $("#acceptTermsADValue").val()
				},
				success: function(data) {
				}
			});
		}
		});
	
	$('#addressSaveAndAddButton').click(function()
	{
		if( $("#acceptTermsADValue").val() =='NO')
		{ 
			// Highlight the information bar and terms box.
//			var $form = $('#addressSection form');
//			$('.terms-box, .section-info-bar', $form).css({ borderColor: 'red', color: 'red' });
			
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
			
			var $infobar = $('#addr-info-bar-div.section-info-bar');
			$infobar.switchClass("section-info-bar", "section-error-bar", 1);
			if ($infobar)
			{
				$("#addr-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				addrImgCount = addrImgCount + 1;
			}
			addToolTips();
			
		}
		else
		{
			$("span[name='nonAcceptedAD']").html('');
			postAddressData("close");
		}
	});
	
	$("#sameAddressCB").click(function() {
		
		if (isSame()){
			$("#contactAddressLocation").val($("#currentAddressLocation").val());
			$("#contactAddressCountry").val($("#currentAddressCountry").val());
			$("#contactAddressLocation").attr('disabled','disabled');
			$("#contactAddressCountry").attr('disabled','disabled');
			
			$("#add-two-lb").addClass("grey-label");
			$("#country-two-lb").addClass("grey-label");
			$("#add-two-em").addClass("grey-label");
			$("#country-two-em").addClass("grey-label");

			
		} else {		
			$("#contactAddressLocation").val("");
			$("#contactAddressCountry").val("");
			$("#contactAddressLocation").removeAttr('disabled');
			$("#contactAddressCountry").removeAttr('disabled');
			$("#add-two-lb").removeClass("grey-label");
			$("#country-two-lb").removeClass("grey-label");
			$("#add-two-em").removeClass("grey-label");
			$("#country-two-em").removeClass("grey-label");

		}
	});
	

	$('a[name="addressCancelButton"]').click(function(){

		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			  url: "/pgadmissions/update/getAddress",
			  data:{
					applicationId:  $('#applicationId').val(),					
					cacheBreaker: new Date().getTime()					
				}, 
			  success: function(data) {
					$('#addressSection').html(data);
				}	
		});
	});
	
	addToolTips();

});

function postAddressData(message)
{
	$('#addressSection > div').append('<div class="ajax" />');

	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
		url:"/pgadmissions/update/editAddress",
		data:{ 
			currentAddressLocation: $("#currentAddressLocation").val(),
			currentAddressCountry: $("#currentAddressCountry").val(),
			contactAddressLocation: $("#contactAddressLocation").val(),
			contactAddressCountry: $("#contactAddressCountry").val(),
			applicationId:  $('#applicationId').val(),
			message:message
		},
		success: function(data) {
			$('#addressSection').html(data);
			$('#addressSection div.ajax').remove();
			markSectionError('#addressSection');
	
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#addressSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#address-H2').trigger('click');
				}
			}
		}
	});
}

function isSame(){
	var same = false;
	if ($('#sameAddressCB:checked').val() !== undefined) {
		same = true;
	}
	return same;
}