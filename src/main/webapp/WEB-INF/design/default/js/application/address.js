$(document).ready(function(){
	$('.selectpicker').selectpicker();
	$("#acceptTermsADValue").val("NO");
	//limitTextArea();

	$('#addressCloseButton').click(function(){
		$('#address-H2').trigger('click');
		return false;
	});
	
	$('#currentAddress1').change(function(){
		if(isSame()){
			$('#contactAddress1').val($('#currentAddress1').val());
		}
	});
	
	$('#currentAddress2').change(function(){
		if(isSame()){
			$('#contactAddress2').val($('#currentAddress2').val());
		}
	});
	
	$('#currentAddress3').change(function(){
		if(isSame()){
			$('#contactAddress3').val($('#currentAddress3').val());
		}
	});
	
	$('#currentAddress4').change(function(){
		if(isSame()){
			$('#contactAddress4').val($('#currentAddress4').val());
		}
	});
	
	$('#currentAddress5').change(function(){
		if(isSame()){
			$('#contactAddress5').val($('#currentAddress5').val());
		}
	});

	$('#currentAddressDomicile').change(function(){
		if(isSame()){
			$('#contactAddressDomicile').val($('#currentAddressDomicile').val());
		}
	});
	
	$("input[name*='acceptTermsADCB']").click(function() {
		if ($("#acceptTermsADValue").val() =='YES'){
			$("#acceptTermsADValue").val("NO");
		} else {	
			$("#acceptTermsADValue").val("YES");
			addrImgCount = 0;
		}
	});
	
	$('#addressSaveAndAddButton').click(function()	{
			$("span[name='nonAcceptedAD']").html('');
			postAddressData("close");
	});
	
	$("#sameAddressCB").click(function() {
		if (isSame()){
			disableContactAddress();
		} else {		
			enableContactAddress(true);
		}
	});
	
	$('#addressClearButton').click(function(){
		loadAddresSection(true);	
	});

	$('a[name="addressCancelButton"]').click(function(){
		$.ajax({
			 type: 'GET',
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
	
	if (isSame()){
		disableContactAddress();
	} else {		
		enableContactAddress(false);
	}
});

function postAddressData(message)
{
	var acceptedTheTerms;
	if ($("#acceptTermsADValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
		acceptedTheTerms = true;
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
		url:"/pgadmissions/update/editAddress",
		data:{ 
			currentAddress1: $("#currentAddress1").val(),
			currentAddress2: $("#currentAddress2").val(),
			currentAddress3: $("#currentAddress3").val(),
			currentAddress4: $("#currentAddress4").val(),
			currentAddress5: $("#currentAddress5").val(),
			currentAddressDomicile: $("#currentAddressDomicile").val(),
			contactAddress1: $("#contactAddress1").val(),
			contactAddress2: $("#contactAddress2").val(),
			contactAddress3: $("#contactAddress3").val(),
			contactAddress4: $("#contactAddress4").val(),
			contactAddress5: $("#contactAddress5").val(),
			contactAddressDomicile: $("#contactAddressDomicile").val(),
			applicationId:  $('#applicationId').val(),
			message:message,
			acceptedTerms: acceptedTheTerms
		},
		success: function(data) {
			$('#addressSection').html(data);
		
	
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#addressSection .alert-error').length;
				if (errorCount > 0)
				{
					$('#address-H2').trigger('click');
					markSectionError('#addressSection');
				}
			}
		},
		complete: function(){
			$('#ajaxloader').fadeOut('fast');
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

function disableContactAddress() {
	$("#contactAddress1").val($("#currentAddress1").val());
	$("#contactAddress2").val($("#currentAddress2").val());
	$("#contactAddress3").val($("#currentAddress3").val());
	$("#contactAddress4").val($("#currentAddress4").val());
	$("#contactAddress5").val($("#currentAddress5").val());
	$("#contactAddressDomicile").val($("#currentAddressDomicile").val());
	$("#contactAddress1").attr('disabled','disabled');
	$("#contactAddress2").attr('disabled','disabled');
	$("#contactAddress3").attr('disabled','disabled');
	$("#contactAddress4").attr('disabled','disabled');
	$("#contactAddress5").attr('disabled','disabled');
	$("#contactAddressDomicile").attr('disabled','disabled');
	$("#contactAddressDomicile").selectpicker('refresh');
	
	$("#add-two-lb-1").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#add-two-lb-2").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#add-two-lb-3").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#add-two-lb-4").addClass("grey-label").parent().find('.hint').addClass("grey");
	
	$("#country-two-lb").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#add-two-em").addClass("grey-label").parent().find('.hint').addClass("grey");
	$("#country-two-em").addClass("grey-label").parent().find('.hint').addClass("grey");
}

function enableContactAddress(clear) {
    $("#contactAddressLocation").val("");
	//$("#contactAddressCountry").val("");

	if (clear === true) {
		$("#contactAddressDomicile").val("");
	    $("#contactAddress1").val("");
	    $("#contactAddress2").val("");
	    $("#contactAddress3").val("");
	    $("#contactAddress4").val("");
	    $("#contactAddress5").val("");
	}
    
	$("#contactAddressLocation").removeAttr('disabled');
	$("#contactAddressDomicile").removeAttr('disabled');
	$("#add-two-lb-1").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#add-two-lb-2").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#add-two-lb-3").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#add-two-lb-4").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#country-two-lb").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#add-two-em").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	$("#country-two-em").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	
	$("#contactAddress1").removeAttr('disabled','disabled');
	$("#contactAddress2").removeAttr('disabled','disabled');
	$("#contactAddress3").removeAttr('disabled','disabled');
	$("#contactAddress4").removeAttr('disabled','disabled');
	$("#contactAddress5").removeAttr('disabled','disabled');
	$("#contactAddressDomicile").removeAttr('disabled','disabled');
	$("#contactAddressDomicile").selectpicker('refresh');
}