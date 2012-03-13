$(document).ready(function(){
	$("#phonenumbersref").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$("#messengersref").on("click", "a", function(){	
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
			refereeId: $("#refereeId").val()
		}
	$.post( "/pgadmissions/update/refereeDetails" , $.param(postData) + "&" + $('[input[name="phoneNumbersRef"]').serialize() + "&" + $('[input[name="messengersRef"]').serialize(),
			
				   function(data) {
				     $('#referencesSection').html(data);
				   });
	});

$('a[name="refereeEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('referee_', '');
	var alreadyAppendedTels = false; 
	var alreadyAppendedMessengers = false;
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
	if(alreadyAppendedTels) return; 
	$("span[name='hiddenPhones']").each(function(){
		$('#phonenumbersref').append('<span name="phone_number_ref">'+ $(this).html() + '</span>');
		 
		});
	alreadyAppendedTels = true;
	if(alreadyAppendedMessengers) return; 
	$("span[name='hiddenMessengers']").each(function(){
		$('#messengersref').append('<span name="messenger_ref">'+ $(this).html() + '</span>');
		  
	});
	$(this).unbind('click'); 
	alreadyAppendedMessengers = true; 
});


$('#addPhoneRefButton').on('click', function(){
	if($('#phoneNumberRef').val() =="Number" || $('#phoneNumberRef').val().trim()== ''){
		alert("Please enter phone number");
		return;
	}
	$('#phonenumbersref').append('<span name="phone_number_ref">'+ 
			$('#phoneTypeRef option:selected').text() + " " + $('#phoneNumberRef').val()+ " "+'<a class="button">delete</a>'+
			'<input type="hidden" name="phoneNumbersRef" value=' +"'" + '{"type":"' +  $('#phoneTypeRef').val()+ '", "number":"' + $('#phoneNumberRef').val()+ '"} ' + "'" + "/>"									
			+'<br/></span>');
})

$('#addMessengerRefButton').on('click', function(){
	if($('#messengerAddressRef').val() =="Address" || $('#messengerAddressRef').val().trim()== ''){
		alert("Please enter address");
		return;
	}
	$('#messengersref').append('<span name="messenger_ref">'+ 
			$('#messengerAddressRef').val()+ " "+'<a class="button">delete</a>'+
			'<input type="hidden" name="messengersRef" value=' +"'" + '{"address":"' + $('#messengerAddressRef').val()+ '"} ' + "'" + "/>"									
			+'<br/></span>');
})

// To make uncompleted functionalities disable.
$(".disabledEle").attr("disabled", "disabled");

});