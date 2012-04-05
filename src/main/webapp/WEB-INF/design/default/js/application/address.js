$(document).ready(function(){


	$('#addressCloseButton').click(function(){
		$('#address-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$('#addressSaveAndAddButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { 
			currentAddressLocation: $("#currentAddressLocation").val(),
			currentAddressCountry: $("#currentAddressCountry").val(), 
			currentAddressId: $("#currentAddressId").val(),
			contactAddressLocation: $("#contactAddressLocation").val(),
			contactAddressCountry: $("#contactAddressCountry").val(), 
			contactAddressId: $("#contactAddressId").val(),
			id: $("#id").val(), 
			appId: $("#appId").val(),
			add:"Add"
		},
		function(data) {
			$('#addressSection').html(data);
		});
	});
	
	$('#addressSaveAndCloseButton').click(function(){
		$.post("/pgadmissions/update/editAddress", {
			currentAddressLocation: $("#currentAddressLocation").val(),
			currentAddressCountry: $("#currentAddressCountry").val(), 
			currentAddressId: $("#currentAddressId").val(),
			contactAddressLocation: $("#contactAddressLocation").val(),
			contactAddressCountry: $("#contactAddressCountry").val(), 
			contactAddressId: $("#contactAddressId").val(),
			id: $("#id").val(), 
			appId: $("#appId").val()
		},
		function(data) {
			$('#addressSection').html(data);
		});
	});
	
	$('a[name="addressCancelButton"]').click(function(){
		$("#currentAddressId").val("");
		$("#currentAddressLocation").val("");
		$("#currentAddressCountry").val("");
		$("#contactAddressId").val("");
		$("#contactAddressLocation").val("");
		$("#contactAddressCountry").val("");
		$("span[class='invalid']").each(function(){
			$(this).hide();
		});
	});
	
	
	  bindDatePickers();

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