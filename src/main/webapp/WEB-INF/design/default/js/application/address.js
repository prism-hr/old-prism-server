$(document).ready(function(){
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
			
			$(".terms-box").attr('style','');
			$("#addr-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#addr-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#addr-info-bar-div .row span.error-hint").remove();

			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsADValue").val()
			},
			function(data) {
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
			
			$("#addr-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#addr-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
			$("#addr-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
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
		$.get("/pgadmissions/update/getAddress",
				{
					applicationId:  $('#applicationId').val(),					
					cacheBreaker: new Date().getTime()					
				},
				function(data) {
					$('#addressSection').html(data);
				}
		);
	});
	
	addToolTips();

});

function postAddressData(message){
	$.post("/pgadmissions/update/editAddress", { 
		currentAddressLocation: $("#currentAddressLocation").val(),
		currentAddressCountry: $("#currentAddressCountry").val(),
		contactAddressLocation: $("#contactAddressLocation").val(),
		contactAddressCountry: $("#contactAddressCountry").val(),
		applicationId:  $('#applicationId').val(),
		message:message
	},
	function(data) {
		$('#addressSection').html(data);
	});
}

function isSame(){
	var same = false;
	if ($('#sameAddressCB:checked').val() !== undefined) {
		same = true;
	}
	return same;
}