$(document).ready(function(){
$('#refereeSaveButton').click(function(){
		$.post("/pgadmissions/update/addReferee", { 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			relationship: $("#ref_relationship").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 
			addressPostcode: $("#ref_address_postcode").val(), 
			addressCountry: $("#ref_address_country").val(), 
			messengerAddress: $("#ref_messenger_address").val(), 
			messengerType: $("#ref_messenger_type").val(), 
			telephoneType: $("#ref_telephone_type").val(), 
			telephoneNumber: $("#ref_telephone_number").val(), 
			email: $("#ref_email").val(), 
			appId: $("#appId").val(),
			id: $("#id").val(), 
			refereeId: $("#refereeId").val()
		},
				   function(data) {
				     $('#referencesSection').html(data);
				   });
	});

$('a[name="refereeEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('referee_', '');
	var messengerId =  $("#messenger_id").val();
	var telephoneId =  $("#telephone_id").val();
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
});


$('a[name="addTelephoneButton"]').click(function() {
	$('#telephones tr:last').after('<tr><td>Telephone Type</td><td><input type=\"text\" id=\"ref_telephone_type${telephone.id!}\" name=\"ref_telephone_type${telephone.id!}\" value=\"${telephone.telephoneType!}\"/></div></td></tr> <tr><td>Telephone Number</td><td><input type=\"text\" id=\"ref_telephone_number${telephone.id!}\" name=\"ref_telephone_number${telephone.id!}\" value=\"${telephone.telephoneNumber!}\"/></td> </tr>');
});

});