$(document).ready(function(){
	
	var addrImgCount = 0; 
	
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
	$('#addressSection > div').append('<div class="ajax" />');

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
			currentAddressCountry: $("#currentAddressCountry").val(),
			contactAddress1: $("#contactAddress1").val(),
			contactAddress2: $("#contactAddress2").val(),
			contactAddress3: $("#contactAddress3").val(),
			contactAddress4: $("#contactAddress4").val(),
			contactAddress5: $("#contactAddress5").val(),
			contactAddressCountry: $("#contactAddressCountry").val(),
			applicationId:  $('#applicationId').val(),
			message:message,
			acceptedTerms: acceptedTheTerms
		},
		success: function(data) {
			$('#addressSection').html(data);
		
	
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#addressSection .alert-error:visible').length;
				if (errorCount == 0)
				{
					$('#address-H2').trigger('click');
				}else{
					markSectionError('#addressSection');
				}
			}
		},
    complete: function()
    {
			$('#addressSection div.ajax').remove();
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
	$("#contactAddressCountry").val($("#currentAddressCountry").val());
	$("#contactAddress1").attr('disabled','disabled');
	$("#contactAddress2").attr('disabled','disabled');
	$("#contactAddress3").attr('disabled','disabled');
	$("#contactAddress4").attr('disabled','disabled');
	$("#contactAddress5").attr('disabled','disabled');
	$("#contactAddressCountry").attr('disabled','disabled');
	
	$("#add-two-lb-1").addClass("grey-label");
	$("#add-two-lb-2").addClass("grey-label");
	$("#add-two-lb-3").addClass("grey-label");
	$("#add-two-lb-4").addClass("grey-label");
	
	$("#country-two-lb").addClass("grey-label");
	$("#add-two-em").addClass("grey-label");
	$("#country-two-em").addClass("grey-label");
}

function enableContactAddress(clear) {
    $("#contactAddressLocation").val("");
	//$("#contactAddressCountry").val("");

	if (clear === true) {
		$("#contactAddressCountry").val("");
	    $("#contactAddress1").val("");
	    $("#contactAddress2").val("");
	    $("#contactAddress3").val("");
	    $("#contactAddress4").val("");
	    $("#contactAddress5").val("");
	}
    
	$("#contactAddressLocation").removeAttr('disabled');
	$("#contactAddressCountry").removeAttr('disabled');
	$("#add-two-lb-1").removeClass("grey-label");
	$("#add-two-lb-2").removeClass("grey-label");
	$("#add-two-lb-3").removeClass("grey-label");
	$("#add-two-lb-4").removeClass("grey-label");
	$("#country-two-lb").removeClass("grey-label");
	$("#add-two-em").removeClass("grey-label");
	$("#country-two-em").removeClass("grey-label");
	
	$("#contactAddress1").removeAttr('disabled','disabled');
	$("#contactAddress2").removeAttr('disabled','disabled');
	$("#contactAddress3").removeAttr('disabled','disabled');
	$("#contactAddress4").removeAttr('disabled','disabled');
	$("#contactAddress5").removeAttr('disabled','disabled');
	$("#contactAddressCountry").removeAttr('disabled','disabled');
}