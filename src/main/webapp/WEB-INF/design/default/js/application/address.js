$(document).ready(function(){
	
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
	
	$('#addressSaveAndAddButton').click(function(){
		postAddressData("close");
	});
	
	$("#sameAddressCB").click(function() {
		
		if (isSame()){
			$("#contactAddressLocation").val($("#currentAddressLocation").val());
			$("#contactAddressCountry").val($("#currentAddressCountry").val());
			$("#contactAddressLocation").attr('disabled','disabled');
			$("#contactAddressCountry").attr('disabled','disabled');
			
			$("#add-two-lb").addClass("grey-label");
			$("#country-two-lb").addClass("grey-label");
			
		} else {		
			$("#contactAddressLocation").val("");
			$("#contactAddressCountry").val("");
			$("#contactAddressLocation").removeAttr('disabled');
			$("#contactAddressCountry").removeAttr('disabled');
			$("#add-two-lb").removeClass("grey-label");
			$("#country-two-lb").removeClass("grey-label");
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

	//open/close
	var $header  =$('#address-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
		
		
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