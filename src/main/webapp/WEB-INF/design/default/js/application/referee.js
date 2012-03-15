$(document).ready(function(){
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
	$("#phonenumbersref").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$("#messengersref").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$('#delBtn').on('click', function(){
		$(this).parent("span").remove();
	});
$('#refereeSaveButton').click(function(){
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			relationship: $("#ref_relationship").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 
			addressPostcode: $("#ref_address_postcode").val(), 
			addressCountry: $("#ref_address_country").val(), 
			email: $("#ref_email").val(), 
			application: $("#appId").val(),
			refereeId: $("#refereeId").val(),
			phoneNumbersRef: "",
			messengersRef: ""
		}
	$.post( "/pgadmissions/update/refereeDetails" , $.param(postData) + "&" + $('#phonenumbersref input[name="phoneNumbersRef"]').serialize() + "&" + $('#messengersref input[name="messengersRef"]').serialize(),
			
				   function(data) {
				     $('#referencesSection').html(data);
				   });
	});

$('#refereeSaveAndAddButton').click(function(){
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			relationship: $("#ref_relationship").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 
			addressPostcode: $("#ref_address_postcode").val(), 
			addressCountry: $("#ref_address_country").val(), 
			email: $("#ref_email").val(), 
			application: $("#appId").val(),
			refereeId: $("#refereeId").val(),
			phoneNumbersRef: "",
			messengersRef: ""
	}
	$.post( "/pgadmissions/update/refereeDetails" , $.param(postData) + "&" + $('#phonenumbersref input[name="phoneNumbersRef"]').serialize() + "&" + $('#messengersref input[name="messengersRef"]').serialize(),
			
			function(data) {
		$('#referencesSection').html(data);
	});
});

$('a[name="refereeCancelButton"]').click(function(){
	$("#ref_firstname").val("");
	$("#ref_lastname").val("");
	$("#ref_relationship").val("");
	$("#ref_employer").val("");
	$("#ref_position").val("");
	$("#ref_address_location").val("");
	$("#ref_address_postcode").val("");
	$("#ref_address_country").val("");
	$("#ref_email").val("");
	$("#ref_address_country").val("");
});

$('a[name="refereeEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('referee_', '');
	$("#refereeId").val($('#'+id+"_refereeId").val());
	$("#ref_firstname").val($('#'+id+"_firstname").val());
	$("#ref_lastname").val($('#'+id+"_lastname").val());
	$("#ref_relationship").val($('#'+id+"_relationship").val());
	$("#ref_employer").val($('#'+id+"_jobEmployer").val());
	$("#ref_position").val($('#'+id+"_jobTitle").val());
	$("#ref_address_location").val($('#'+id+"_addressLocation").val());
	$("#ref_address_postcode").val($('#'+id+"_addressPostcode").val());
	$("#ref_address_country").val($('#'+id+"_addressCountry").val());
	$("#ref_email").val($('#'+id+"_email").val());
	
	$('#phonenumbersref').html("");
	$("span[name='"+id+"_hiddenPhones']").each(function(){
		$('#phonenumbersref').append('<span name="phone_number_ref">'+ $(this).html() + '</span>');
	});
	$('#messengersref').html("");
	$("span[name='"+id+"_hiddenMessengers']").each(function(){
		$('#messengersref').append('<span name="messenger_ref">'+ $(this).html() + '</span>');
		  
	});
});


$('#addPhoneRefButton').on('click', function(){
	if($('#phoneNumberRef').val() =="Number" || $('#phoneNumberRef').val().trim()== ''){
		alert("Please enter phone number");
		return;
	}
	$('#phonenumbersref').append('<span name="phone_number_ref">'+ 
			$('#phoneTypeRef option:selected').text() + " " + $('#phoneNumberRef').val()+ " "+'<a class="button-delete">delete</a>'+
			'<input type="hidden" name="phoneNumbersRef" value=' +"'" + '{"type":"' +  $('#phoneTypeRef').val()+ '", "number":"' + $('#phoneNumberRef').val()+ '"} ' + "'" + "/>"									
			+'<br/></span>');
})

$('#addMessengerRefButton').on('click', function(){
	if($('#messengerAddressRef').val() =="Address" || $('#messengerAddressRef').val().trim()== ''){
		alert("Please enter address");
		return;
	}
	$('#messengersref').append('<span name="messenger_ref">'+ 
			$('#messengerAddressRef').val()+ " "+'<a class="button-delete">delete</a>'+
			'<input type="hidden" name="messengersRef" value=' +"'" + '{"address":"' + $('#messengerAddressRef').val()+ '"} ' + "'" + "/>"									
			+'<br/></span>');
})

// To make uncompleted functionalities disable.
$(".disabledEle").attr("disabled", "disabled");

});